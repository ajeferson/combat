package br.com.ajeferson.combat.view.service.model

import br.com.ajeferson.combat.view.view.enumeration.Owner
import java.util.*

/**
 * Created by ajeferson on 25/02/2018.
 */
data class ChatMessage(val text: String, val kind: Owner) {

    val date = Date()

}