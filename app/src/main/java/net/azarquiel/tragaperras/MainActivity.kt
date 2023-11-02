package net.azarquiel.tragaperras

import android.graphics.drawable.AnimationDrawable
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var tvdinero: TextView
    private var dinero: Int = 50
    private lateinit var mp: MediaPlayer
    private lateinit var random: Random
    private var rulando: Boolean = false
    private var frames = Array<AnimationDrawable>(3) { AnimationDrawable() }
    private var ivfichas = arrayOfNulls<ImageView>(3)
    private var credito = 50

    private var jugada = arrayOf(0,0,0)
    private lateinit var frame: AnimationDrawable
    private lateinit var ivficha: ImageView
    private var figuras = arrayOf( "campana", "cereza", "dolar", "fresa", "limon", "siete" )
    private var anis = arrayOf(R.anim.ani1, R.anim.ani2, R.anim.ani3)
    private var ivfichasids = arrayOf(R.id.ivficha1, R.id.ivficha2, R.id.ivficha3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Nos sonara la musica que hemos puesto
        mp = MediaPlayer.create(this, R.raw.maquina)
        mp.isLooping = true

        // Saldran en Aleatorio las fichas
        random = Random(System.currentTimeMillis())

        //Nos traeremos del activity_main las cosas que hemos echo
        ivficha = findViewById<ImageView>(R.id.ivficha3)
        tvdinero = findViewById<TextView>(R.id.tvdinero)
        var ibstop = findViewById<ImageButton>(R.id.ibstop)

        //Activaremos para que nos haga click
        ibstop.setOnClickListener { onClickStop() }

        makeViews()
    }

    private fun makeViews() {

        val llfichas = findViewById<LinearLayout>(R.id.llfichas)
        for (i in 0 until llfichas.childCount) {
            ivfichas[i] = llfichas.getChildAt(i) as ImageView
            ivfichas[i]!!.setBackgroundResource(anis[i])
            frames[i] = ivfichas[i]!!.background as AnimationDrawable
        }
    }

    private fun onClickStop() {
        if (rulando) return
        mp.start()
        for (i in 0 until 3) {
            ivfichas[i]!!.setImageResource(android.R.color.transparent)
            frames[i].start()
        }
        rulando = true
        paramos()
    }

    private fun paramos() {
        for (i in 0 until 3) {
            paraFicha(i)
        }
    }

    private fun compruebaJugada() {
        if (jugada[0]==jugada[1] && jugada[1]==jugada[2]) {
            if (jugada[0]==0) {
                credito += 50
            }
            else {
                credito += 10
            }
        }
        else {
            credito -= 1
        }
        if (credito == 0){
            mostrarMensajeSinDinero()
        }

        tvdinero.text = " $credito €"
    }

    private fun paraFicha(i: Int) {
        GlobalScope.launch() {
            SystemClock.sleep(2000+(i*500L))
            launch(Main) {
                val n = (0 until 6).random(random)
                jugada[i] = n
                val id = resources.getIdentifier(figuras[n], "drawable", packageName)
                ivfichas[i]!!.setImageResource(id)
                frames[i].stop()
                if (i==2) {
                    mp.pause()
                    rulando = false
                    compruebaJugada()
                }
            }
        }
    }

    private fun mostrarMensajeSinDinero() {

        AlertDialog.Builder(this)
            .setTitle("Te has quedado sin dinero")
            .setMessage("El juego ha terminado.")
            .setPositiveButton("Salir") { _, _ -> finish() }
            .show()
    }
}