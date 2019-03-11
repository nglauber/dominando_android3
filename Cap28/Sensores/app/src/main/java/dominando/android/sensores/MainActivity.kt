package dominando.android.sensores

import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val sensorManager: SensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    private val sensorsList: List<Sensor> by lazy {
        sensorManager.getSensorList(Sensor.TYPE_ALL)
    }
    private val sensorListener = object: SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            Toast.makeText(this@MainActivity,
                "Precisão mudou: $accuracy", Toast.LENGTH_SHORT).show()
        }
        override fun onSensorChanged(event: SensorEvent) {
            var values = "Valores do Sensor:\n"
            event.values.indices.forEach { i ->
                values += "values[$i] = ${event.values[i]}\n"
            }
            txtValues.text = values
        }
    }
    private var selectedSensor: Int = 0

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val sensorNamesList = sensorsList.map { it.name }
        val adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, sensorNamesList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnSensors.adapter = adapter
        spnSensors.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?,
                                        position: Int, id: Long) {
                selectedSensor = position
                unregisterSensor()
                registerSensor()
            }

        }
    }
    override fun onResume() {
        super.onResume()
        registerSensor()
    }
    override fun onPause() {
        super.onPause()
        unregisterSensor()
    }
    private fun registerSensor() {
        sensorManager.registerListener(
            sensorListener,
            sensorsList[selectedSensor],
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }
    private fun unregisterSensor() {
        sensorManager.unregisterListener(sensorListener)
    }
}

