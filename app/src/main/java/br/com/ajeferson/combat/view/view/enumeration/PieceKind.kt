package br.com.ajeferson.combat.view.view.enumeration

import br.com.ajeferson.combat.R

/**
 * Created by ajeferson on 27/02/2018.
 */
enum class PieceKind(val descriptionId: Int, val resId: Int) {
    SPY(R.string.spy, R.drawable.ic_spy),
    SOLDIER(R.string.soldier, R.drawable.ic_soldier),
    GUNNER(R.string.gunner, R.drawable.ic_gunner),
    SERGEANT(R.string.sergeant, R.drawable.ic_sergeant),
    TENANT(R.string.tenant, R.drawable.ic_tenant),
    CAPTAIN(R.string.captain, R.drawable.ic_captain),
    MAJOR(R.string.major, R.drawable.ic_major),
    COLONEL(R.string.colonel, R.drawable.ic_colonel),
    GENERAL(R.string.general, R.drawable.ic_general),
    MARSHAL(R.string.marshal, R.drawable.ic_marshall),
    PRISONER(R.string.prisoner, R.drawable.ic_prisoner),
    BOMB(R.string.bomb, R.drawable.ic_bomb)
}