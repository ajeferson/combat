package br.com.ajeferson.combat.view.viewmodel.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import br.com.ajeferson.combat.view.service.connection.ConnectionManager
import br.com.ajeferson.combat.view.service.repository.ChatRepository
import br.com.ajeferson.combat.view.viewmodel.GameViewModel
import javax.inject.Inject

/**
 * Created by ajeferson on 25/02/2018.
 */
class GameViewModelFactory @Inject constructor(
        private val connectionManager: ConnectionManager,
        private val chatMessageRepository: ChatRepository): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(GameViewModel::class.java)) {
            return GameViewModel(connectionManager, chatMessageRepository) as T
        }
        throw IllegalArgumentException("Invalid class")
    }

}