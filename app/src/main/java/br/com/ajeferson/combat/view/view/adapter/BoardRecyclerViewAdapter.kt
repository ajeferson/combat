package br.com.ajeferson.combat.view.view.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import br.com.ajeferson.combat.R
import br.com.ajeferson.combat.databinding.RvItemBoardBinding
import br.com.ajeferson.combat.view.service.model.Coordinates
import br.com.ajeferson.combat.view.service.model.Piece
import br.com.ajeferson.combat.view.view.enumeration.BoardItemKind

/**
 * Created by ajeferson on 26/02/2018.
 */
class BoardRecyclerViewAdapter(
        board: List<List<BoardItemKind>>,
        pieces: List<List<Piece?>>): RecyclerView.Adapter<BoardRecyclerViewAdapter.ViewHolder>() {

    var board = board
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var pieces = pieces
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onItemClick: ((Int, Int) -> Unit)? = null

    private val size get() = board.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val binding = DataBindingUtil.inflate<RvItemBoardBinding>(inflater, R.layout.rv_item_board, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = Math.pow(size.toDouble(), 2.toDouble()).toInt()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (row, column) = position.toCoordinates(size)
        holder.bind(board[row][column], pieces[row][column], row, column, onItemClick)
    }

    class ViewHolder(private val binding: RvItemBoardBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(kind: BoardItemKind, piece: Piece?, row: Int, column: Int, onItemClick: ((Int, Int) -> Unit)?) {
            binding.kind = kind
            binding.piece = piece
            binding.root.setOnClickListener { onItemClick?.let { it(row, column) } }
            binding.executePendingBindings()
        }

    }

    private fun Int.toCoordinates(size: Int): Coordinates {
        val row = this / size
        val column = this - (size * row)
        return Coordinates.newInstance(row, column)
    }

}