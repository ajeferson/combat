package br.com.ajeferson.combat.view.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import br.com.ajeferson.combat.view.extension.value
import br.com.ajeferson.combat.view.service.model.ChatMessage
import br.com.ajeferson.combat.view.service.repository.ChatMessageRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by ajeferson on 20/02/2018.
 */
class GameViewModel: ViewModel() {

    val messages = MutableLiveData<ChatMessage>()
    val status = ObservableField(Status.NONE)
    val liveStatus = MutableLiveData<Status>()

    private fun setStatus(status: Status) {
        liveStatus.value = status
        this.status.value = status
    }

    fun onCreate() {
        subscribeToChatMessages()

    }

    fun onStart() {
    }

    fun onResume() {
    }

    fun onDestroy() {

    }

    private fun subscribeToChatMessages() {

        val repository = ChatMessageRepository()
        repository
                .messages
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    messages.value = it
                }

    }


    enum class Status {
        NONE,
        PLAYING

    }

}