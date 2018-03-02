package br.com.ajeferson.combat.view.view.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import br.com.ajeferson.combat.R
import br.com.ajeferson.combat.databinding.ActivityGameBinding
import br.com.ajeferson.combat.view.service.model.ChatMessage
import br.com.ajeferson.combat.view.service.model.Coordinates
import br.com.ajeferson.combat.view.view.adapter.BoardRecyclerViewAdapter
import br.com.ajeferson.combat.view.view.adapter.ChatRecyclerViewAdapter
import br.com.ajeferson.combat.view.view.enumeration.GameStatus
import br.com.ajeferson.combat.view.view.enumeration.GameStatus.*
import br.com.ajeferson.combat.view.view.enumeration.Owner
import br.com.ajeferson.combat.view.viewmodel.GameViewModel
import br.com.ajeferson.combat.view.viewmodel.factory.GameViewModelFactory
import dagger.android.AndroidInjection
import javax.inject.Inject

class GameActivity : AppCompatActivity() {

    lateinit var binding: ActivityGameBinding
    lateinit var viewModel: GameViewModel

    @Inject
    lateinit var factory: GameViewModelFactory

    private val chatAdapter: ChatRecyclerViewAdapter by lazy {
        ChatRecyclerViewAdapter()
    }

    private val boardAdapter: BoardRecyclerViewAdapter by lazy {
        BoardRecyclerViewAdapter(listOf(), mutableListOf())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_game)
        viewModel = ViewModelProviders.of(this, factory).get(GameViewModel::class.java)

        // Chat Adapter
        binding.chatRv.adapter = chatAdapter
        binding.chatRv.layoutManager = LinearLayoutManager(this)

        // Board Adapter
        binding.gameRv.adapter = boardAdapter
        binding.gameRv.layoutManager = GridLayoutManager(this, viewModel.board.size)
        boardAdapter.board = viewModel.board
        boardAdapter.pieces = viewModel.pieces
        boardAdapter.onItemClick = viewModel.didClickPiece

        binding.executePendingBindings()

        observe()
        viewModel.onCreate()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_game_menu, menu)
        return true
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.menu_connect -> viewModel.onConnectTouched()
            else -> Unit
        }
        return true
    }

    private fun observe() {
        viewModel.messages.observe(this, Observer { it?.let { handleChatMessage(it) } })
        viewModel.liveStatus.observe(this, Observer { it?.let { handleStatusChange(it) } })
        viewModel.placedCoordinates.observe(this, Observer { it?.let { presentPlacePickerDialog(it.copy()) } })
        viewModel.placedPiece.observe(this, Observer { it?.let { boardAdapter.placePiece(it) } })
        viewModel.error.observe(this, Observer { it?.let { handleError(it) } })
    }

    private fun handleError(error: GameViewModel.Error) {
        when(error) {
            GameViewModel.Error.PLACE_PIECE_INVALID_COORDINATES -> presentErrorAlert("You can not place a piece here")
        }
    }

    private fun handleChatMessage(message: ChatMessage) {
        chatAdapter.addMessage(message)
    }

    private fun handleStatusChange(status: GameStatus) {

        val logMessage = when(status) {
            CONNECTING -> "Conectando ao servidor..."
            CONNECTED -> "Conectado"
            DISCONNECTED -> "Desconectado"
            WAITING_OPPONENT -> "Esperando oponente..."
            PLACING_PIECES -> "Posicione suas peças"
            OPPONENT_GIVE_UP -> "Oponente desistiu"
            READY -> if(viewModel.availablePiecesCount == 0) "Você está pronto para jogar" else "Seu oponente está pronto para jogar"
            TURN -> "Sua vez de jogar"
            OPPONENT_TURN -> "Esperando jogada do oponente"
        }

        chatAdapter.addMessage(ChatMessage(logMessage, Owner.SERVER))

    }

    private fun presentPlacePickerDialog(coordinates: Coordinates) {

        val kinds = viewModel
                .availablePieces
                .filter { it.value > 0 }
                .map { it.key }
                .sortedByDescending { it.toString() }
                .reversed()

        val items = kinds.map {
            val amount = viewModel.availablePieces[it]!!
            "${getString(it.descriptionId)} x$amount"
        }.toTypedArray()

        AlertDialog
                .Builder(this)
                .setTitle(getString(R.string.select_a_piece))
                .setItems(items, { _, index ->
                    viewModel.selectPiece(kinds[index], coordinates)
                })
                .show()

    }

    private fun presentErrorAlert(message: String) {
        AlertDialog
                .Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
    }

    companion object {

        private const val TAG = "Game"

        fun newIntent(context: Context) = Intent(context, GameActivity::class.java)

    }

}
