package dominando.android.sensores

import android.content.Context
import android.graphics.BitmapFactory

class Ball(context: Context) {
    var xPos: Float = 0f
        private set
    var yPos: Float = 0f
        private set
    private var xSpeed: Float = 0f
    private var ySpeed: Float = 0f
    private var xAcceleration: Float = 0f
    private var yAcceleration: Float = 0f
    private var screenWidth: Float = 0f
    private var screenHeight: Float = 0f
    private var lastUpdateTime: Long = -1
    val ballImage = BitmapFactory.decodeResource(
        context.resources, R.drawable.ball)

    fun setAcceleration(x: Float, y: Float) {
        xAcceleration = -x
        yAcceleration = y
        applyPhysics()
    }
    fun setScreenDimension(w: Int, h: Int) {
        screenWidth = w.toFloat()
        screenHeight = h.toFloat()
    }
    fun applyPhysics() {
        if (screenWidth <= 0 || screenHeight <= 0) return
        val timestamp = System.currentTimeMillis()
        if (lastUpdateTime < 0) {
            lastUpdateTime = timestamp
            return
        }
        val elapsedTime = timestamp - lastUpdateTime
        lastUpdateTime = timestamp
        xSpeed += xAcceleration * elapsedTime / 1000 * PIXELS_PER_METER
        ySpeed += yAcceleration * elapsedTime / 1000 * PIXELS_PER_METER
        xPos += xSpeed * elapsedTime / 1000 * PIXELS_PER_METER
        yPos += ySpeed * elapsedTime / 1000 * PIXELS_PER_METER
        var reboundInX = false
        var reboundInY = false
        if (yPos < 0) {
            yPos = 0f
            ySpeed = -ySpeed * REBOUND
            reboundInY = true
        } else if (yPos + ballImage.height > screenHeight) {
            yPos = screenHeight - ballImage.height
            ySpeed = -ySpeed * REBOUND
            reboundInY = true
        }
        if (reboundInY && Math.abs(ySpeed) < MIN_SPEED) {
            ySpeed = 0f
        }
        if (xPos < 0) {
            xPos = 0f
            xSpeed = -xSpeed * REBOUND
            reboundInX = true
        } else if (xPos + ballImage.width > screenWidth) {
            xPos = screenWidth - ballImage.width
            xSpeed = -xSpeed * REBOUND
            reboundInX = true
        }
        if (reboundInX && Math.abs(xSpeed) < MIN_SPEED) {
            xSpeed = 0f
        }
    }
    companion object {
        private const val REBOUND = 0.6f
        private const val MIN_SPEED = 5f
        private const val PIXELS_PER_METER = 25f
    }
}
