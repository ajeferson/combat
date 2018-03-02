package br.com.ajeferson.combat.view.service.message

/**
 * Created by ajeferson on 02/03/2018.
 */
enum class MessageKind {

    WAIT_OPPONENT;

    val message get() = Message.from(this.toString())

}