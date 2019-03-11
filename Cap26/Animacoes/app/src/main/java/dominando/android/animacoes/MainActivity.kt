package dominando.android.animacoes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView

class MainActivity : AppCompatActivity() {
    private val options = mapOf(
        "View Animations" to ViewAnimationsActivity::class.java,
        "Property Animations" to PropertyAnimationsActivity::class.java,
        "Sprite Animation" to SpriteActivity::class.java,
        "Layout Animation" to LayoutChangesActivity::class.java,
        "Transitions" to TransitionActivity::class.java,
        "Reveal" to RevealActivity::class.java
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val adapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1,
            options.keys.toList())
        val listView = ListView(this)
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            showActivity(position)
        }
        setContentView(listView)
    }
    private fun showActivity(position: Int) {
        val key = options.keys.toList()[position]
        startActivity(Intent(this, options[key]))
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out)
    }
}
