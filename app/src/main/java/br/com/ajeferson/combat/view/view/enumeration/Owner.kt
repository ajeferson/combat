package br.com.ajeferson.combat.view.view.enumeration

/**
 * Created by ajeferson on 27/02/2018.
 */
enum class Owner(val description: String) {

    SELF("Você:"),
    OPPONENT("Oponente:"),
    SERVER("Servidor");

    val isSelf get() = this == SELF
    val isOpponent get() = this == OPPONENT
    val isServer get() = this == SERVER

}