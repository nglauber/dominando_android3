package dominando.android.livros.model

import org.parceler.Parcel

@Parcel
data class Publisher(
    var id: String = "",
    var name: String = ""
) {
    override fun toString(): String = "$id - $name"
}