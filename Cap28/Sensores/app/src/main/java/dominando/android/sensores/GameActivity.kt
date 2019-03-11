package dominando.android.sensores

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceHolder.Callback
import android.view.SurfaceView

class GameActivity : Activity(), SensorEventListener, Callback {
    private val surfaceView: SurfaceView by lazy {
        SurfaceView(this).apply {
            keepScreenOn = true
        }
    }
    private val surfaceHolder: SurfaceHolder by lazy {
        surfaceView.holder
    }
    private val ball: Ball by lazy {
        Ball(this)
    }
    private val sensorManager: SensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    private var threadGame: ThreadGame? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        surfaceHolder.addCallback(this)
        setContentView(surfaceView)
    }
    override fun onResume() {
        super.onResume()
        val accelerometer = sensorManager.getDefaultSensor(
            Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accelerometer,
            SensorManager.SENSOR_DELAY_GAME)
    }
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int,
                                height: Int) {
        ball.setScreenDimension(width, height)
    }
    override fun surfaceCreated(holder: SurfaceHolder) {
        threadGame = ThreadGame(this, ball, holder)
        threadGame?.start()
    }
    override fun surfaceDestroyed(holder: SurfaceHolder) {
        try {
            ball.setScreenDimension(0, 0)
            threadGame?.stopGame()
        } finally {
            threadGame = null
        }
    }
    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    override fun onSensorChanged(event: SensorEvent) {
        ball.setAcceleration(event.values[0], event.values[1])
    }
}
