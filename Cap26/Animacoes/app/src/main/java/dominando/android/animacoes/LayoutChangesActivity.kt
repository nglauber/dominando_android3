package dominando.android.animacoes

import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.Button
import kotlinx.android.synthetic.main.activity_layout_changes.*
import android.R.attr.x
import android.graphics.Point
import android.view.Display



class LayoutChangesActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layout_changes)
        var count = 1
        val removeClickListener = View.OnClickListener { view ->
            llContainer.removeView(view)
        }
        btnAdd.setOnClickListener {
            llContainer.addView(Button(this).apply {
                text = "Button $count"
                setOnClickListener(removeClickListener)
            })
            count++
        }
        initTransition()
    }

    private fun initTransition() {
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        llContainer.layoutTransition = LayoutTransition().apply {
            val slideRight = ObjectAnimator.ofPropertyValuesHolder(
                null as Any?,
                PropertyValuesHolder.ofFloat(View.X, 0f, width.toFloat())
            )
            slideRight.duration = 300
            val scaleUp = ObjectAnimator.ofPropertyValuesHolder(
                null as Any?,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 0f, 1f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f, 1f)
            )
            scaleUp.duration = 300
            scaleUp.startDelay = 300
            scaleUp.interpolator = OvershootInterpolator()
            setAnimator(LayoutTransition.APPEARING, scaleUp)
            setAnimator(LayoutTransition.DISAPPEARING, slideRight)
        }
    }
}
