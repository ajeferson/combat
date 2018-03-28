package br.com.ajeferson.combat.view.service.connection

import android.content.Context.WIFI_SERVICE
import android.net.wifi.WifiManager
import android.text.format.Formatter
import br.com.ajeferson.combat.view.common.App
import br.com.ajeferson.combat.view.rmi.RmiClientInterface
import br.com.ajeferson.combat.view.rmi.RmiServerInterface
import br.com.ajeferson.combat.view.service.model.*
import br.com.ajeferson.combat.view.view.enumeration.GameStatus
import br.com.ajeferson.combat.view.view.enumeration.GameStatus.*
import br.com.ajeferson.combat.view.view.enumeration.Owner
import br.com.ajeferson.combat.view.view.enumeration.PieceKind
import br.com.ajeferson.combat.view.view.enumeration.RestartKind
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import lipermi.handler.CallHandler
import lipermi.net.Client
import lipermi.net.Server
import java.lang.Exception

/**
 * Created by ajeferson on 26/03/2018.
 */
class RmiGameService(app: App, private val ip: String, private val port: Int): GameService, RmiClientInterface {

    private lateinit var rmi: RmiServerInterface
    private val clientIp: String

    override val status: Subject<GameStatus> = PublishSubject.create()
    override val chats: Subject<ChatMessage> = PublishSubject.create()
    override val placedPieces: Subject<PieceCoordinatesDto> = PublishSubject.create()
    override val moves: Subject<Move> = PublishSubject.create()
    override val strikes: Subject<Strike> = PublishSubject.create()
    override val restarts: Subject<Restart> = PublishSubject.create()

    override var id: Int = -1

    private var exported = false

    init {

        // Set client ip
        val wm = app.applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        clientIp = Formatter.formatIpAddress(wm.connectionInfo.ipAddress)

    }




    /**
     * GameService
     * */

    override fun connect() {

        Completable
                .complete()
                .subscribeOn(Schedulers.io())
                .subscribe {

                    try {

                        // Get remote server object
                        val callHandler = CallHandler()
                        val client = Client(ip, port, callHandler)
                        rmi = client.getGlobal(RmiServerInterface::class.java) as RmiServerInterface

                        emitStatus(CONNECTING)

                        // Register self as a client
                        if(!exported) {
                            val ch = CallHandler()
                            ch.registerGlobal(RmiClientInterface::class.java, this)
                            val server = Server()
                            server.bind(port, ch)
                            exported = true
                        }

                        // Actually joining the game
                        emitStatus(CONNECTED)
                        rmi.serverJoin(clientIp)

                    } catch (e: Exception) {
                        e.printStackTrace()
                        emitStatus(DISCONNECTED)
                    }

                }

    }

    override fun disconnect() {
        rmi.serverGiveUp(id)
        emitStatus(DISCONNECTED)
    }

    override fun placePiece(placedPiece: PlacedPiece) {
        rmi.serverPlacePiece(placedPiece.copy(senderId = id))
    }

    override fun ready() {
        rmi.serverReady(id)
    }

    override fun move(from: Coordinates, to: Coordinates) {
        val move = RmiMove(from.row, from.column, to.row, to.column)
        rmi.serverMove(id, move)
    }

    override fun strike(from: Coordinates, to: Coordinates) {
        val move = RmiMove(from.row, from.column, to.row, to.column)
        rmi.serverStrike(id, move)
    }

    override fun sendChat(text: String) {
        rmi.serverChat(id, text)
    }

    override fun restart() {
        rmi.serverRestart(id)
    }

    override fun answerRestartRequest(accepted: Boolean) {
        rmi.serverAnswerRestartRequest(id, accepted)
    }





    /**
     * Helper
     * */

    private fun emitStatus(newStatus: GameStatus?) {
        if(newStatus == null) return
        status.onNext(newStatus)
    }

    private fun ownerWith(id: Int): Owner = if(id == this.id) Owner.SELF else Owner.OPPONENT




    /**
     * RmiClientInterface
     * Messages that the server calls in this client
     * */

    override fun clientPlacePiece(placedPiece: PlacedPiece) {

        val owner = ownerWith(placedPiece.senderId)
        val piece = Piece(kind = PieceKind.valueOf(placedPiece.kind), owner = owner)

        val coordinates = Coordinates.newInstance(owner, placedPiece.row, placedPiece.column)
        val dto = PieceCoordinatesDto(piece = piece, coordinates = coordinates)

        placedPieces.onNext(dto)

    }

    override fun clientWaitOpponent() {
        emitStatus(WAITING_OPPONENT)
    }

    override fun clientPlacePieces(id: Int) {
        emitStatus(PLACING_PIECES)
        this.id = id
    }

    override fun clientTurn() {
        emitStatus(TURN)
    }

    override fun clientOpponentTurn() {
        emitStatus(OPPONENT_TURN)
    }

    override fun clientMove(id: Int, rmiMove: RmiMove) {
        val owner = ownerWith(id)
        val from = Coordinates.newInstance(owner, rmiMove.x1, rmiMove.y1)
        val to = Coordinates.newInstance(owner, rmiMove.x2, rmiMove.y2)
        moves.onNext(Move(from, to))
    }

    override fun clientStrike(id: Int, rmiStrike: RmiMove) {
        val owner = ownerWith(id)
        val from = Coordinates.newInstance(owner, rmiStrike.x1, rmiStrike.y1)
        val to = Coordinates.newInstance(owner, rmiStrike.x2, rmiStrike.y2)
        strikes.onNext(Strike(from, to))
    }

    override fun clientChat(id: Int, message: String) {
        val chatMessage = ChatMessage(text = message, kind = ownerWith(id))
        chats.onNext(chatMessage)
    }

    override fun clientRestartRequest() {
        restarts.onNext(Restart(RestartKind.REQUEST))
    }

    override fun clientRestartAnswer(accepted: Boolean) {
        val kind = if(accepted) RestartKind.ACCEPTED else RestartKind.REJECTED
        restarts.onNext(Restart(kind))
    }

    override fun clientGiveUp() {
        emitStatus(OPPONENT_GIVE_UP)
        emitStatus(DISCONNECTED)
    }

    override fun clientKeepAlive() {
    }

}