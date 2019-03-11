package dominando.android.customview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        jogoDaVelha.listener = object : JogoDaVelhaView.JogoDaVelhaListener {
            override fun fimDeJogo(vencedor: Int) {
                val mensagem = when (vencedor) {
                    JogoDaVelhaView.XIS -> "X venceu"
                    JogoDaVelhaView.BOLA -> "O venceu"
                    else -> "Empatou"
                }
                Toast.makeText(this@MainActivity, mensagem, Toast.LENGTH_LONG).show()
            }
        }
        button.setOnClickListener { jogoDaVelha.reiniciarJogo() }
    }
}

