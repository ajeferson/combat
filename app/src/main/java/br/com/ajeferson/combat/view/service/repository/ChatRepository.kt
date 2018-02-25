package br.com.ajeferson.combat.view.service.repository

import br.com.ajeferson.combat.view.service.connection.ConnectionManager
import br.com.ajeferson.combat.view.service.model.ChatMessage
import javax.inject.Inject

/**
 * Created by ajeferson on 25/02/2018.
 */
// TODO pass the connection on the constructor
class ChatRepository @Inject constructor(private val connectionManager: ConnectionManager) {

    // TODO Could be a flowable
    var messages = connectionManager
            .messages
            .map {
                ChatMessage(it, ChatMessage.Kind.SELF)
            }

    fun sendMessage(message: String) {
        connectionManager.sendMessage(message)
    }


}