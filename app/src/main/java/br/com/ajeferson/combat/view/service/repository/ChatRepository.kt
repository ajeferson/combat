package br.com.ajeferson.combat.view.service.repository

import br.com.ajeferson.combat.view.service.connection.ConnectionManager
import br.com.ajeferson.combat.view.service.message.Message
import br.com.ajeferson.combat.view.service.message.MessageKind
import br.com.ajeferson.combat.view.service.model.ChatMessage
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by ajeferson on 25/02/2018.
 */
// TODO pass the connection on the constructor
class ChatRepository @Inject constructor(private val connectionManager: ConnectionManager) {

    var messages: Observable<ChatMessage> = connectionManager
            .messages
            .filter { it.kind.isChat }
            .map {
                val text = it.tokens[0] as String
                ChatMessage(text = text, kind = it.owner)
            }

    fun sendMessage(text: String) {
        val message = Message(kind = MessageKind.CHAT)
        message.tokens.add(text)
        connectionManager.sendMessage(message)
    }


}