package dominando.android.mapas

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            val errorCode = geofencingEvent.errorCode
            Toast.makeText(context, "Erro no serviço de localização: $errorCode",
                Toast.LENGTH_LONG).show()
        } else {
            val geofences = geofencingEvent.triggeringGeofences
            val transition = geofencingEvent.geofenceTransition
            geofences.forEach { geofence ->
                val msg = when (transition) {
                    Geofence.GEOFENCE_TRANSITION_ENTER ->
                        "Geofence ID: ${geofence.requestId} ENTROU do perímetro"
                    Geofence.GEOFENCE_TRANSITION_EXIT ->
                        "Geofence ID: ${geofence.requestId} SAIU do perímetro"
                    Geofence.GEOFENCE_TRANSITION_DWELL ->
                        "Geofence ID: ${geofence.requestId} PERMANECE do perímetro"
                    else ->
                        "Erro no Geofence: $transition"
                }
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            }
        }
    }
}
