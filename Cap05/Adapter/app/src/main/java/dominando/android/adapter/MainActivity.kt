package dominando.android.adapter

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    // 0=VW;1=GM;2=Fiat;3=Ford
    private val vehicles = mutableListOf(
        Vehicle("Onix", 2018, 1, true, true),
        Vehicle("Uno", 2007, 2, true, false),
        Vehicle("Del Rey", 1988, 3, false, true),
        Vehicle("Gol", 2014, 0, true, true)
    )
    private lateinit var txtFooter: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listView.emptyView = findViewById(android.R.id.empty)
        val adapter = VehicleAdapter(this, vehicles)
        listView.adapter = adapter
        initHeaderAndFooter(listView, adapter)
        listView.setOnItemClickListener { parent, view, position, id ->
            val vehicle = parent.getItemAtPosition(position) as? Vehicle
            if (vehicle != null) {
                val (model, year) = vehicle
                Toast.makeText(this, "$model $year", Toast.LENGTH_SHORT).show()
                vehicles.remove(vehicle)
                adapter.notifyDataSetChanged()
                txtFooter.text = resources.getQuantityString(
                    R.plurals.footer_text, adapter.count, adapter.count)
            }
        }
    }

    private fun initHeaderAndFooter(listView : ListView, adapter: VehicleAdapter) {
        val padding = 8
        val txtHeader = TextView(this)
        txtHeader.setBackgroundColor(Color.GRAY)
        txtHeader.setTextColor(Color.WHITE)
        txtHeader.setText(R.string.header_text)
        txtHeader.setPadding(padding, padding, 0, padding)
        listView.addHeaderView(txtHeader)

        txtFooter = TextView(this)
        txtFooter.text = resources.getQuantityString(
            R.plurals.footer_text, adapter.count, adapter.count)
        txtFooter.setBackgroundColor(Color.LTGRAY)
        txtFooter.gravity = Gravity.END
        txtFooter.setPadding(0, padding, padding, padding)
        listView.addFooterView(txtFooter)
    }
}
