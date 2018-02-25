package br.com.ajeferson.combat.view.view.activity

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import br.com.ajeferson.combat.R

class ServerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server)
        //        val addr = InetAddress.getByName("127.0.0.1")
//        val sock = ServerSocket(1234, addr)
    }


    companion object {

        fun newIntent(context: Context) = Intent(context, ServerActivity::class.java)

    }
}
