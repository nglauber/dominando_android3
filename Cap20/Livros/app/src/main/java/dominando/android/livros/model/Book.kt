package dominando.android.livros.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import dominando.android.livros.BR
import org.parceler.Parcel

@Parcel
class Book : BaseObservable() {
    @Bindable
    var id: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.id)
        }
    @Bindable
    var title: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.title)
        }
    @Bindable
    var author: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.author)
        }
    @Bindable
    var coverUrl: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.coverUrl)
        }
    @Bindable
    var pages: Int = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.pages)
        }
    @Bindable
    var year: Int = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.year)
        }
    @Bindable
    var publisher: Publisher? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.publisher)
        }
    @Bindable
    var available: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.available)
        }
    @Bindable
    var mediaType: MediaType = MediaType.PAPER
        set(value) {
            field = value
            notifyPropertyChanged(BR.mediaType)
        }
    @Bindable
    var rating: Float = 0f
        set(value) {
            field = value
            notifyPropertyChanged(BR.rating)
        }

    var userId: String = ""
}
