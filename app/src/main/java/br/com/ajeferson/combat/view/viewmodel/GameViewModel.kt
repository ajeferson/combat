package br.com.ajeferson.combat.view.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import br.com.ajeferson.combat.view.extension.value
import br.com.ajeferson.combat.view.service.connection.ConnectionManager
import br.com.ajeferson.combat.view.service.model.ChatMessage
import br.com.ajeferson.combat.view.service.repository.ChatRepository
import br.com.ajeferson.combat.view.service.connection.ConnectionManager.ConnectionStatus
import br.com.ajeferson.combat.view.service.message.MessageKind
import br.com.ajeferson.combat.view.service.model.Coordinates
import br.com.ajeferson.combat.view.service.model.Piece
import br.com.ajeferson.combat.view.view.enumeration.BoardItemKind
import br.com.ajeferson.combat.view.view.enumeration.BoardItemKind.*
import br.com.ajeferson.combat.view.view.enumeration.Owner.*
import br.com.ajeferson.combat.view.view.enumeration.PieceKind
import br.com.ajeferson.combat.view.view.enumeration.PieceKind.*
import br.com.ajeferson.combat.view.viewmodel.GameViewModel.Status.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by ajeferson on 20/02/2018.
 */
class GameViewModel(private val connectionManager: ConnectionManager,
                    private val chatRepository: ChatRepository): ViewModel() {

    val messages = MutableLiveData<ChatMessage>()
    val status = ObservableField(Status.DISCONNECTED)
    val liveStatus = MutableLiveData<Status>()

    val placedPieceCoordinates = MutableLiveData<Coordinates>()

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

    private fun setStatus(status: Status) {
        liveStatus.value = status
        this.status.value = status
    }

    fun onCreate() {
        subscribeToConnectionStatus()
        subscribeToChat()
        subscribeToMessages()
    }

    fun onStart() {
    }

    fun onResume() {

    }

    fun onDestroy() {

    }

    private fun subscribeToChat() {
        chatRepository
                .messages
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    messages.value = it
                }
    }

    private fun subscribeToConnectionStatus() {
        connectionManager
                .status
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    setStatus(when(it) {
                        ConnectionStatus.CONNECTING -> CONNECTING
                        ConnectionStatus.CONNECTED -> CONNECTED
                        else -> DISCONNECTED
                    })
                }, {
                    // TODO Handle this
                    setStatus(DISCONNECTED)
                })
    }

    private fun subscribeToMessages() {
        connectionManager
                .messages
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    if(it.kind.isChat) {
                        return@subscribe
                    }

                    // TODO Refactor
                    setStatus(when(it.kind) {
                        MessageKind.WAIT_OPPONENT -> WAITING_OPPONENT
                        MessageKind.OPPONENT_GIVE_UP -> OPPONENT_GIVE_UP
                        MessageKind.PLACE_PIECES -> {
                            resetAvailablePieces()
                            PLACING_PIECES
                        }
                        MessageKind.CHAT -> NONE
                    })

                }, {
                    // TODO Handle
                })
    }

    fun onConnectTouched() {
        connectionManager.connect()
    }


    private fun resetAvailablePieces() {
        initialAvailablePieces.forEach { availablePieces[it.key] = it.value }
    }

    val didClickPiece: (Int, Int) -> Unit = click@ { row, column ->

        val status = this.status.value ?: return@click

        if(status.isPlacingPieces) {
            placedPieceCoordinates.value = Coordinates(row, column)
        }

    }

    fun selectPiece(pieceKind: PieceKind) {

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

    val pieces = (0 until 10).map { arrayOfNulls<Piece>(10).toMutableList() }.toMutableList()

    init {
        pieces[0][0] = Piece(COLONEL, SELF)
    }


    enum class Status {
        NONE,
        DISCONNECTED,           // Not connected to server at all
        CONNECTING,             // Waiting for a response to the connection request
        CONNECTED,              // Connected to server
        WAITING_OPPONENT,       // Waiting for the opponent to connect
        OPPONENT_GIVE_UP,       // Opponent has given up
        PLACING_PIECES;         // Should now starting placing pieces

        val isPlacingPieces get() = this == PLACING_PIECES

    }

}