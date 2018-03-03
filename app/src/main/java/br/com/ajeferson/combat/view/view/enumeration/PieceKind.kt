package br.com.ajeferson.combat.view.view.enumeration

import br.com.ajeferson.combat.R

/**
 * Created by ajeferson on 27/02/2018.
 */
enum class PieceKind(val descriptionId: Int, val resId: Int, val strength: Int) {

    SPY(R.string.spy, R.drawable.ic_spy, 1),
    SOLDIER(R.string.soldier, R.drawable.ic_soldier, 2),
    GUNNER(R.string.gunner, R.drawable.ic_gunner, 3),
    SERGEANT(R.string.sergeant, R.drawable.ic_sergeant, 4),
    TENANT(R.string.tenant, R.drawable.ic_tenant, 5),
    CAPTAIN(R.string.captain, R.drawable.ic_captain, 6),
    MAJOR(R.string.major, R.drawable.ic_major, 7),
    COLONEL(R.string.colonel, R.drawable.ic_colonel, 8),
    GENERAL(R.string.general, R.drawable.ic_general, 9),
    MARSHAL(R.string.marshal, R.drawable.ic_marshall, 10),
    PRISONER(R.string.prisoner, R.drawable.ic_prisoner, 0),
    BOMB(R.string.bomb, R.drawable.ic_bomb, 0)

}

infix fun PieceKind.beats(that: PieceKind): Boolean? {

    // Spy is the only one who can defeat the marshall, but if marshall strikes first...
    if(this == PieceKind.SPY && that == PieceKind.MARSHAL || this == PieceKind.MARSHAL && that == PieceKind.SPY) {
        return true
    }

    // Gunners can beat bombs
    if(this == PieceKind.GUNNER && that == PieceKind.BOMB) {
        return true
    }

    if(this == PieceKind.BOMB && that == PieceKind.GUNNER) {
        return false
    }

    if(this == that) {
        return null
    }

    // Bombs defeat any others
    if(this == PieceKind.BOMB) {
        return null
    }

    if(that == PieceKind.BOMB) {
        return null
    }

    return this.strength > that.strength

}