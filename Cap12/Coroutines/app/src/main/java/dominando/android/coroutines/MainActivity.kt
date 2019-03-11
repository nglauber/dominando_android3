package dominando.android.coroutines

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        job = Job()
        button1.setOnClickListener {
            carregarTarefasSequenciais()
        }
        button2.setOnClickListener {
            carregarTarefasParalelas()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
    suspend fun tarefa1(): String {
        Log.d("NGVL", "tarefa1 -> INICIO ${Thread.currentThread().name}")
        delay(5000)
        Log.d("NGVL", "tarefa1 -> FIM")
        return "Tarefa1"
    }
    suspend fun tarefa2(): String {
        Log.d("NGVL", "tarefa2 -> INICIO ${Thread.currentThread().name}")
        delay(3000)
        Log.d("NGVL", "tarefa2 -> FIM")
        return "Tarefa2"
    }
    private fun carregarTarefasSequenciais() {
        launch {
            val a = withContext(Dispatchers.Default) { tarefa1() }
            val b = withContext(Dispatchers.IO) { tarefa2() }
            button1.text = "$a $b"
        }
    }
    private fun carregarTarefasParalelas() {
        launch {
            coroutineScope {
                val a = async(Dispatchers.IO) { tarefa1() }
                val b = async (Dispatchers.Default)  { tarefa2() }
                button2.text = "${a.await()} ${b.await()}"
            }
        }
    }
}

