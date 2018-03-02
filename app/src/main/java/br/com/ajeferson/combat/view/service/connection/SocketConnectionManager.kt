package br.com.ajeferson.combat.view.service.connection

import java.net.InetSocketAddress
import java.net.Socket
import br.com.ajeferson.combat.view.service.connection.ConnectionManager.ConnectionStatus.*
import br.com.ajeferson.combat.view.service.connection.ConnectionManager.ConnectionStatus
import br.com.ajeferson.combat.view.service.message.Message
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.io.PrintWriter
import java.util.*

/**
 * Created by ajeferson on 25/02/2018.
 */
class SocketConnectionManager(private val ip: String, private val port: Int): ConnectionManager {

    override var status = PublishSubject.create<ConnectionStatus>()
    override val messages = PublishSubject.create<Message>()

    override val connection get() = socket
    private lateinit var socket: Socket

    private lateinit var reader: Scanner
    private lateinit var writer: PrintWriter

    private val messagesListener = Runnable {
        try {
            do {
                val rawMessage = reader.nextLine()
                if(rawMessage != null) {
                    messages.onNext(Message.from(rawMessage))
                }
            } while(rawMessage != null)
        } catch (e: NoSuchElementException) {
            status.onNext(DISCONNECTED) // Lost connection with server
        }
    }

    override fun connect() {
        Completable
                .complete()
                .subscribeOn(Schedulers.io())
                .subscribe {
                    status.onNext(CONNECTING)
                    socket = Socket()
                    socket.connect(InetSocketAddress(ip, port), 10000)
                    reader = Scanner(socket.getInputStream())
                    writer = PrintWriter(socket.getOutputStream())
                    Thread(messagesListener).start()
                    status.onNext(CONNECTED)
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