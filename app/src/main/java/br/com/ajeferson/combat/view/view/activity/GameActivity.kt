package br.com.ajeferson.combat.view.view.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import br.com.ajeferson.combat.R
import br.com.ajeferson.combat.databinding.ActivityGameBinding
import br.com.ajeferson.combat.view.service.model.ChatMessage
import br.com.ajeferson.combat.view.service.model.ChatMessage.Kind.*
import br.com.ajeferson.combat.view.view.adapter.BoardRecyclerViewAdapter
import br.com.ajeferson.combat.view.view.adapter.ChatRecyclerViewAdapter
import br.com.ajeferson.combat.view.viewmodel.GameViewModel
import br.com.ajeferson.combat.view.viewmodel.GameViewModel.Status.*
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
        boardAdapter.onItemClick = viewModel.onPieceClick

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
    }

    private fun handleChatMessage(message: ChatMessage) {
        chatAdapter.addMessage(message)
    }

    private fun handleStatusChange(status: GameViewModel.Status) {
        when(status) {
            CONNECTING -> chatAdapter.addMessage(ChatMessage("Conectando ao servidor...", LOG))
            CONNECTED -> chatAdapter.addMessage(ChatMessage("Conectado", LOG))
            DISCONNECTED -> chatAdapter.addMessage(ChatMessage("Desconectado", LOG))
            WAITING_OPPONENT -> chatAdapter.addMessage(ChatMessage("Esperando oponente...", LOG))
            else -> Unit
        }
    }

    companion object {

        fun newIntent(context: Context) = Intent(context, GameActivity::class.java)

    }

}
