package br.com.ajeferson.combat.view.service.connection

import io.reactivex.Observable

/**
 * Created by ajeferson on 25/02/2018.
 */
interface ConnectionManager {

    val connection: Any
    val status: Observable<ConnectionStatus>
    val messages: Observable<String>

    fun connect()
    fun sendMessage(message: String)

    enum class ConnectionStatus {
        CONNECTING,
        CONNECTED,
        DISCONNECTED,
    }

}