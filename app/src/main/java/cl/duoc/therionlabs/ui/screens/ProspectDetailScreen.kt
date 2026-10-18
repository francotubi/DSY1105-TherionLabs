package cl.duoc.therionlabs.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.therionlabs.data.local.ProspectoEntity
import cl.duoc.therionlabs.ui.components.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun displayDate(value: Long) =
    if (value == 0L) "Fecha no disponible"
    else SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("es-CL")).format(Date(value))

@Composable
fun DetailScreen(p: ProspectoEntity?, footer: @Composable () -> Unit = {}, onBack: () -> Unit) {
    if (p == null) {
        Column(Modifier.padding(20.dp)) {
            Text("Registro no disponible.")
            TextButton(onClick = onBack) { Text("Volver al historial") }
        }
        return
    }
    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 20.dp),
        contentPadding = PaddingValues(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            SectionTitle(
                "Ficha del prospecto",
                "Registro #${p.id} · ${displayDate(p.fechaCaptura)}",
            )
        }
        item {
            Card {
                Column(
                    Modifier.fillMaxWidth().padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text("${p.nombre} ${p.apellidos}", style = MaterialTheme.typography.titleLarge)
                    Text("RUT: ${p.rut}")
                    Text("Nacimiento: ${p.fechaNacimiento}")
                    Text("Correo: ${p.email}")
                    Text("Celular: ${p.celular}")
                    Text("Establecimiento: ${p.establecimiento}")
                    Text("Comuna de residencia: ${p.comunaResidencia}")
                    Text("Comuna del establecimiento: ${p.comunaEstablecimiento}")
                    Text("Actividad: ${p.actividadActual}")
                    Text("Jornada: ${p.jornada}")
                }
            }
        }
        item {
            Card {
                Column(
                    Modifier.fillMaxWidth().padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text("Intereses", style = MaterialTheme.typography.titleMedium)
                    Text("Sede: ${p.sede} · ${p.region}")
                    Text("Interés: ${p.nivelInteres}")
                    listOfNotNull(p.carrera1, p.carrera2, p.carrera3).forEachIndexed { i, c ->
                        Text(
                            "${i+1}. ${if (c == cl.duoc.therionlabs.domain.Catalogs.otraCarrera) "Otra: ${p.otraCarrera} (por confirmar)" else c}"
                        )
                    }
                    Text("Modalidad: ${p.modalidad}")
                    Text("Contacto preferido: ${p.canal}")
                    if (p.observaciones.isNotBlank()) Text("Observaciones: ${p.observaciones}")
                }
            }
        }
        item {
            Text("Visita: ${p.eventoNombre}")
            Text("Captador: ${p.captador}")
            Text("Estado de envío: ${if (p.estado == "PENDIENTE") "Pendiente" else "Sincronizado"}")
            if (p.latitud != null && p.longitud != null) {
                Text("Ubicación: %.5f, %.5f".format(p.latitud, p.longitud))
                Text(
                    "Precisión: ${p.precisionMetros?.toInt()} m · ${displayDate(p.fechaUbicacion?:0)}"
                )
            }
        }
        p.fotoUri?.let { uri ->
            item {
                Text("Fotografía de la actividad", style = MaterialTheme.typography.titleMedium)
                Photo(uri)
            }
        }
        item { footer() }
        item {
            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Volver al historial")
            }
        }
    }
}
