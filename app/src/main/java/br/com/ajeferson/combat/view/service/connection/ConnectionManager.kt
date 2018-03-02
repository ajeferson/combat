package br.com.ajeferson.combat.view.service.connection

import br.com.ajeferson.combat.view.service.message.Message
import io.reactivex.subjects.Subject

/**
 * Created by ajeferson on 25/02/2018.
 */
interface ConnectionManager {

    val connection: Any
    val status: Subject<ConnectionStatus>
    val messages: Subject<Message>

    fun connect()
    fun sendMessage(message: String)

    enum class ConnectionStatus {
        CONNECTING,
        CONNECTED,
        DISCONNECTED,
    }

}