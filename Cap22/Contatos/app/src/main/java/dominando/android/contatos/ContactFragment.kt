package dominando.android.contatos

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import java.io.FileNotFoundException

class ContactFragment : DialogFragment(), DialogInterface.OnClickListener {
    lateinit var edtName: EditText
    lateinit var edtPhone: EditText
    lateinit var edtAddress: EditText
    lateinit var imgPhoto: ImageView
    private var selectedPhoto: Bitmap? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.fragment_contact, null)
        edtName = view.findViewById(R.id.edtName)
        edtPhone = view.findViewById(R.id.edtPhone)
        edtAddress = view.findViewById(R.id.edtAddress)
        imgPhoto = view.findViewById(R.id.imgPhoto)
        imgPhoto.setOnClickListener {
            selectPhotoFromGallery()
        }
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_title)
            .setView(view)
            .setPositiveButton(R.string.button_ok, this)
            .setNegativeButton(R.string.button_cancel, null)
            .create()
    }
    private fun selectPhotoFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }
    override fun onClick(dialog: DialogInterface, which: Int) {
        val photo = selectedPhoto
        if (edtName.text.isNotEmpty() && edtPhone.text.isNotEmpty() && photo != null) {
            ContactUtils.insertContact(
                requireContext(),
                edtName.text.toString(),
                edtPhone.text.toString(),
                edtAddress.text.toString(),
                photo)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null && data.data != null) {
            try {
                val options = BitmapFactory.Options()
                options.inSampleSize = 4
                selectedPhoto = BitmapFactory.decodeStream(
                    requireContext().contentResolver.openInputStream(data.data),
                    null,
                    options
                )
                imgPhoto.setImageBitmap(selectedPhoto)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }
}
