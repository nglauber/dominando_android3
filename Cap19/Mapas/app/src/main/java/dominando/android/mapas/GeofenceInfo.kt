package dominando.android.mapas

import com.google.android.gms.location.Geofence
data class GeofenceInfo(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Float,
    val expirationDuration: Long,
    val transitionType: Int ) {
    fun getGeofence(): Geofence {
        return Geofence.Builder()
            .setRequestId(id)
            .setTransitionTypes(transitionType)
            .setCircularRegion(latitude, longitude, radius)
            .setExpirationDuration(expirationDuration)
            .build()
    }
}
