package br.com.ajeferson.combat.view.view.enumeration

/**
 * Created by ajeferson on 02/03/2018.
 */
enum class GameStatus {

    DISCONNECTED,           // Not connected to server at all
    CONNECTING,             // Waiting for a response to the connection request
    CONNECTED,              // Connected to server
    WAITING_OPPONENT,       // Waiting for the opponent to connect
    OPPONENT_GIVE_UP,       // Opponent has given up
    PLACING_PIECES,         // Should now starting placing pieces
    READY,                  // Ready to start playing
    TURN,                   // My turn to play
    OPPONENT_TURN;          // Opponent's turn

    val isPlacingPieces get() = this == PLACING_PIECES

}