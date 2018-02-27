package br.com.ajeferson.combat.view.view.bindingadapters

import android.databinding.BindingAdapter
import android.view.View
import android.widget.ImageView
import br.com.ajeferson.combat.R
import br.com.ajeferson.combat.view.service.model.Piece
import br.com.ajeferson.combat.view.view.enumeration.BoardItemKind
import br.com.ajeferson.combat.view.view.enumeration.Owner

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

@BindingAdapter("piece")
fun setPieceOnImage(imageView: ImageView, piece: Piece?) {
    piece?.let {
        imageView.setBackgroundResource(when(it.owner) {
            Owner.SELF -> it.kind.resId
            else -> R.drawable.ic_question_mark
        })
    }
}