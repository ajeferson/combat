package br.com.ajeferson.combat.view.service.model

import br.com.ajeferson.combat.view.view.enumeration.Owner

/**
 * Created by ajeferson on 02/03/2018.
 */
class Coordinates private constructor(val row: Int, val column: Int) {

    companion object {

        fun newInstance(owner: Owner, row: Int, column: Int): Coordinates {
            return Coordinates(
                    if(owner.isSelf) row else 9 - row,
                    if(owner.isSelf) column else 9 - column
            )
        }

        fun newInstance(row: Int, column: Int): Coordinates {
            return Coordinates(row, column)
        }

    }

    operator fun component1() = row

    operator fun component2() = column

}