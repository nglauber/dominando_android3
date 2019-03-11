package dominando.android.toques

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.core.view.GestureDetectorCompat

class GestureActivity : AppCompatActivity() {
    private lateinit var gestureDetector: GestureDetectorCompat

    private val doubleTapListener: GestureDetector.OnDoubleTapListener =
        object : GestureDetector.OnDoubleTapListener {
            override fun onSingleTapConfirmed(motionEvent: MotionEvent): Boolean {
                Log.d(TAG, "onSingleTapConfirmed")
                return true
            }
            override fun onDoubleTap(motionEvent: MotionEvent): Boolean {
                Log.d(TAG, "onDoubleTap")
                return true
            }
            override fun onDoubleTapEvent(motionEvent: MotionEvent): Boolean {
                Log.d(TAG, "onDoubleTapEvent")
                return true
            }
        }

    private val gestureListener: GestureDetector.OnGestureListener =
        object : GestureDetector.OnGestureListener {
            override fun onDown(motionEvent: MotionEvent): Boolean {
                Log.d(TAG, "onDown")
                return true
            }
            override fun onShowPress(motionEvent: MotionEvent) {
                Log.d(TAG, "onShowPress")
            }
            override fun onSingleTapUp(motionEvent: MotionEvent): Boolean {
                Log.d(TAG, "onSingleTapUp")
                return true
            }
            override fun onScroll(motionEvent: MotionEvent, motionEvent2: MotionEvent,
                                  v: Float, v2: Float): Boolean {
                Log.d(TAG, "onScroll")
                return true
            }
            override fun onLongPress(motionEvent: MotionEvent) {
                Log.d(TAG, "onLongPress")
            }
            override fun onFling(motionEvent: MotionEvent, motionEvent2: MotionEvent,
                                 v: Float, v2: Float): Boolean {
                Log.d(TAG, "onFling")
                return true
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gesture)
        gestureDetector = GestureDetectorCompat(this, gestureListener)
        gestureDetector.setOnDoubleTapListener(doubleTapListener)
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }
    companion object {
        private val TAG = "DominandoAndroid"
    }
}
