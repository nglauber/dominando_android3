package dominando.android.toques

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TouchActivity : AppCompatActivity() {
    private lateinit var txtTouchEvent: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        txtTouchEvent = TextView(this)
        setContentView(txtTouchEvent)
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        var log = "onTouch\n"
        val consumed = when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                log += "ACTION_DOWN\n"
                true
            }
            MotionEvent.ACTION_MOVE -> {
                log += "ACTION_MOVE\n"
                true
            }
            MotionEvent.ACTION_UP -> {
                log += "ACTION_UP\n"
                true
            }
            MotionEvent.ACTION_CANCEL -> {
                log += "ACTION_CANCEL\n"
                true
            }
            MotionEvent.ACTION_OUTSIDE -> {
                log += "ACTION_OUTSIDE\n"
                true
            }
            else -> false
        }
        val fingerOnScreen = event.pointerCount
        log += "Dedos na tela: $fingerOnScreen\n"
        for (i in 0 until fingerOnScreen) {
            log += "$i x=${event.getX(i)}, y=${ event.getY(i)}\n"
        }
        txtTouchEvent.text = log
        return consumed
    }
}

