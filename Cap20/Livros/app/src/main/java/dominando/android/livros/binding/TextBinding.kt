package dominando.android.livros.binding

import android.widget.TextView
import androidx.databinding.BindingAdapter
import dominando.android.livros.R
import dominando.android.livros.model.MediaType

object TextBinding {
    @JvmStatic
    @BindingAdapter("android:text")
    fun setMediaTypeText(textView: TextView, mediaType: MediaType?) {
        if (mediaType == null) {
            textView.text = null
            return
        }
        val context = textView.context
        textView.text = when (mediaType) {
            MediaType.EBOOK -> context.getString(R.string.text_book_media_ebook)
            MediaType.PAPER -> context.getString(R.string.text_book_media_paper)
        }
    }
}
