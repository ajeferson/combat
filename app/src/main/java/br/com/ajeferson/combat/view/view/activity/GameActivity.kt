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
import br.com.ajeferson.combat.view.service.model.Restart
import br.com.ajeferson.combat.view.service.model.Strike
import br.com.ajeferson.combat.view.view.adapter.BoardRecyclerViewAdapter
import br.com.ajeferson.combat.view.view.adapter.ChatRecyclerViewAdapter
import br.com.ajeferson.combat.view.view.enumeration.GameStatus
import br.com.ajeferson.combat.view.view.enumeration.GameStatus.*
import br.com.ajeferson.combat.view.view.enumeration.Owner
import br.com.ajeferson.combat.view.view.enumeration.RestartKind
import br.com.ajeferson.combat.view.view.enumeration.beats
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
        observe()
        viewModel.onCreate()

        // Chat Adapter
        binding.chatRv.adapter = chatAdapter
        binding.chatRv.layoutManager = LinearLayoutManager(this)

        // Board Adapter
        binding.gameRv.adapter = boardAdapter
        binding.gameRv.layoutManager = GridLayoutManager(this, viewModel.board.size)
        boardAdapter.board = viewModel.board
        boardAdapter.pieces = viewModel.pieces
        boardAdapter.onItemClick = viewModel.didClickPiece

        binding.viewModel = viewModel
        binding.executePendingBindings()

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
            R.id.menu_give_up -> viewModel.onGiveUpTouched()
            R.id.menu_restart -> viewModel.onRestartTouched()
            else -> Unit
        }
        return true
    }

    private fun observe() {
        viewModel.messages.observe(this, Observer { it?.let { handleChatMessage(it) } })
        viewModel.liveStatus.observe(this, Observer { it?.let { handleStatusChange(it) } })
        viewModel.placedCoordinates.observe(this, Observer { it?.let { presentPlacePickerDialog(it) } })
        viewModel.placedPiece.observe(this, Observer { it?.let { handlePlacedPiece() } })
        viewModel.error.observe(this, Observer { it?.let { handleError(it) } })
        viewModel.move.observe(this, Observer { it?.let { handleMove() } })
        viewModel.strikes.observe(this, Observer { it?.let { handleStrike(it) } })
        viewModel.restarts.observe(this, Observer { it?.let { handleRestart(it) } })
    }

    private fun handleError(error: GameViewModel.Error) {
        when(error) {
            GameViewModel.Error.PLACE_PIECE_INVALID_COORDINATES -> presentErrorAlert("You can not place a piece here")
            GameViewModel.Error.ALREADY_CONNECTED -> presentErrorAlert("You are already connected to the server")
            GameViewModel.Error.ALREADY_DISCONNECTED -> presentErrorAlert("You are already disconnected")
            GameViewModel.Error.ALREADY_RESTARTING -> presentErrorAlert("There already is a restart request in progress")
            GameViewModel.Error.MOVE -> presentErrorAlert("Bad move")
        }
    }

    private fun handleChatMessage(message: ChatMessage) {
        chatAdapter.addMessage(message)
    }

    private fun handleStatusChange(status: GameStatus) {

        val logMessage = when(status) {
            CONNECTING -> "Connecting..."
            CONNECTED -> "Connected"
            DISCONNECTED -> "Disconnected"
            WAITING_OPPONENT -> "Waiting opponent..."
            PLACING_PIECES -> "Place your pieces"
            OPPONENT_GIVE_UP -> "Opponent has given up"
            READY -> ""
            TURN -> "Your turn"
            OPPONENT_TURN -> "Opponent's turn"
        }

        // Star over the game
        if(status == DISCONNECTED) {
            chatAdapter.clearMessages()
            boardAdapter.pieces = viewModel.pieces
        }

        // Opponent has given up
        if(status == OPPONENT_GIVE_UP) {
            presentInfoAlert("Your opponent has given up this match, you won!")
        }

        if(!logMessage.isEmpty()) {
            chatAdapter.addMessage(ChatMessage(logMessage, Owner.SERVER))
        }

    }

    private fun handlePlacedPiece() {
        boardAdapter.pieces = viewModel.pieces
    }

    private fun handleMove() {
        boardAdapter.pieces = viewModel.pieces
    }

    private fun handleStrike(strike: Strike) {

        boardAdapter.pieces = viewModel.pieces

        val striker = strike.striker ?: return
        val defender = strike.defender ?: return
        val owner = strike.owner ?: return

        val result = when(striker beats defender) {
            null -> "It' a tie"
            true -> if(owner == Owner.SELF) "You win" else "You lose"
            false -> if(owner == Owner.SELF) "You lose" else "You win"
        }

        val strikerDesc = getString(striker.descriptionId)
        val defenderDesc = getString(defender.descriptionId)

        presentAlert("Result", "$strikerDesc vs $defenderDesc: $result")

    }

    private fun handleRestart(restart: Restart) {
        when(restart.kind) {
            RestartKind.REQUEST -> presentRestartRequestAlert()
            RestartKind.REJECTED -> presentErrorAlert("Your restart request was declined")
            RestartKind.ACCEPTED -> {
                restartAccepted()
                presentAlert("Success", "Your restart request was accepted")
            }
        }
    }

    private fun restartAccepted() {
        chatAdapter.clearMessages()
        chatAdapter.addMessage(ChatMessage("Match restarted", Owner.SERVER))
        viewModel.restartReset()
        boardAdapter.pieces = viewModel.pieces
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

    private fun presentRestartRequestAlert() {
        AlertDialog
                .Builder(this)
                .setTitle("Restart")
                .setMessage("You opponent wants to restart this match. Proceed?")
                .setPositiveButton("OK", { _, _ ->
                    restartAccepted()
                    viewModel.answerRestartRequest(true)
                })
                .setNegativeButton("Cancel", { _, _ ->  viewModel.answerRestartRequest(false) })
                .show()
    }

    private fun presentErrorAlert(message: String) {
        presentAlert("Error", message)
    }

    private fun presentInfoAlert(message: String) {
        presentAlert("Info", message)
    }

    private fun presentAlert(title: String, message: String) {
        AlertDialog
                .Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
    }

    companion object {

        private const val TAG = "Game"

        fun newIntent(context: Context) = Intent(context, GameActivity::class.java)

    }

}
