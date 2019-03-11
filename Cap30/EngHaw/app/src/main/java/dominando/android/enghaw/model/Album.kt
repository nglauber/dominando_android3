package dominando.android.enghaw.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
class Album (
    @PrimaryKey (autoGenerate = true)
    var id: Long = 0L,
    @SerializedName("titulo")
    val title: String,
    @SerializedName("capa")
    val cover: String,
    @SerializedName("capa_big")
    val coverBig: String,
    @SerializedName("ano")
    val year: Int,
    @SerializedName("gravadora")
    val recordingCompany: String,
    @SerializedName("formacao")
    val formation: List<String>,
    @SerializedName("faixas")
    val tracks: List<String>
) : Parcelable
