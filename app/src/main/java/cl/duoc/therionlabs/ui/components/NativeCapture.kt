package cl.duoc.therionlabs.ui.components

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import cl.duoc.therionlabs.domain.CaptureDraft
import cl.duoc.therionlabs.native.DeviceResources
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Photo(uri: String) {
    val context = LocalContext.current
    val bitmap by
        produceState<android.graphics.Bitmap?>(null, uri) {
            value =
                withContext(Dispatchers.IO) {
                    try {
                        context.contentResolver.openInputStream(Uri.parse(uri)).use { stream ->
                            BitmapFactory.decodeStream(
                                stream,
                                null,
                                BitmapFactory.Options().apply { inSampleSize = 4 },
                            )
                        }
                    } catch (_: Exception) {
                        null
                    }
                }
        }
    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "Fotografía de la actividad",
            modifier = Modifier.fillMaxWidth().heightIn(max = 220.dp),
        )
    }
}

@Composable
fun NativeCapture(d: CaptureDraft, update: ((CaptureDraft) -> CaptureDraft) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var pending by rememberSaveable { mutableStateOf<String?>(null) }
    var locating by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    val camera =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
            if (ok && pending != null) {
                val uri = pending
                update { it.copy(fotoUri = uri) }
                message = "Fotografía adjunta a la visita."
            } else message = "Captura cancelada. Puedes continuar sin fotografía."
            pending = null
        }
    fun locate() {
        if (locating) return
        locating = true
        scope.launch {
            try {
                val result = DeviceResources.location(context)
                if (result != null) {
                    update {
                        it.copy(
                            latitud = result.latitude,
                            longitud = result.longitude,
                            precisionMetros = result.accuracy,
                            fechaUbicacion = result.time,
                        )
                    }
                    message = "Ubicación de la visita registrada."
                } else
                    message =
                        "No fue posible obtener la ubicación. Activa la ubicación del dispositivo o vuelve a intentar."
            } catch (_: Exception) {
                message = "No se pudo obtener la ubicación. Puedes continuar sin ella."
            } finally {
                locating = false
            }
        }
    }
    val permission =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            grants ->
            if (grants.values.any { it }) locate()
            else
                message =
                    "Permiso de ubicación no concedido. El registro puede guardarse sin ubicación."
        }
    Card {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionTitle(
                "Evidencia de la visita",
                "Opcional. Adjunta material o el lugar de la actividad, sin fotografiar personas ni documentos personales.",
            )
            OutlinedButton(
                onClick = {
                    try {
                        val uri = DeviceResources.cameraUri(context)
                        pending = uri.toString()
                        camera.launch(uri)
                    } catch (_: ActivityNotFoundException) {
                        message = "No hay una aplicación de cámara disponible."
                    } catch (_: Exception) {
                        message = "No se pudo abrir la cámara. Puedes continuar sin fotografía."
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Outlined.CameraAlt, null)
                Spacer(Modifier.width(8.dp))
                Text(if (d.fotoUri == null) "Tomar fotografía" else "Cambiar fotografía")
            }
            d.fotoUri?.let {
                Photo(it)
                TextButton(onClick = { update { it.copy(fotoUri = null) } }) {
                    Text("Quitar fotografía")
                }
            }
            OutlinedButton(
                enabled = !locating,
                onClick = {
                    val allowed =
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                        ) == PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                            ) == PackageManager.PERMISSION_GRANTED
                    if (allowed) locate()
                    else
                        permission.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                            )
                        )
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (locating) {
                    CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                } else Icon(Icons.Outlined.LocationOn, null)
                Spacer(Modifier.width(8.dp))
                Text(if (locating) "Buscando ubicación…" else "Registrar ubicación de la visita")
            }
            if (d.latitud != null && d.longitud != null) {
                Text("Latitud: %.5f · Longitud: %.5f".format(d.latitud, d.longitud))
                Text(
                    "Precisión aproximada: ${d.precisionMetros?.toInt()} m",
                    style = MaterialTheme.typography.bodySmall,
                )
                TextButton(
                    onClick = {
                        update {
                            it.copy(
                                latitud = null,
                                longitud = null,
                                precisionMetros = null,
                                fechaUbicacion = null,
                            )
                        }
                    }
                ) {
                    Text("Quitar ubicación")
                }
            }
            AnimatedVisibility(message != null) {
                Text(message.orEmpty(), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
