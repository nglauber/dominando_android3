package dominando.android.animacoes

import android.animation.*
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.*
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import kotlinx.android.synthetic.main.activity_property_animations.*

class PropertyAnimationsActivity : BaseActivity() {

    private var listInterpolators = arrayOf(
        AccelerateDecelerateInterpolator(),
        AccelerateInterpolator(1.0f), // <- fator (opcional)
        AnticipateInterpolator(2.0f), // <- tensão (opcional)
        AnticipateOvershootInterpolator(2.0f, 1.5f), // <- tensão, tensão extra tension (opcional)
        BounceInterpolator(),
        CycleInterpolator(2f), // <- ciclos
        DecelerateInterpolator(1.0f), // <- fator (opcional)
        FastOutLinearInInterpolator(),
        FastOutSlowInInterpolator(),
        LinearOutSlowInInterpolator(),
        LinearInterpolator(),
        OvershootInterpolator(2.0f) // <- tensão (opcional)
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_property_animations)
        imgBazinga.setOnClickListener {
            executeAnimator()
        }
        txtArgb.setOnClickListener { animateText() }
    }
    private fun executeAnimator() {
        val animationIndex = spnAnimators.selectedItemPosition
        val interpolator = listInterpolators[spnInterpolators.selectedItemPosition]
//        executeViewPropertyAnimation(animationIndex, interpolator)
//        executeObjectAnimator(animationIndex, interpolator)
        executeObjectAnimatorFromXml(animationIndex, interpolator)
    }
    private fun executeViewPropertyAnimation(index: Int, interpolator: Interpolator) {
        when (index) {
            0 -> imgBazinga.animate()
                .alpha(0f)
                .setDuration(ANIMATION_DURATION)
                .setInterpolator(interpolator)
                .withEndAction {
                    imgBazinga.animate()
                        .alpha(1f)
                        .setDuration(ANIMATION_DURATION)
                        .start()
                }
            1 -> imgBazinga.animate()
                .rotation(360f)
                .setDuration(ANIMATION_DURATION)
                .withEndAction {
                    imgBazinga.animate()
                        .rotation(0f)
                        .setDuration(ANIMATION_DURATION)
                        .start()
                }
            2 -> imgBazinga.animate()
                .scaleX(2f)
                .scaleY(2f)
                .setDuration(ANIMATION_DURATION)
                .withEndAction {
                    imgBazinga.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(ANIMATION_DURATION)
                        .start()
                }
            3 -> imgBazinga.animate()
                .translationX(500f)
                .setDuration(ANIMATION_DURATION)
                .withEndAction {
                    imgBazinga.animate()
                        .translationX(0f)
                        .setDuration(ANIMATION_DURATION)
                        .start()
                }
            4 -> imgBazinga.animate()
                .alpha(0f)
                .rotation(360f)
                .scaleX(2f)
                .scaleY(2f)
                .translationX(500f)
                .setDuration(ANIMATION_DURATION)
                .withEndAction {
                    imgBazinga.animate()
                        .alpha(1f)
                        .rotation(0f)
                        .scaleX(1f)
                        .scaleY(1f)
                        .translationX(0f)
                        .setDuration(ANIMATION_DURATION)
                        .start()
                }
        }
    }

    private fun executeObjectAnimator(index: Int, interpolator: Interpolator) {
        val animList = mutableListOf<Animator>()

        val alphaAnim = ObjectAnimator.ofFloat(
            imgBazinga, View.ALPHA, 0f).apply {
            duration = ANIMATION_DURATION
            repeatCount = 1
            repeatMode = ValueAnimator.REVERSE
        }
        animList.add(alphaAnim)

        val rotateAnim = ObjectAnimator.ofFloat(
            imgBazinga, View.ROTATION, 360f).apply {
            duration = ANIMATION_DURATION
            repeatCount = 1
            repeatMode = ValueAnimator.REVERSE
        }
        animList.add(rotateAnim)

        val scaleX = ObjectAnimator.ofFloat(
            imgBazinga, View.SCALE_X, 2f).apply {
            repeatCount = 1
            repeatMode = ValueAnimator.REVERSE
            duration = ANIMATION_DURATION
        }
        val scaleY = ObjectAnimator.ofFloat(
            imgBazinga, View.SCALE_Y, 2f).apply {
            repeatCount = 1
            repeatMode = ValueAnimator.REVERSE
            duration = ANIMATION_DURATION
        }
        val set = AnimatorSet()
        set.playTogether(scaleX, scaleY)
        animList.add(set)

        val translAnim = ObjectAnimator.ofFloat(
            imgBazinga, View.TRANSLATION_X, 0f, 500f).apply {
            duration = ANIMATION_DURATION
            repeatMode = ValueAnimator.REVERSE
            repeatCount = 1
        }
        animList.add(translAnim)

        val allTogether = AnimatorSet()
        allTogether.playTogether(alphaAnim, rotateAnim, set, translAnim)
        animList.add(allTogether)

        animList[index].interpolator = interpolator
        animList[index].start()
    }

    private fun executeObjectAnimatorFromXml(index: Int, interpolator: Interpolator) {
        val animList = listOf(
            AnimatorInflater.loadAnimator(this, R.animator.fade),
            AnimatorInflater.loadAnimator(this, R.animator.rotation),
            AnimatorInflater.loadAnimator(this, R.animator.scale),
            AnimatorInflater.loadAnimator(this, R.animator.translate),
            AnimatorInflater.loadAnimator(this, R.animator.all_together)
        )
        animList[index].interpolator = interpolator
        animList[index].setTarget(imgBazinga)
        animList[index].start()
    }

    private fun animateText() {
        val textColorAnim = ObjectAnimator.ofArgb(
            txtArgb, "textColor",
            Color.BLACK, Color.WHITE).apply {
            duration = 1000L
            repeatMode = ValueAnimator.REVERSE
            repeatCount = 1
        }
        val bgColorAnim = ObjectAnimator.ofArgb(
            txtArgb, "backgroundColor",
            Color.WHITE, Color.RED).apply {
            duration = 1000L
            repeatMode = ValueAnimator.REVERSE
            repeatCount = 1
        }
        val set = AnimatorSet()
        set.playTogether(textColorAnim, bgColorAnim)
        set.start()
    }

    companion object {
        private const val ANIMATION_DURATION = 1000L
    }
}
