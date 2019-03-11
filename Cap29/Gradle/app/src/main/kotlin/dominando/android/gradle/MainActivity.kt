package dominando.android.gradle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dominando.android.mylib.LibActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.text = "${Helper.message} ${getString(R.string.msg_text)}"
        button.setOnClickListener {
            startActivity(Intent(this, LibActivity::class.java))
        }
    }
}