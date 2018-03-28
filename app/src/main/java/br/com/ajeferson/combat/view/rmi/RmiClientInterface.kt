package br.com.ajeferson.combat.view.rmi

import br.com.ajeferson.combat.view.service.model.PlacedPiece
import br.com.ajeferson.combat.view.service.model.RmiMove

/**
 * Created by ajeferson on 26/03/2018.
 */
interface RmiClientInterface {
    fun clientWaitOpponent()
    fun clientPlacePieces(id: Int)
    fun clientPlacePiece(placedPiece: PlacedPiece)
    fun clientTurn()
    fun clientOpponentTurn()
    fun clientMove(id: Int, rmiMove: RmiMove)
    fun clientStrike(id: Int, rmiStrike: RmiMove)
    fun clientChat(id: Int, message: String)
    fun clientRestartRequest()
    fun clientRestartAnswer(accepted: Boolean)
    fun clientGiveUp()
    fun clientKeepAlive()
}