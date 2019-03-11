package dominando.android.enghaw.model

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

object AlbumHttp {
    const val BASE_URL =
        "https://raw.githubusercontent.com/nglauber/dominando_android2/master/enghaw/"

    fun loadAlbums(): Array<Album>? {
        val client = OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build()
        val request = Request.Builder()
            .url("${BASE_URL}enghaw.json")
            .build()
        return try {
            val response = client.newCall(request).execute()
            val json = response.body()?.string()
            Gson().fromJson(json, Array<Album>::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
