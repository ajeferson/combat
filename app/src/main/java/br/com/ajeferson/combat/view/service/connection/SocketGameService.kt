package br.com.ajeferson.combat.view.service.connection

import java.net.InetSocketAddress
import java.net.Socket
import br.com.ajeferson.combat.view.service.message.Message
import br.com.ajeferson.combat.view.service.message.MessageKind
import br.com.ajeferson.combat.view.service.message.MessageKind.*
import br.com.ajeferson.combat.view.service.model.ChatMessage
import br.com.ajeferson.combat.view.service.model.Coordinates
import br.com.ajeferson.combat.view.service.model.Piece
import br.com.ajeferson.combat.view.service.model.PieceCoordinatesDto
import br.com.ajeferson.combat.view.view.enumeration.GameStatus
import br.com.ajeferson.combat.view.view.enumeration.GameStatus.*
import br.com.ajeferson.combat.view.view.enumeration.PieceKind
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.io.PrintWriter
import java.util.*

/**
 * Created by ajeferson on 25/02/2018.
 */
class SocketGameService(private val ip: String, private val port: Int): GameService {

    override val status: Subject<GameStatus> = PublishSubject.create()
    override val chats: Subject<ChatMessage> = PublishSubject.create()
    override val placedPieces: Subject<PieceCoordinatesDto> = PublishSubject.create()

    override var id: Long = -1L

    override val connection get() = socket
    private lateinit var socket: Socket

    private lateinit var reader: Scanner
    private lateinit var writer: PrintWriter

    private val messagesListener = Runnable {
        try {
            do {
                val rawMessage = reader.nextLine()
                if(rawMessage != null) {
                    didReceive(Message.from(rawMessage))
                }
            } while(rawMessage != null)
        } catch (e: NoSuchElementException) {
            emitStatus(DISCONNECTED) // Lost connection with server
        }
    }

    override fun connect() {
        Completable
                .complete()
                .subscribeOn(Schedulers.io())
                .subscribe {
                    emitStatus(CONNECTING)
                    socket = Socket()
                    socket.connect(InetSocketAddress(ip, port), 10000)
                    reader = Scanner(socket.getInputStream())
                    writer = PrintWriter(socket.getOutputStream())
                    Thread(messagesListener).start()
                    emitStatus(CONNECTED)
                }
    }

    override fun sendMessage(message: Message) {
        message.senderId = id
        Completable
                .complete()
                .subscribeOn(Schedulers.io())
                .subscribe {
                    writer.println(message)
                    writer.flush()
                }
    }


    private fun didReceive(message: Message) {

        emitStatus(when(message.kind) {
            WAIT_OPPONENT -> WAITING_OPPONENT
            MessageKind.OPPONENT_GIVE_UP -> GameStatus.OPPONENT_GIVE_UP
            PLACE_PIECES -> PLACING_PIECES
            else -> null
        })

        if(message.kind.isPlacePieces) {
            val idStr = message.tokens[0] as String
            id = idStr.toLong()
        }

        message.setOwnerFromId(id)

        // Dispatch to specific handlers
        when(message.kind) {
            CHAT -> handleChatMessage(message)
            PLACE_PIECE -> handlePlacePieceMessage(message)
            else -> Unit
        }

    }

    private fun handleChatMessage(message: Message) {
        val text = message.tokens[0]
        val chatMessage = ChatMessage(text = text, kind = message.owner)
        chats.onNext(chatMessage)
    }

    private fun handlePlacePieceMessage(message: Message) {
        val piece = Piece(kind = PieceKind.valueOf(message.tokens[0]), owner = message.owner)
        val coordinates = Coordinates(row = message.tokens[1].toInt(), column = message.tokens[2].toInt())
        val dto = PieceCoordinatesDto(piece = piece, coordinates = coordinates)
        placedPieces.onNext(dto)
    }

    private fun emitStatus(newStatus: GameStatus?) {
        if(newStatus == null) return
        status.onNext(newStatus)
    }

    // RMI Implementation
    override fun waitForOpponent() {
        emitStatus(WAITING_OPPONENT)
    }

}