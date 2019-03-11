package dominando.android.livros.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import dominando.android.livros.firebase.FbRepository
import dominando.android.livros.model.Book

class BookDetailsViewModel : ViewModel() {
    private val repo = FbRepository()
    private val selectedBookId = MutableLiveData<String>()
    private var selectedBook = Transformations.switchMap(selectedBookId) { bookId ->
        repo.loadBook(bookId)
    }
    fun getBook(id: String): LiveData<Book> {
        selectedBookId.value = id
        return selectedBook
    }
}
