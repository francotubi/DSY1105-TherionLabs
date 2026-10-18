package cl.duoc.therionlabs.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.therionlabs.domain.EventDraft
import cl.duoc.therionlabs.ui.components.*

@Composable
fun CreateEventScreen(
    d: EventDraft,
    errors: Map<String, String>,
    busy: Boolean,
    update: ((EventDraft) -> EventDraft) -> Unit,
    save: () -> Unit,
    back: () -> Unit,
) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SectionTitle("Crear evento", "Los prospectos registrados se asociarán a esta actividad.")
        Input(
            "Nombre del evento *",
            d.nombre,
            { value -> update { it.copy(nombre = value) } },
            errors["nombre"],
            enabled = !busy,
        )
        Input(
            "Colegio o institución *",
            d.colegio,
            { value -> update { it.copy(colegio = value) } },
            errors["colegio"],
            enabled = !busy,
        )
        Input(
            "Fecha · DD/MM/AAAA *",
            d.fecha,
            { value -> update { it.copy(fecha = value) } },
            errors["fecha"],
            enabled = !busy,
        )
        Input(
            "Comuna *",
            d.comuna,
            { value -> update { it.copy(comuna = value) } },
            errors["comuna"],
            enabled = !busy,
        )
        Input(
            "Descripción de la actividad *",
            d.actividad,
            { value -> update { it.copy(actividad = value) } },
            errors["actividad"],
            lines = 3,
            enabled = !busy,
        )
        Button(onClick = save, enabled = !busy, modifier = Modifier.fillMaxWidth()) {
            Text(if (busy) "Guardando…" else "Crear evento")
        }
        TextButton(onClick = back, enabled = !busy) { Text("Volver a eventos") }
    }
}
