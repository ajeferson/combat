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
import br.com.ajeferson.combat.view.service.model.Piece
import br.com.ajeferson.combat.view.view.enumeration.BoardItemKind
import br.com.ajeferson.combat.view.view.enumeration.BoardItemKind.*
import br.com.ajeferson.combat.view.view.enumeration.Owner.*
import br.com.ajeferson.combat.view.view.enumeration.PieceKind.*
import br.com.ajeferson.combat.view.viewmodel.GameViewModel.Status.*
import io.reactivex.Scheduler
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

    private fun setStatus(status: Status) {
        liveStatus.value = status
        this.status.value = status
    }

    fun onCreate() {
        subscribeToConnectionStatus()
        subscribeToChatMessages()
        subscribeToLogMessages()
    }

    fun onStart() {
    }

    fun onResume() {

    }

    fun onDestroy() {

    }

    private fun subscribeToChatMessages() {
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

    private fun subscribeToLogMessages() {
        connectionManager
                .messages
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    if(it.kind.isChat) {
                        return@subscribe
                    }

                    setStatus(when(it.kind) {
                        MessageKind.WAIT_OPPONENT -> WAITING_OPPONENT
                        else -> NONE
                    })

                }, {
                    // TODO Handle
                })
    }

    fun onConnectTouched() {
        connectionManager.connect()
    }

    val onPieceClick: (Int, Int) -> Unit = { row, column ->
        println("Selected $row $column")
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
        DISCONNECTED, // Not connected to server at all
        CONNECTING, // Waiting for a response to the connection request
        CONNECTED, // Connected to server
        WAITING_OPPONENT, // Waiting for the opponent to connect
        PLACING_PIECES, // Should now starting placing pieces
        WAITING_PLACING, // Waiting for opponent to finish placing his/her pieces
        GAME_STARTED, //
        NONE,
        PLACING,

        PLAYING
    }

}