package cl.duoc.therionlabs.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.therionlabs.data.local.*
import cl.duoc.therionlabs.domain.*
import cl.duoc.therionlabs.ui.components.*

@Composable
fun ProspectsScreen(
    title: String,
    subtitle: String,
    records: List<ProspectoEntity>,
    followUps: List<SeguimientoEntity>,
    admin: Boolean,
    open: (Int) -> Unit,
    back: () -> Unit,
) {
    var search by rememberSaveable(title) { mutableStateOf("") }
    var filter by rememberSaveable(title) { mutableStateOf("TODOS") }
    val latest =
        followUps
            .sortedWith(
                compareByDescending<SeguimientoEntity> { it.fecha }.thenByDescending { it.id }
            )
            .distinctBy { it.prospectoId }
            .associateBy { it.prospectoId }
    val visible = records.filter { p ->
        val state = latest[p.id]?.estado ?: "SIN_CONTACTAR"
        "${p.nombre} ${p.apellidos} ${p.rut} ${p.email} ${p.celular} ${p.eventoNombre}"
            .contains(search.trim(), true) &&
            (filter == "TODOS" || (if (admin) state == filter else p.estado == filter))
    }
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            TextButton(onClick = back) { Text("Volver") }
            SectionTitle(title, subtitle)
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(Modifier.weight(1f)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Prospectos")
                        Text("${records.size}", style = MaterialTheme.typography.headlineMedium)
                    }
                }
                Card(Modifier.weight(1f)) {
                    Column(Modifier.padding(16.dp)) {
                        Text(if (admin) "Sin contactar" else "Por enviar")
                        Text(
                            "${if (admin) records.count { (latest[it.id]?.estado ?: "SIN_CONTACTAR") == "SIN_CONTACTAR" }
                        else records.count { it.estado == "PENDIENTE" }}",
                            style = MaterialTheme.typography.headlineMedium,
                        )
                    }
                }
            }
        }
        item { Input("Buscar nombre, RUT o contacto", search, { search = it }) }
        item {
            val options =
                if (admin) listOf("Todos") + FollowUpRules.states.values
                else listOf("Todos", "Pendientes", "Sincronizados")
            val label =
                if (filter == "TODOS") "Todos"
                else if (admin) FollowUpRules.label(filter)
                else if (filter == "PENDIENTE") "Pendientes" else "Sincronizados"
            Select(
                if (admin) "Estado de seguimiento" else "Estado de envío",
                label,
                options,
                { chosen ->
                    filter =
                        if (chosen == "Todos") "TODOS"
                        else if (admin)
                            FollowUpRules.states.entries.first { it.value == chosen }.key
                        else if (chosen == "Pendientes") "PENDIENTE" else "SINCRONIZADO"
                },
            )
        }
        if (visible.isEmpty())
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(20.dp)) {
                        Text(
                            if (records.isEmpty()) "Todavía no hay prospectos registrados."
                            else "No hay coincidencias para este filtro."
                        )
                    }
                }
            }
        items(visible, key = { it.id }) { p ->
            ElevatedCard(
                onClick = { open(p.id) },
                modifier = Modifier.fillMaxWidth().animateContentSize(),
            ) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    Text("${p.nombre} ${p.apellidos}", style = MaterialTheme.typography.titleMedium)
                    Text(
                        if (p.carrera1 == Catalogs.otraCarrera) "Otra: ${p.otraCarrera}"
                        else p.carrera1
                    )
                    Text(p.eventoNombre, style = MaterialTheme.typography.bodySmall)
                    if (admin)
                        Text(
                            "Seguimiento: ${FollowUpRules.label(latest[p.id]?.estado ?: "SIN_CONTACTAR")}",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    Text(
                        "Envío: ${if (p.estado == "PENDIENTE") "pendiente" else "sincronizado"}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(displayDate(p.fechaCaptura), style = MaterialTheme.typography.bodySmall)
                    Text("Ver ficha", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
