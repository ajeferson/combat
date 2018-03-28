package br.com.ajeferson.combat.view.rmi

import br.com.ajeferson.combat.view.service.model.PlacedPiece
import br.com.ajeferson.combat.view.service.model.RmiMove

/**
 * Created by ajeferson on 26/03/2018.
 */

interface RmiServerInterface {
    fun serverJoin(ip: String)
    fun serverPlacePiece(placedPiece: PlacedPiece)
    fun serverReady(id: Int)
    fun serverMove(id: Int, move: RmiMove)
    fun serverStrike(id: Int, strike: RmiMove)
    fun serverChat(id: Int, message: String)
    fun serverRestart(id: Int)
    fun serverAnswerRestartRequest(id: Int, accepted: Boolean)
    fun serverGiveUp(id: Int)
}