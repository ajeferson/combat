package br.com.ajeferson.combat.view.service.connection

import br.com.ajeferson.combat.view.service.message.Message
import br.com.ajeferson.combat.view.service.model.*
import br.com.ajeferson.combat.view.view.enumeration.GameStatus
import io.reactivex.subjects.Subject

/**
 * Created by ajeferson on 25/02/2018.
 */
interface GameService {

    val connection: Any

    val status: Subject<GameStatus>
    val chats: Subject<ChatMessage>
    val placedPieces: Subject<PieceCoordinatesDto>
    val moves: Subject<Move>
    val strikes: Subject<Strike>
    val restarts: Subject<Restart>

    var id: Long

    fun connect()
    fun disconnect()
    fun restart()
    fun answerRestartRequest(accepted: Boolean)

    fun sendMessage(message: Message)
    fun sendMove(from: Coordinates, to: Coordinates)
    fun sendStrike(from: Coordinates, to: Coordinates)

    // One for each Message Kind
    fun waitForOpponent()
//    fun opponentGiveUp()
//    fun placePiece()
//    ...

}