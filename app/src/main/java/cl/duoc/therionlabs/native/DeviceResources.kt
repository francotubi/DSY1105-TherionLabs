package cl.duoc.therionlabs.native

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.location.LocationManagerCompat
import androidx.core.os.CancellationSignal
import java.io.File
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull

object DeviceResources {
    fun cameraUri(context: Context) =
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.files",
            File.createTempFile(
                "visita_",
                ".jpg",
                File(context.filesDir, "visitas").apply { mkdirs() },
            ),
        )

    suspend fun location(context: Context): Location? =
        withTimeoutOrNull(20000L) {
            val fine =
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                ) == PackageManager.PERMISSION_GRANTED
            val coarse =
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ) == PackageManager.PERMISSION_GRANTED
            if (!fine && !coarse) return@withTimeoutOrNull null
            val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val provider =
                when {
                    fine && manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ->
                        LocationManager.GPS_PROVIDER
                    manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ->
                        LocationManager.NETWORK_PROVIDER
                    else -> return@withTimeoutOrNull null
                }
            suspendCancellableCoroutine { cont ->
                val cancel = CancellationSignal()
                cont.invokeOnCancellation { cancel.cancel() }
                try {
                    LocationManagerCompat.getCurrentLocation(
                        manager,
                        provider,
                        cancel,
                        ContextCompat.getMainExecutor(context),
                    ) { location ->
                        if (cont.isActive) cont.resume(location)
                    }
                } catch (_: SecurityException) {
                    if (cont.isActive) cont.resume(null)
                } catch (_: IllegalArgumentException) {
                    if (cont.isActive) cont.resume(null)
                }
            }
        }
}
