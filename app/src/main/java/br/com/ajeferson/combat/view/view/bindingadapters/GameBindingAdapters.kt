package br.com.ajeferson.combat.view.view.bindingadapters

import android.databinding.BindingAdapter
import android.view.View
import br.com.ajeferson.combat.R
import br.com.ajeferson.combat.view.view.enumeration.BoardItemKind

/**
 * Created by ajeferson on 26/02/2018.
 */
@BindingAdapter("boardItemKind")
fun setBoardKindOnTextView(view: View, kind: BoardItemKind?) {
    view.setBackgroundResource(when(kind) {
        BoardItemKind.LAND -> R.color.brownLand
        else -> R.color.blueWater
    })
}