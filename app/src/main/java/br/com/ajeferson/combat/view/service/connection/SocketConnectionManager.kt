package br.com.ajeferson.combat.view.service.connection

import java.net.InetSocketAddress
import java.net.Socket
import br.com.ajeferson.combat.view.service.connection.ConnectionManager.ConnectionStatus.*
import br.com.ajeferson.combat.view.service.connection.ConnectionManager.ConnectionStatus
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.schedulers.Schedulers
import java.io.PrintWriter
import java.util.*

/**
 * Created by ajeferson on 25/02/2018.
 */
class SocketConnectionManager(private val ip: String, private val port: Int): ConnectionManager {

    private lateinit var statusEmitter: ObservableEmitter<ConnectionStatus>
    private lateinit var messagesEmitter: ObservableEmitter<String>

    override val status: Observable<ConnectionStatus> = Observable.create<ConnectionStatus> {
        statusEmitter = it
    }

    override val messages = Observable.create<String> {
        messagesEmitter = it
    }

    override val connection get() = socket
    private lateinit var socket: Socket

    private lateinit var reader: Scanner
    private lateinit var writer: PrintWriter

    private val messagesListener = Runnable {
        do {
            val message = reader.nextLine()
            if(message != null) {
                messagesEmitter.onNext(message)
            }
        } while(message != null)
    }

    override fun connect() {
        Completable
                .complete()
                .subscribeOn(Schedulers.io())
                .subscribe {
                    statusEmitter.onNext(CONNECTING)
                    socket = Socket()
                    socket.connect(InetSocketAddress(ip, port), 10000)
                    reader = Scanner(socket.getInputStream())
                    writer = PrintWriter(socket.getOutputStream())
                    Thread(messagesListener).start()
                    statusEmitter.onNext(CONNECTED)
                }
    }

    override fun sendMessage(message: String) {
        Completable
                .complete()
                .subscribeOn(Schedulers.io())
                .subscribe {
                    writer.println(message)
                    writer.flush()
                }
    }

}