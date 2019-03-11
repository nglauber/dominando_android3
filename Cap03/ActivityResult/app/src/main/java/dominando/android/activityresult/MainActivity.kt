package dominando.android.activityresult

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var state: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnState.setOnClickListener {
            val intent = Intent(this, StatesListActivity::class.java)
            intent.putExtra(StatesListActivity.EXTRA_STATE, state)
            startActivityForResult(intent, REQUEST_STATE)
        }
        if (savedInstanceState != null) {
            state = savedInstanceState.getString(EXTRA_STATE)
            if (state != null) {
                btnState.text = state
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_STATE) {
            state = data?.getStringExtra(StatesListActivity.EXTRA_RESULT)
            btnState.text = state
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EXTRA_STATE, state)
    }
    companion object {
        private const val REQUEST_STATE = 1
        private const val EXTRA_STATE = "estado"
    }
}
