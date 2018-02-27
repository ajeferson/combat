package br.com.ajeferson.combat.view.service.model

import java.util.*

/**
 * Created by ajeferson on 25/02/2018.
 */
data class ChatMessage(val text: String, val kind: Kind) {

    val date = Date()

    enum class Kind {
        SELF,
        ENEMY,
        LOG
    }

}