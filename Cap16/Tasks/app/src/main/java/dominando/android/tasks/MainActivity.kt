package dominando.android.tasks

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import java.lang.ref.WeakReference
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        handler = Handler()
        btnStart.setOnClickListener {
            btnStart.isEnabled = false
            handler.post(MyThread(this))
        }
    }
    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
    }
    private class MyThread(activity: MainActivity) : Thread() {
        private val activityRef = WeakReference<MainActivity>(activity)
        private var count = 0
        override fun run() {
            super.run()
            activityRef.get()?.let { activity ->
                if (count < 10) {
                    count++
                    activity.txtMessage.text = "Contador: $count"
                    activity.handler.postDelayed(this, 1000)
                } else {
                    count = 0
                    activity.txtMessage.text = "Acabou!"
                    activity.btnStart.isEnabled = true
                }
            }
        }
    }
}

