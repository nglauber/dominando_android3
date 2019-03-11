package dominando.android.basico

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Pessoa(val nome: String, val idade: Int) : Parcelable

