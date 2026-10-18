package cl.duoc.therionlabs.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cl.duoc.therionlabs.data.local.*
import cl.duoc.therionlabs.domain.*
import cl.duoc.therionlabs.native.ContactActions
import cl.duoc.therionlabs.ui.components.*

@Composable
fun ManagementScreen(
    p: ProspectoEntity,
    followUps: List<SeguimientoEntity>,
    busy: Boolean,
    onDirty: (Boolean) -> Unit,
    save: (String, String, String, () -> Unit) -> Unit,
) {
    var state by rememberSaveable(p.id) { mutableStateOf("EN_SEGUIMIENTO") }
    var channel by
        rememberSaveable(p.id) {
            mutableStateOf(p.canal.takeIf { it in FollowUpRules.channels } ?: "Teléfono")
        }
    var note by rememberSaveable(p.id) { mutableStateOf("") }
    var attempted by rememberSaveable(p.id) { mutableStateOf(false) }
    var actionMessage by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(note) { onDirty(note.isNotBlank()) }
    DisposableEffect(Unit) { onDispose { onDirty(false) } }
    val context = LocalContext.current
    val items =
        followUps
            .filter { it.prospectoId == p.id }
            .sortedWith(
                compareByDescending<SeguimientoEntity> { it.fecha }.thenByDescending { it.id }
            )
    val error = if (attempted) FollowUpRules.validate(state, channel, note) else null
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        SectionTitle("Contacto y seguimiento", "Canal preferido: ${p.canal}")
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Estado actual: ${FollowUpRules.label(items.firstOrNull()?.estado ?: "SIN_CONTACTAR")}",
                    style = MaterialTheme.typography.titleMedium,
                )
                OutlinedButton(
                    onClick = {
                        val result = ContactActions.phone(context, p.celular)
                        actionMessage =
                            result
                                ?: "Marcador abierto. Registra después el resultado de la gestión."
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Abrir marcador telefónico")
                }
                OutlinedButton(
                    onClick = {
                        val result = ContactActions.email(context, p.email)
                        actionMessage =
                            result
                                ?: "Correo abierto. El envío se confirma en la aplicación de correo."
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Redactar correo")
                }
                actionMessage?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
            }
        }
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Registrar una gestión", style = MaterialTheme.typography.titleMedium)
                Select(
                    "Resultado del seguimiento",
                    FollowUpRules.label(state),
                    FollowUpRules.states.values.toList(),
                    { label ->
                        state = FollowUpRules.states.entries.first { it.value == label }.key
                    },
                    enabled = !busy,
                )
                Select(
                    "Canal utilizado",
                    channel,
                    FollowUpRules.channels,
                    { channel = it },
                    enabled = !busy,
                )
                Input(
                    "Observación y resultado",
                    note,
                    { note = it },
                    keyboard = androidx.compose.ui.text.input.KeyboardType.Text,
                    lines = 3,
                    enabled = !busy,
                )
                Text("${note.length}/500 caracteres", style = MaterialTheme.typography.bodySmall)
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                Button(
                    enabled = !busy,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        attempted = true
                        if (FollowUpRules.validate(state, channel, note) == null)
                            save(state, channel, note) {
                                note = ""
                                attempted = false
                            }
                    },
                ) {
                    if (busy) {
                        CircularProgressIndicator(
                            Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(if (busy) "Guardando…" else "Guardar gestión")
                }
                Text(
                    "Abrir el teléfono o correo no modifica el estado. Confirma aquí el resultado real del contacto.",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        Text("Historial de gestiones (${items.size})", style = MaterialTheme.typography.titleMedium)
        if (items.isEmpty()) Text("Sin gestiones registradas.")
        items.forEach { g ->
            OutlinedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    Text(FollowUpRules.label(g.estado), style = MaterialTheme.typography.titleSmall)
                    Text(g.observacion)
                    Text(
                        "${g.canal} · ${displayDate(g.fecha)}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(g.administrativoNombre, style = MaterialTheme.typography.bodySmall)
                    Text(
                        "Envío de la gestión: pendiente",
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
    }
}

@Composable
fun AccountScreen(user: UserSession, logout: () -> Unit) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        SectionTitle("Mi cuenta", "Sesión y alcance de esta versión")
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(user.nombre, style = MaterialTheme.typography.titleLarge)
                Text(if (AccessPolicy.canManage(user)) "Administrativo" else "Captador de terreno")
                Text("Identificador: ${user.id}", style = MaterialTheme.typography.bodySmall)
            }
        }
        Text(
            "Los registros y las gestiones se conservan en este dispositivo. El envío al sistema central y el acceso entre dispositivos se incorporarán con el backend."
        )
        OutlinedButton(onClick = logout, modifier = Modifier.fillMaxWidth()) {
            Text("Cerrar sesión")
        }
    }
}

@Composable
fun FollowUpHistoryScreen(
    items: List<SeguimientoEntity>,
    records: List<ProspectoEntity>,
    open: (Int) -> Unit,
) {
    androidx.compose.foundation.lazy.LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            SectionTitle(
                "Gestiones registradas",
                "Historial de atención de los prospectos en este dispositivo.",
            )
        }
        if (items.isEmpty())
            item {
                Text(
                    "Todavía no hay gestiones. Abre un evento y registra el seguimiento desde la ficha del prospecto."
                )
            }
        items(items.size) { index ->
            val g = items[index]
            val p = records.find { it.id == g.prospectoId }
            ElevatedCard(onClick = { open(g.prospectoId) }, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        p?.let { "${it.nombre} ${it.apellidos}" } ?: "Prospecto #${g.prospectoId}",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(FollowUpRules.label(g.estado), color = MaterialTheme.colorScheme.primary)
                    Text(g.observacion)
                    Text(
                        "${g.canal} · ${displayDate(g.fecha)}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(g.administrativoNombre, style = MaterialTheme.typography.bodySmall)
                    Text("Ver ficha", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}
