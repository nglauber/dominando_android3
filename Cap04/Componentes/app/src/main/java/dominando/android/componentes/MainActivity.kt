package dominando.android.componentes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.SeekBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initSpinner()
        initSeekbar()
        initSwitch()
        // Atribuindo programaticamente os valores-padrão
        chkEnabled.isChecked = true
        skbValue.progress = 20
        spnNames.setSelection(2)
        rgOptions.check(R.id.rbOption2)
        btnShowValues.setOnClickListener { showValues() }
    }
    private fun initSpinner() {
        val names = arrayOf("Eric", "Diana", "Presto", "Hank", "Sheila", "Bob")
        val adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, names)
        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item)
        spnNames.adapter = adapter
    }
    private fun initSeekbar() {
        skbValue.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                    txtValue.text = i.toString()
                }
                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            })
    }
    private fun initSwitch() {
        swtEnabled.setOnCheckedChangeListener { compoundButton, b ->
            chkEnabled.isEnabled = compoundButton.isChecked
            tgbEnabled.isEnabled = b
        }
    }
    private fun showValues() {
        val idSelectedRadio = rgOptions.checkedRadioButtonId
        val radio = findViewById<RadioButton>(idSelectedRadio)
        val enabledText = getString(
            if (chkEnabled.isChecked) R.string.text_enabled
            else R.string.text_disabled
        )
        val message = getString(R.string.msg_result,
            enabledText,
            skbValue.progress,
            spnNames.selectedItem,
            radio.text.toString()
        )
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
