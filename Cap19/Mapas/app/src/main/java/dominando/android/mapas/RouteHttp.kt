package dominando.android.mapas

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.*

object RouteHttp {
    fun searchRoute(orig: LatLng, dest: LatLng): List<LatLng>? {
        try {
            val urlRoute = String.format(Locale.US,
                "https://maps.googleapis.com/maps/api/directions/json?" +
                        "origin=%f,%f&destination=%f,%f&" +
                        "sensor=true&mode=driving&key=%s",
                orig.latitude, orig.longitude,
                dest.latitude, dest.longitude,
                "SUA_SERVER_KEY")

            val client = OkHttpClient()
            val request = Request.Builder()
                .url(urlRoute)
                .build()

            val response = client.newCall(request).execute()
            val result = response.body()?.string()
            val json = JSONObject(result)
            val jsonRoute = json.getJSONArray("routes").getJSONObject(0)
            val leg = jsonRoute.getJSONArray("legs").getJSONObject(0)
            val steps = leg.getJSONArray("steps")
            val numSteps = steps.length()
            var step: JSONObject

            val latLngList = mutableListOf<LatLng>()
            for (i in 0 until numSteps) {
                step = steps.getJSONObject(i)
                val points = step.getJSONObject("polyline").getString("points")
                latLngList.addAll(PolyUtil.decode(points))
            }
            return latLngList
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}
