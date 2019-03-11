package dominando.android.sensores

import java.util.concurrent.TimeUnit
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.view.SurfaceHolder

class ThreadGame(context: Context,
                 private val ball: Ball,
                 private val surfaceHolder: SurfaceHolder) : Thread() {
    private var isRunning: Boolean = false
    private val backgroundImage: Bitmap = BitmapFactory.decodeResource(
        context.resources, R.drawable.grass)

    override fun run() {
        isRunning = true
        while (isRunning) {
            try {
                ball.applyPhysics()
                drawGame()
                TimeUnit.MILLISECONDS.sleep(16)
            } catch (ie: InterruptedException) {
                isRunning = false
            }
        }
    }
    fun stopGame() {
        isRunning = false
        interrupt()
    }
    private fun drawGame() {
        var canvas: Canvas? = null
        try {
            canvas = surfaceHolder.lockCanvas()
            if (canvas != null) {
                val rect = Rect(0, 0, canvas.width, canvas.height)
                canvas.drawBitmap(backgroundImage, null, rect, null)
                canvas.drawBitmap(ball.ballImage,
                    ball.xPos, ball.yPos, null)
            }
        } finally {
            if (canvas != null) {
                surfaceHolder.unlockCanvasAndPost(canvas)
            }
        }
    }
}
