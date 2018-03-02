package br.com.ajeferson.combat.view.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import android.util.Log
import br.com.ajeferson.combat.view.extension.value
import br.com.ajeferson.combat.view.service.connection.GameService
import br.com.ajeferson.combat.view.service.model.ChatMessage
import br.com.ajeferson.combat.view.service.message.MessageKind
import br.com.ajeferson.combat.view.service.model.Coordinates
import br.com.ajeferson.combat.view.service.model.Piece
import br.com.ajeferson.combat.view.service.model.PieceCoordinatesDto
import br.com.ajeferson.combat.view.view.enumeration.BoardItemKind
import br.com.ajeferson.combat.view.view.enumeration.BoardItemKind.*
import br.com.ajeferson.combat.view.view.enumeration.GameStatus
import br.com.ajeferson.combat.view.view.enumeration.GameStatus.*
import br.com.ajeferson.combat.view.view.enumeration.Owner
import br.com.ajeferson.combat.view.view.enumeration.PieceKind
import br.com.ajeferson.combat.view.view.enumeration.PieceKind.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by ajeferson on 20/02/2018.
 */
class GameViewModel(private val gameService: GameService): ViewModel() {

    val messages = MutableLiveData<ChatMessage>()
    val status = ObservableField(DISCONNECTED)
    val liveStatus = MutableLiveData<GameStatus>()
    val error = MutableLiveData<Error>()

    val placedCoordinates = MutableLiveData<Coordinates>()
    val placedPiece = MutableLiveData<PieceCoordinatesDto>()

    val pieces = (0 until 10).map { arrayOfNulls<Piece>(10).toMutableList() }.toMutableList()

    private val initialAvailablePieces = mapOf(
            SOLDIER to 8,
            BOMB to 6,
            GUNNER to 5,
            SERGEANT to 4,
            TENANT to 4,
            CAPTAIN to 4,
            MAJOR to 3,
            COLONEL to 2,
            GENERAL to 1,
            MARSHAL to 1,
            SPY to 1,
            PRISONER to 1
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
        subscribeToStatus()
        subscribeToChats()
        subscribeToPlacedPieces()
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
    }

    private fun subscribeToStatus() {
        gameService
                .status
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    setStatus(it)

                    when(it) {
                        PLACING_PIECES -> {
                            resetAvailablePieces()
                        }
                        else -> Unit
                    }

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



    /**
     * Pieces Placing
     * */

    private fun resetAvailablePieces() {
        initialAvailablePieces.forEach { availablePieces[it.key] = it.value }
    }

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

        // Pre process piece
        if(dto.piece.belongsToOpponent) {
            dto.apply {
                var (newRow, newColumn) = dto.coordinates
                newRow = 9 - newRow
                newColumn = 9 - newColumn
                dto.coordinates = Coordinates(newRow, newColumn)
            }
        }

        // Place the piece
        val (row, column) = dto.coordinates
        pieces[row][column] = dto.piece.copy()
        placedPiece.value = dto

        // Reduce the current amount
        val oldAmount = availablePieces[dto.piece.kind] ?: return
        availablePieces[dto.piece.kind] = oldAmount - 1

        // Ready to play
        if(availablePiecesCount == 0) {
            gameService.sendMessage(MessageKind.READY.message)
        }

    }




    /**
     * View Actions
     * */

    fun onConnectTouched() {
        gameService.connect()
    }

    val didClickPiece: (Int, Int) -> Unit = click@ { row, column ->

        val status = this.status.value ?: return@click

        if(status.isPlacingPieces) {
            if(row < 6 || pieces[row][column] != null) {
                error.value = Error.PLACE_PIECE_INVALID_COORDINATES
                return@click
            }
            placedCoordinates.value = Coordinates(row, column)
        }

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
        PLACE_PIECE_INVALID_COORDINATES
    }

}