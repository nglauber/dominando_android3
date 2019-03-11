package dominando.android.toques

import android.os.Bundle
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_pinch.*

class PinchActivity : AppCompatActivity() {
    private lateinit var scaleDetector: ScaleGestureDetector
    private var scaleValue: Float = 0.toFloat()
    private var origWidth: Int = 0
    private var origHeight: Int = 0
    private val scaleListener =
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                scaleValue *= detector.scaleFactor
                scaleValue = Math.max(1.0f, Math.min(scaleValue, 7.0f))
                val lp = imageView.layoutParams
                lp.width = (origWidth * scaleValue).toInt()
                lp.height = (origHeight * scaleValue).toInt()
                imageView.layoutParams = lp
                return true
            }
        }
    private val imageViewGlobalLayoutListener =
        object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                origWidth = imageView.width
                origHeight = imageView.height
                imageView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        }
    private val imageViewTouchListener =
        View.OnTouchListener { view, motionEvent ->
            scaleDetector.onTouchEvent(motionEvent)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pinch)
        scaleDetector = ScaleGestureDetector(this, scaleListener)
        imageView.setOnTouchListener(imageViewTouchListener)
        imageView.viewTreeObserver.addOnGlobalLayoutListener(
            imageViewGlobalLayoutListener
        )
    }
}
