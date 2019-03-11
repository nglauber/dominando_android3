package dominando.android.livros

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.format.DateFormat
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import dominando.android.livros.databinding.ActivityBookFormBinding
import dominando.android.livros.model.Book
import dominando.android.livros.model.MediaType
import dominando.android.livros.model.Publisher
import dominando.android.livros.viewmodels.BookFormViewModel
import kotlinx.android.synthetic.main.book_form_content.*
import org.parceler.Parcels
import java.io.File
import java.util.*

class BookFormActivity : BaseActivity() {
    private val viewModel: BookFormViewModel by lazy {
        ViewModelProviders.of(this).get(BookFormViewModel::class.java)
    }

    private val binding: ActivityBookFormBinding by lazy {
        DataBindingUtil.setContentView<ActivityBookFormBinding>(
            this, R.layout.activity_book_form
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.content.book = if (savedInstanceState == null) {
            Parcels.unwrap<Book>(intent.getParcelableExtra(EXTRA_BOOK)) ?: Book()
        } else {
            Parcels.unwrap<Book>(savedInstanceState.getParcelable(EXTRA_BOOK))
        }
        binding.content.publishers = listOf(
            Publisher("1", "Novatec"),
            Publisher("2", "Outra")
        )
        binding.content.view = this
    }
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable(EXTRA_BOOK, Parcels.wrap(binding.content.book))
    }
    fun onMediaTypeChanged(buttonView: CompoundButton, isChecked: Boolean) {
        if (isChecked) {
            if (buttonView === binding.content.rbMediaEbook) {
                binding.content.book?.mediaType = MediaType.EBOOK
            } else if (buttonView === binding.content.rbMediaPaper) {
                binding.content.book?.mediaType = MediaType.PAPER
            }
        }
    }
    fun clickSaveBook(view: View) {
        val book = binding.content.book
        if (book != null) {
            try {
                viewModel.saveBook(book)
            } catch (e: Exception) {
                showMessageError()
            }
        }
    }

    private fun showMessageSuccess() {
        Toast.makeText(this, R.string.message_book_saved, Toast.LENGTH_SHORT).show()
    }

    private fun showMessageError() {
        Toast.makeText(this, R.string.message_error_book_saved, Toast.LENGTH_SHORT).show()
    }


    override fun init() {
        viewModel.showProgress().observe(this, Observer { loading ->
            loading?.let {
                btnSave.isEnabled = !loading
                binding.content.progressBar.visibility = if (it) View.VISIBLE else View.GONE
            }
        })
        viewModel.savingOperation().observe(this, Observer { success ->
            success?.let {
                if (success) {
                    showMessageSuccess()
                    finish()
                } else {
                    showMessageError()
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == RC_CAMERA) {
            binding.content.book?.coverUrl = "file://${viewModel.tempImageFile?.absolutePath}"
        }
    }

    fun clickTakePhoto(view: View) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            viewModel.deleteTempPhoto()
            val fileName = DateFormat.format("ddMMyyy_hhmmss", Date()).toString()
            val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "$fileName.jpg")
            val photoUri = FileProvider.getUriForFile(
                this,
                "dominando.android.livros.fileprovider",
                file)
            viewModel.tempImageFile = file
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            startActivityForResult(takePictureIntent, RC_CAMERA)
        }
    }


    companion object {
        private const val RC_CAMERA = 1
        private const val EXTRA_BOOK = "book"

        fun start(context: Context, book: Book) {
            context.startActivity(
                Intent(context, BookFormActivity::class.java).apply {
                    putExtra(EXTRA_BOOK, Parcels.wrap(book))
                }
            )
        }
    }
}
