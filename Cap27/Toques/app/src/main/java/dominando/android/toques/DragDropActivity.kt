package dominando.android.toques

import android.content.ClipData
import android.os.Build
import android.os.Bundle
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_drag_drop.*

class DragDropActivity : AppCompatActivity(), View.OnTouchListener, View.OnDragListener {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drag_drop)
        img1.setOnTouchListener(this)
        img2.setOnTouchListener(this)
        img3.setOnTouchListener(this)
        img4.setOnTouchListener(this)
        llTopLeft.setOnDragListener(this)
        llTopRight.setOnDragListener(this)
        llBottomLeft.setOnDragListener(this)
        llBottomRight.setOnDragListener(this)
    }
    override fun onTouch(view: View, me: MotionEvent): Boolean {
        val action = me.action
        if (action == MotionEvent.ACTION_DOWN) {
            val data = ClipData.newPlainText("", "")
            val shadowBuilder = View.DragShadowBuilder(view)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)  {
                view.startDragAndDrop(data, shadowBuilder, view, 0)
            } else {
                view.startDrag(data, shadowBuilder, view, 0)
            }
            view.visibility = View.INVISIBLE
            return true
        }
        return false
    }
    override fun onDrag(v: View, event: DragEvent): Boolean {
        when (event.action) {
            DragEvent.ACTION_DRAG_ENTERED -> v.setBackgroundResource(R.drawable.bg_hover)
            DragEvent.ACTION_DRAG_EXITED -> v.setBackgroundResource(R.drawable.bg)
            DragEvent.ACTION_DROP -> {
                val view = event.localState as View
                val owner = view.parent as ViewGroup
                owner.removeView(view)
                val container = v as LinearLayout
                container.addView(view)
                view.visibility = View.VISIBLE
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                v.setBackgroundResource(R.drawable.bg)
                val view2 = event.localState as View
                view2.visibility = View.VISIBLE
            }
        }
        return true
    }
}
