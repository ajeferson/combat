package br.com.ajeferson.combat.view.service.connection

import br.com.ajeferson.combat.view.service.message.Message
import br.com.ajeferson.combat.view.service.model.ChatMessage
import br.com.ajeferson.combat.view.service.model.PieceCoordinatesDto
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

    var id: Long

    fun connect()
    fun disconnect()
    fun sendMessage(message: Message)

    // One for each Message Kind
    fun waitForOpponent()
//    fun opponentGiveUp()
//    fun placePiece()
//    ...

}