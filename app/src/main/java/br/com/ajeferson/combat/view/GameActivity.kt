package br.com.ajeferson.combat.view

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import br.com.ajeferson.combat.R
import java.net.Socket

class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        val runnable = Runnable {
            val socket = Socket("192.168.0.106", 3000)
            println(socket)
        }
        Thread(runnable).start()

    }

    companion object {

        fun newIntent(context: Context) = Intent(context, GameActivity::class.java)

    }

}
