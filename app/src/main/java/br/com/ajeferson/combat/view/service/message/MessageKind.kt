package br.com.ajeferson.combat.view.service.message

/**
 * Created by ajeferson on 02/03/2018.
 */
enum class MessageKind {

    WAIT_OPPONENT, //
    OPPONENT_GIVE_UP,
    PLACE_PIECE,
    PLACE_PIECES, //
    READY,
    TURN,
    OPPONENT_TURN,
    MOVE,
    STRIKE,
    RESTART_REQUEST,
    RESTART_ACCEPTED,
    RESTART_REJECTED,
    CHAT;

    val message get() = Message(kind = this)

    val isChat get() = this == CHAT
    val isPlacePieces get() = this == PLACE_PIECES
    val isPlacePiece get() = this == PLACE_PIECE

}