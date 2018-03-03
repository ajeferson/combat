package br.com.ajeferson.combat.view.service.model

import br.com.ajeferson.combat.view.view.enumeration.Owner
import br.com.ajeferson.combat.view.view.enumeration.PieceKind

/**
 * Created by ajeferson on 02/03/2018.
 */
data class Strike(
        val from: Coordinates,
        val to: Coordinates,
        var striker: PieceKind? = null,
        var defender: PieceKind? = null,
        var owner: Owner? = null)