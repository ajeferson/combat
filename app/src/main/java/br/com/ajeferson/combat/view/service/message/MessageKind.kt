package br.com.ajeferson.combat.view.service.message

/**
 * Created by ajeferson on 02/03/2018.
 */
enum class MessageKind {

    WAIT_OPPONENT,
    CHAT;

    val message get() = Message.from(this.toString())

    val isChat get() = this == CHAT

}