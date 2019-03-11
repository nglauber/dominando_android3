package dominando.android.bottomsheet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_bottom_sheet.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnBottomSheet.setOnClickListener {
            val behavior = BottomSheetBehavior.from(layoutBottomSheet)
            if (behavior.state == BottomSheetBehavior.STATE_HIDDEN
                || behavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            } else if (behavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        btnBottomSheetDialog.setOnClickListener {
            val dialog = BottomDialog()
            dialog.show(supportFragmentManager, "tag")
        }
    }
}
