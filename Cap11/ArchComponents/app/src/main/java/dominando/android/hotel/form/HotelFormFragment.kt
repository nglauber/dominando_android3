package dominando.android.hotel.form

import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import dominando.android.hotel.model.Hotel
import dominando.android.hotel.R
import kotlinx.android.synthetic.main.fragment_hotel_form.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.lifecycle.Observer

class HotelFormFragment : DialogFragment() {
    private val viewModel: HotelFormViewModel by viewModel()
    private var hotel: Hotel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_hotel_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val hotelId = arguments?.getLong(EXTRA_HOTEL_ID, 0) ?: 0
        if (hotelId > 0) {
            viewModel.loadHotel(hotelId).observe(viewLifecycleOwner, Observer { hotel ->
                this.hotel = hotel
                showHotel(hotel)
            })
        }
        edtAddress.setOnEditorActionListener { _, i, _ ->
            handleKeyboardEvent(i)
        }
        dialog?.setTitle(R.string.action_new_hotel)
        // Abre o teclado virtual ao exibir o Dialog
        dialog?.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    private fun showHotel(hotel: Hotel) {
        edtName.setText(hotel.name)
        edtAddress.setText(hotel.address)
        rtbRating.rating = hotel.rating
    }

    private fun errorSaveHotel() {
        Toast.makeText(requireContext(), R.string.error_hotel_not_found, Toast.LENGTH_SHORT).show()
    }

    private fun errorInvalidHotel() {
        Toast.makeText(requireContext(), R.string.error_invalid_hotel, Toast.LENGTH_SHORT).show()
    }

    private fun handleKeyboardEvent(actionId: Int): Boolean {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            saveHotel()
            return true
        }
        return false
    }

    private fun saveHotel() {
        val hotel = this.hotel ?: Hotel()
        val hotelId = arguments?.getLong(EXTRA_HOTEL_ID, 0) ?: 0
        hotel.id = hotelId
        hotel.name = edtName.text.toString()
        hotel.address = edtAddress.text.toString()
        hotel.rating = rtbRating.rating
        try {
            if (viewModel.saveHotel(hotel)) {
                dialog?.dismiss()
            } else {
                errorInvalidHotel()
            }
        } catch (e: Exception) {
            errorSaveHotel()
        }
    }

    fun open(fm: FragmentManager) {
        if (fm.findFragmentByTag(DIALOG_TAG) == null) {
            show(fm, DIALOG_TAG)
        }
    }

    companion object {
        private const val DIALOG_TAG = "editDialog"
        private const val EXTRA_HOTEL_ID = "hotel_id"

        fun newInstance(hotelId: Long = 0) = HotelFormFragment().apply {
            arguments = Bundle().apply {
                putLong(EXTRA_HOTEL_ID, hotelId)
            }
        }
    }
}
