package br.com.ajeferson.combat.view.service.model

import br.com.ajeferson.combat.view.view.enumeration.Owner
import br.com.ajeferson.combat.view.view.enumeration.PieceKind

/**
 * Created by ajeferson on 27/02/2018.
 */
data class Piece(
        val kind: PieceKind,
        var owner: Owner) {

    val belongsToSelf get() = owner == Owner.SELF
    val belongsToOpponent get() = owner == Owner.OPPONENT

}