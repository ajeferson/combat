package br.com.ajeferson.combat.view.view.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import br.com.ajeferson.combat.R
import br.com.ajeferson.combat.databinding.ActivityGameBinding
import br.com.ajeferson.combat.view.service.model.ChatMessage
import br.com.ajeferson.combat.view.view.adapter.ChatRecyclerViewAdapter
import br.com.ajeferson.combat.view.viewmodel.GameViewModel

class GameActivity : AppCompatActivity() {

    lateinit var binding: ActivityGameBinding
    lateinit var viewModel: GameViewModel

    private val chatAdapter: ChatRecyclerViewAdapter by lazy {
        ChatRecyclerViewAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_game)
        viewModel = ViewModelProviders.of(this).get(GameViewModel::class.java)

        binding.chatRv.adapter = chatAdapter
        binding.chatRv.layoutManager = LinearLayoutManager(this)

        binding.executePendingBindings()


        viewModel.onCreate()

    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
        observe()
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    private fun observe() {
        viewModel.messages.observe(this, Observer { it?.let { handleChatMessage(it) } })
        viewModel.liveStatus.observe(this, Observer { it?.let { handleStatusChange(it) } })
    }

    private fun handleChatMessage(message: ChatMessage) {
        chatAdapter.addMessage(message)
    }

    private fun handleStatusChange(status: GameViewModel.Status) {
    }

    companion object {

        fun newIntent(context: Context) = Intent(context, GameActivity::class.java)

    }

}
