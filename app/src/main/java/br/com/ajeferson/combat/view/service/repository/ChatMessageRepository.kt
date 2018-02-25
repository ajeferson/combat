package br.com.ajeferson.combat.view.service.repository

import br.com.ajeferson.combat.view.service.model.ChatMessage
import io.reactivex.Observable

/**
 * Created by ajeferson on 25/02/2018.
 */
// TODO pass the connection on the constructor
class ChatMessageRepository {

    // TODO Could be a flowable
    var messages: Observable<ChatMessage>

    init {

        val chatMessages = listOf(
                ChatMessage("Hey", ChatMessage.Kind.SELF),
                ChatMessage("Hi there", ChatMessage.Kind.ENEMY),
                ChatMessage("How you doing?", ChatMessage.Kind.SELF)
        )

        messages = Observable.create { emitter ->

            chatMessages.forEach {
                Thread.sleep(2000L)
                emitter.onNext(it)
            }

        }

    }


}