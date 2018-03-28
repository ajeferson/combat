package br.com.ajeferson.combat.view.service.model

import java.io.Serializable

/**
 * Created by ajeferson on 27/03/2018.
 */
data class PlacedPiece(val kind: String, val row: Int, val column: Int, val senderId: Int = -1): Serializable