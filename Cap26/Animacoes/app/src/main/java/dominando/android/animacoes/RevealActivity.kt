package dominando.android.animacoes

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.ConstraintSet.*
import androidx.transition.*
import kotlinx.android.synthetic.main.activity_reveal.*

class RevealActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reveal)
        fabAdd.setOnClickListener { showCard() }
        viewCard.setOnClickListener { hideCard() }
    }
    private fun showCard() {
        moveFab(true)
    }
    private fun hideCard() {
        revealAnimation(false, viewCard.width, fabAdd.width)
    }
    private fun revealAnimation(forward: Boolean, initialRadius: Int, finalRadius: Int) {
        val cx = viewCard.width / 2
        val cy = viewCard.height / 2
        val anim = ViewAnimationUtils.createCircularReveal(viewCard, cx, cy,
            initialRadius.toFloat(), finalRadius.toFloat())
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                if (!forward) {
                    viewCard.visibility = View.INVISIBLE
                    fabAdd.visibility = View.VISIBLE
                    moveFab(false)
                }
            }
        })
        if (forward) {
            viewCard.visibility = View.VISIBLE
        }
        anim.start()
    }
    private fun moveFab(forward: Boolean) {
        val transition = TransitionSet().apply {
            addTransition(ChangeBounds())
            addTransition(ChangeTransform())
            ordering = TransitionSet.ORDERING_TOGETHER
            duration = resources
                .getInteger(android.R.integer.config_mediumAnimTime)
                .toLong()
            setPathMotion(ArcMotion())
            addListener(object : TransitionListenerAdapter() {
                override fun onTransitionEnd(transition: Transition) {
                    if (forward) {
                        fabAdd.visibility = View.INVISIBLE
                        revealAnimation(true, fabAdd.width, viewCard.width)
                    }
                }
            })
        }
        val constraintSet = ConstraintSet()
        constraintSet.clone(rootLayout)
        TransitionManager.beginDelayedTransition(rootLayout, transition)
        if (forward) {
            with(constraintSet) {
                clear(R.id.fabAdd, BOTTOM)
                clear(R.id.fabAdd, END)
                listOf(TOP, START, BOTTOM, END).forEach {
                    connect(R.id.fabAdd, it, R.id.viewCard, it, 0)
                }
            }
            constraintSet.applyTo(rootLayout)
            fabAdd.scaleX = 2f
            fabAdd.scaleY = 2f
        } else {
            with(constraintSet) {
                val margin = resources.getDimension(R.dimen.default_margin).toInt()
                listOf(TOP, START, BOTTOM, END).forEach {
                    clear(R.id.fabAdd, it)
                }
                connect(R.id.fabAdd, BOTTOM, PARENT_ID, BOTTOM, margin)
                connect(R.id.fabAdd, END, PARENT_ID, END, margin)
            }
            constraintSet.applyTo(rootLayout)
            fabAdd.scaleX = 1.0f
            fabAdd.scaleY = 1.0f
        }
    }
}
