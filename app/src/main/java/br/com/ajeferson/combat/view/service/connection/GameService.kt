package br.com.ajeferson.combat.view.service.connection

import br.com.ajeferson.combat.view.service.model.*
import br.com.ajeferson.combat.view.view.enumeration.GameStatus
import io.reactivex.subjects.Subject

/**
 * Created by ajeferson on 25/02/2018.
 */
interface GameService {

    val status: Subject<GameStatus>
    val chats: Subject<ChatMessage>
    val placedPieces: Subject<PieceCoordinatesDto>
    val moves: Subject<Move>
    val strikes: Subject<Strike>
    val restarts: Subject<Restart>

    var id: Int

    // One for each Message Kind
    fun connect()
    fun disconnect()
    fun placePiece(placedPiece: PlacedPiece)
    fun ready()
    fun move(from: Coordinates, to: Coordinates)
    fun strike(from: Coordinates, to: Coordinates)
    fun sendChat(text: String)
    fun restart()
    fun answerRestartRequest(accepted: Boolean)

}