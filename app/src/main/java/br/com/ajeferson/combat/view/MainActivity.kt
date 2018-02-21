package br.com.ajeferson.combat.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import br.com.ajeferson.combat.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        goToServerActivitiyButton.setOnClickListener { goToServerActivity() }
        goToGameActivityButton.setOnClickListener { goToGameActivity() }
    }

    private fun goToServerActivity() {
        startActivity(ServerActivity.newIntent(this))
    }

    private fun goToGameActivity() {
        startActivity(GameActivity.newIntent(this))
    }

}
