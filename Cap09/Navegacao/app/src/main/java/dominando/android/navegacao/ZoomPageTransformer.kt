package dominando.android.navegacao

import android.view.View
import androidx.viewpager.widget.ViewPager

class ZoomPageTransformer : ViewPager.PageTransformer {
    override fun transformPage(view: View, position: Float) {
        val minScale = 0.85f
        val minAlpha = 0.5f
        val pageWidth = view.width
        val pageHeight = view.height
        when {
            (position < -1) ->
                view.alpha = 0f
            (position <= 1) -> {
                val scaleFactor = Math.max(minScale, 1 - Math.abs(position))
                val vertMargin = pageHeight * (1 - scaleFactor) / 2
                val horzMargin = pageWidth * (1 - scaleFactor) / 2
                view.apply {
                    translationX = if (position < 0) {
                        horzMargin - vertMargin / 2
                    } else {
                        -horzMargin + vertMargin / 2
                    }
                    scaleX = scaleFactor
                    scaleY = scaleFactor
                    alpha = minAlpha + (scaleFactor - minScale) /
                            (1 - minScale) * (1 - minAlpha)
                }
            }
            else ->
                view.alpha = 0f
        }
    }
}
