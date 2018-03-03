package br.com.ajeferson.combat.view.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import br.com.ajeferson.combat.view.extension.value
import br.com.ajeferson.combat.view.service.connection.GameService
import br.com.ajeferson.combat.view.service.message.MessageKind
import br.com.ajeferson.combat.view.service.model.*
import br.com.ajeferson.combat.view.view.enumeration.*
import br.com.ajeferson.combat.view.view.enumeration.BoardItemKind.*
import br.com.ajeferson.combat.view.view.enumeration.GameStatus.*
import br.com.ajeferson.combat.view.view.enumeration.PieceKind.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlin.math.abs
import kotlin.math.max

/**
 * Created by ajeferson on 20/02/2018.
 */
class GameViewModel(private val gameService: GameService): ViewModel() {

    val text = ObservableField<String>()
    val messages = MutableLiveData<ChatMessage>()
    val status = ObservableField(DISCONNECTED)
    val liveStatus = MutableLiveData<GameStatus>()
    val error = MutableLiveData<Error>()
    val move = MutableLiveData<Move>()
    val strikes = MutableLiveData<Strike>()
    val restarts = MutableLiveData<Restart>()

    private var restarting = false

    val placedCoordinates = MutableLiveData<Coordinates>()
    val placedPiece = MutableLiveData<PieceCoordinatesDto>()

    lateinit var pieces: MutableList<MutableList<Piece?>>

    private var moveCoordinate: Coordinates? = null

    private val initialAvailablePieces = mapOf(
            SOLDIER to 1//8,
//            BOMB to 1,//6,
//            GUNNER to 1,//5,
//            SERGEANT to 1,//4,
//            TENANT to 1,//4,
//            CAPTAIN to 1,//4,
//            MAJOR to 1,//3,
//            COLONEL to 1,//2,
//            GENERAL to 1,
//            MARSHAL to 1,
//            SPY to 1,
//            PRISONER to 1
    )

    var availablePieces = mutableMapOf<PieceKind, Int>()
    val availablePiecesCount get() = availablePieces
            .map { it.value }
            .reduce { a, b -> a + b }

    private fun setStatus(status: GameStatus) {
        liveStatus.value = status
        this.status.value = status
    }



    /**
     * Life Cycle
     * */

    fun onCreate() {
        resetGame()
        subscribeToStatus()
        subscribeToChats()
        subscribeToPlacedPieces()
        subscribeToMoves()
        subscribeToStrikes()
        subscribeToRestarts()
    }

    fun onStart() {
    }

    fun onResume() {

    }

    fun onDestroy() {

    }





    /**
     * Subscriptions
     * */

    private fun subscribeToChats() {
        gameService
                .chats
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    messages.value = it
                }
    }

    private fun subscribeToStatus() {
        gameService
                .status
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    when(it) {
                        DISCONNECTED -> {
                            resetGame()
                        }
                        else -> Unit
                    }

                    setStatus(it)

                }, {
                    // TODO Handle
                })
    }

    private fun subscribeToPlacedPieces() {
        gameService
                .placedPieces
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    placePiece(it)
                }
    }

    private fun subscribeToMoves() {
        gameService
                .moves
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    didMove(it)
                }
    }

    private fun subscribeToStrikes() {
        gameService
                .strikes
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    didStrike(it)
                }
    }

    private fun subscribeToRestarts() {
        gameService
                .restarts
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    restarts.value = it
                    if(it.kind != RestartKind.REQUEST) {
                        restarting = false
                    }
                }
    }


    /**
     * Pieces Placing
     * */

    fun selectPiece(pieceKind: PieceKind, coordinates: Coordinates) {

        // Build the message
        val message = MessageKind
                .PLACE_PIECE
                .message
                .apply { addValues(pieceKind.toString()) } // Add the kind
                .apply { addValues(coordinates.row) }
                .apply { addValues(coordinates.column) }

        // Send the message
        gameService.sendMessage(message)

    }

    private fun placePiece(dto: PieceCoordinatesDto) {

        // Place the piece
        val (row, column) = dto.coordinates
        pieces[row][column] = dto.piece.copy()
        placedPiece.value = dto

        // Should not decrease amount if it belongs to the opponent
        if(dto.piece.belongsToOpponent) {
            return
        }

        // Reduce the current amount
        val oldAmount = availablePieces[dto.piece.kind] ?: return
        availablePieces[dto.piece.kind] = oldAmount - 1

        // Ready to play
        if(availablePiecesCount == 0) {
            setStatus(READY)
            gameService.sendMessage(MessageKind.READY.message)
        }

    }




    /**
     * View Actions
     * */

    fun onConnectTouched() {
        if(status.value == CONNECTING) return
        if(status.value != DISCONNECTED) {
            error.value = Error.ALREADY_CONNECTED
            return
        }
        gameService.connect()
    }

    fun onGiveUpTouched() {
        if(status.value == null || status.value == DISCONNECTED) {
            error.value = Error.ALREADY_DISCONNECTED
            return
        }
        gameService.disconnect()
    }

    fun onRestartTouched() {
        if(status.value != TURN && status.value != OPPONENT_TURN && status.value != PLACING_PIECES) {
            return
        }
        if(restarting) {
            error.value = Error.ALREADY_RESTARTING
        }
        restarting = true
        gameService.restart()
    }

    fun onSendTouched() {

        if(status.value == null || status.value == DISCONNECTED || status.value == CONNECTING) {
            return
        }

        if(text.value.trim().isEmpty()) {
            return
        }

        gameService.sendChat(text.value.trim())
        text.value = ""

    }

    fun answerRestartRequest(accepted: Boolean) {
        gameService.answerRestartRequest(accepted)
    }

    val didClickPiece: (Int, Int) -> Unit = click@ { row, column ->

        val status = this.status.value ?: return@click

        val coordinates = Coordinates.newInstance(row, column)

        if(status.isPlacingPieces) {
            if(row < 6 || pieces[row][column] != null) {
                error.value = Error.PLACE_PIECE_INVALID_COORDINATES
                return@click
            }
            placedCoordinates.value = coordinates
            return@click
        }

        // TODO Validate moveCoordinate
        if(status.isTurn) {

            if(moveCoordinate == null) { // Select piece to moveCoordinate

                if(!isMoveValid(coordinates, null)) {
                    error.value = Error.MOVE
                    return@click
                }

                moveCoordinate = coordinates

            } else {

                if(!isMoveValid(moveCoordinate!!, coordinates)) {
                    error.value = Error.MOVE
                    return@click
                }

                if(pieces[coordinates.row][coordinates.column]?.owner == Owner.OPPONENT) {
                    gameService.sendStrike(moveCoordinate!!, coordinates)
                } else {
                    gameService.sendMove(moveCoordinate!!, coordinates)
                }

                moveCoordinate = null

            }

            return@click

        }

    }

    private fun isMoveValid(from: Coordinates, to: Coordinates?): Boolean {


        /**
         * First step
         * */

        run {

            val (row, column) = from

            if(board[row][column] == WATER || pieces[row][column]?.owner != Owner.SELF ||
                    pieces[row][column]?.kind == BOMB || pieces[row][column]?.kind == PRISONER) {
                return false
            }

        }

        /**
         * Second Step
         * */

        run {

            if(to == null) {
                return true
            }

            val (row, column) = to

            // Not water and not over a self piece
            if(board[row][column] == WATER || pieces[row][column]?.owner == Owner.SELF) {
                return false
            }

            val kind = pieces[from.row][from.column]?.kind ?: return false

            // Soldier can go everywhere
            // TODO can not jump over other pieces
            if(kind == SOLDIER) {

                val rowDiff = abs(from.row - to.row)
                val columnDiff = abs(from.column - to.column)

                if((rowDiff > 0) xor (columnDiff > 0)) {
                    val change = max(rowDiff, columnDiff)
                    return change == 1 || pieces[row][column] == null
                }

                return false

            }

            return (abs(from.row - to.row) + abs(from.column - to.column)) == 1

        }



    }

    private fun didMove(move: Move) {
        val (from, to) = move
        pieces[to.row][to.column] = pieces[from.row][from.column]
        pieces[from.row][from.column] = null
        this.move.value = move
    }

    private fun didStrike(strike: Strike) {

        val (from, to) = strike

        val striker = pieces[from.row][from.column]?.kind ?: return
        val defender = pieces[to.row][to.column]?.kind ?: return

        strike.striker = striker
        strike.defender = defender
        strike.owner = pieces[from.row][from.column]?.owner

        when(striker beats defender) {
            null -> { // Tie
                pieces[from.row][from.column] = null
                pieces[to.row][to.column] = null
            }
            true -> { // Win
                pieces[to.row][to.column] = pieces[from.row][from.column]
                pieces[from.row][from.column] = null
            }
            false -> { // Lose
                pieces[from.row][from.column] = null
            }
        }

        this.strikes.value = strike

    }

    private fun resetGame() {
        pieces = (0 until 10).map { arrayOfNulls<Piece>(10).toMutableList() }.toMutableList()
        initialAvailablePieces.forEach { availablePieces[it.key] = it.value }
        moveCoordinate = null
    }

    fun restartReset() {
        resetGame()
        setStatus(PLACING_PIECES)
    }

    val board: List<List<BoardItemKind>> = listOf(
            listOf(LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND),
            listOf(LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND),
            listOf(LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND),
            listOf(LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND),
            listOf(LAND, LAND, WATER, WATER, LAND, LAND, WATER, WATER, LAND, LAND),
            listOf(LAND, LAND, WATER, WATER, LAND, LAND, WATER, WATER, LAND, LAND),
            listOf(LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND),
            listOf(LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND),
            listOf(LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND),
            listOf(LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND)
    )

    enum class Error {
        PLACE_PIECE_INVALID_COORDINATES,
        ALREADY_CONNECTED,
        ALREADY_DISCONNECTED,
        ALREADY_RESTARTING,
        MOVE
    }

}