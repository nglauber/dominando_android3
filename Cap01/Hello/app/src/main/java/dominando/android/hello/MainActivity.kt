package dominando.android.hello

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val config = resources.configuration
        val metrics = resources.displayMetrics
        val orientation = config.orientation
        val density = metrics.density
        val height = metrics.heightPixels
        val width = metrics.widthPixels
        val mcc = config.mcc
        val mnc = config.mnc
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            config.locales[0] else config.locale

        val tag = "NGVL"
        Log.d(tag, "density: $density")
        Log.d(tag, "orientation: $orientation")
        Log.d(tag, "height: $height")
        Log.d(tag, "width: $width")
        Log.d(tag, "language: ${locale.language} - ${locale.country}")
        Log.d(tag, "mcc: $mcc")
        Log.d(tag, "mnc: $mnc")
    }
}
