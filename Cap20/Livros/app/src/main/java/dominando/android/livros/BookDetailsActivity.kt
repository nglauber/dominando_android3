package dominando.android.livros

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import dominando.android.livros.databinding.ActivityBookDetailsBinding
import dominando.android.livros.model.Book
import dominando.android.livros.viewmodels.BookDetailsViewModel
import org.parceler.Parcels

class BookDetailsActivity : BaseActivity() {

    private val viewModel: BookDetailsViewModel by lazy {
        ViewModelProviders.of(this).get(BookDetailsViewModel::class.java)
    }

    private val binding: ActivityBookDetailsBinding by lazy {
        DataBindingUtil.setContentView<ActivityBookDetailsBinding>(
            this, R.layout.activity_book_details
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val book = Parcels.unwrap<Book>(intent.getParcelableExtra(EXTRA_BOOK))
        if (book != null) {
            binding.book = book
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.book_details, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_edit_book) {
            binding.book?.let {
                BookFormActivity.start(this, it)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun init() {
        binding.book?.let { book ->
            viewModel.getBook(book.id).observe(this, Observer {
                binding.book = it
            })
        }
    }

    companion object {
        private const val EXTRA_BOOK = "book"

        fun start(context: Context, book: Book) {
            context.startActivity(
                Intent(context, BookDetailsActivity::class.java).apply {
                    putExtra(EXTRA_BOOK, Parcels.wrap(book))
                }
            )
        }
    }
}
