package cl.duoc.therionlabs.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.therionlabs.data.local.*
import cl.duoc.therionlabs.domain.*
import cl.duoc.therionlabs.ui.components.*

@Composable
fun EventsScreen(
    user: UserSession,
    events: List<EventoEntity>,
    records: List<ProspectoEntity>,
    busy: Boolean,
    create: () -> Unit,
    delete: (String) -> Unit,
    open: (EventoEntity) -> Unit,
) {
    var search by rememberSaveable { mutableStateOf("") }
    val admin = AccessPolicy.canManage(user)
    var toDelete by remember { mutableStateOf<EventoEntity?>(null) }
    val actualEvents = events.filterNot { EventRules.isDemo(it.id) }
    val visible = actualEvents.filter { e ->
        (admin || e.activo) && "${e.nombre} ${e.colegio} ${e.fecha}".contains(search.trim(), true)
    }
    toDelete?.let { event ->
        AlertDialog(
            onDismissRequest = { if (!busy) toDelete = null },
            title = { Text("¿Eliminar evento?") },
            text = {
                Text(
                    "Se eliminará ${event.nombre}. Esta acción solo está permitida si el evento no tiene prospectos registrados y no se puede deshacer."
                )
            },
            confirmButton = {
                TextButton(
                    enabled = !busy,
                    onClick = {
                        toDelete = null
                        delete(event.id)
                    },
                ) {
                    Text("Eliminar evento")
                }
            },
            dismissButton = {
                TextButton(enabled = !busy, onClick = { toDelete = null }) {
                    Text("Conservar evento")
                }
            },
        )
    }
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            SectionTitle(
                if (admin) "Eventos y prospectos" else "Eventos de captación",
                if (admin)
                    "Revisa cada actividad, consulta sus prospectos y gestiona su seguimiento."
                else "Selecciona la visita del colegio antes de registrar a los interesados.",
            )
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Outlined.Event, null)
                    Column {
                        Text(
                            "${actualEvents.count { it.activo }} eventos disponibles",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            "${records.count { record -> actualEvents.any { it.id == record.eventoId } }} prospectos ${if (admin) "en este dispositivo" else "registrados por tu cuenta"}",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }
        if (admin)
            item {
                Button(onClick = create, enabled = !busy, modifier = Modifier.fillMaxWidth()) {
                    Text("Crear evento")
                }
            }
        item { Input("Buscar colegio, evento o fecha", search, { search = it }) }
        if (visible.isEmpty())
            item {
                Text(
                    if (actualEvents.isEmpty()) {
                        if (admin) "Todavía no hay eventos. Crea el primero para comenzar."
                        else
                            "Todavía no hay eventos. Solicita al administrativo que cree uno en este dispositivo."
                    } else "No hay eventos que coincidan con la búsqueda."
                )
            }
        items(visible, key = { it.id }) { event ->
            ElevatedCard(Modifier.fillMaxWidth().animateContentSize()) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(event.nombre, style = MaterialTheme.typography.titleMedium)
                    Text(event.colegio)
                    if (event.comuna.isNotBlank()) Text(event.comuna)
                    Text(
                        "${event.fecha} · ${event.actividad}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(
                        "${records.count { it.eventoId == event.id }} prospectos ${if (admin) "disponibles" else "de tu cuenta"}",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge,
                    )
                    Button(
                        onClick = { open(event) },
                        enabled = !busy,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(if (admin) "Ver prospectos" else "Registrar en este evento")
                    }
                    if (admin && records.none { it.eventoId == event.id })
                        TextButton(
                            onClick = { toDelete = event },
                            enabled = !busy,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Eliminar evento", color = MaterialTheme.colorScheme.error)
                        }
                }
            }
        }
        item {
            Text(
                "Los eventos y sus prospectos se conservan en este dispositivo.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
