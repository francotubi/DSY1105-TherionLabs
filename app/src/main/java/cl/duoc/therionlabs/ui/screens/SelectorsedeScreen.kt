package cl.duoc.therionlabs.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.therionlabs.domain.CaptureDraft
import cl.duoc.therionlabs.domain.Catalogs
import cl.duoc.therionlabs.ui.components.*

@Composable
fun SelectorSedeScreen(
    d: CaptureDraft,
    errors: Map<String, String>,
    busy: Boolean,
    update: ((CaptureDraft) -> CaptureDraft) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        SectionTitle("Intereses y visita", "Paso 2 de 2 · ${d.nombre} ${d.apellidos}")
        Select(
            "Región de interés *",
            d.region,
            Catalogs.sedes.keys.toList(),
            { v -> update { it.copy(region = v, sede = "") } },
            errors["region"],
        )
        Select(
            "Sede de interés *",
            d.sede,
            Catalogs.sedes[d.region] ?: emptyList(),
            { v -> update { it.copy(sede = v) } },
            errors["sede"],
            d.region.isNotBlank(),
        )
        Select(
            "Nivel de interés",
            d.nivelInteres,
            listOf("Alto", "Medio", "Bajo"),
            { v -> update { it.copy(nivelInteres = v) } },
        )
        Text(
            "Carreras de interés * · selecciona hasta tres",
            style = MaterialTheme.typography.titleMedium,
        )
        errors["carreras"]?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        var school by
            androidx.compose.runtime.saveable.rememberSaveable {
                mutableStateOf("Todas las escuelas")
            }
        var search by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf("") }
        var expanded by remember(school, search) { mutableStateOf(false) }
        Select(
            "Escuela",
            school,
            listOf("Todas las escuelas") + Catalogs.carrerasPorEscuela.keys,
            { school = it },
            enabled = !busy,
        )
        Input("Buscar carrera", search, { search = it }, enabled = !busy)
        val available =
            if (school == "Todas las escuelas") Catalogs.carrerasOficiales
            else Catalogs.carrerasPorEscuela[school].orEmpty()
        val matches = available.filter {
            Catalogs.normalize(it).contains(Catalogs.normalize(search))
        }
        if (d.carreras.isNotEmpty()) {
            Text(
                "Seleccionadas (${d.carreras.size}/3)",
                style = MaterialTheme.typography.titleSmall,
            )
            d.carreras.forEach { carrera ->
                InputChip(
                    selected = true,
                    onClick = {
                        update {
                            it.copy(
                                carreras = it.carreras - carrera,
                                otraCarrera =
                                    if (carrera == Catalogs.otraCarrera) "" else it.otraCarrera,
                            )
                        }
                    },
                    enabled = !busy,
                    label = { Text("$carrera · quitar") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        Text("${matches.size} carreras encontradas", style = MaterialTheme.typography.bodySmall)
        val shown = if (expanded) matches else matches.take(12)
        shown.forEach { carrera ->
            val selected = carrera in d.carreras
            FilterChip(
                selected = selected,
                enabled = !busy && (selected || d.carreras.size < 3),
                onClick = {
                    update {
                        it.copy(
                            carreras =
                                if (selected) it.carreras - carrera else it.carreras + carrera
                        )
                    }
                },
                label = { Text(carrera) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (matches.size > 12)
            TextButton(onClick = { expanded = !expanded }) {
                Text(if (expanded) "Mostrar menos" else "Mostrar las ${matches.size} carreras")
            }
        val otherSelected = Catalogs.otraCarrera in d.carreras
        FilterChip(
            selected = otherSelected,
            enabled = !busy && (otherSelected || d.carreras.size < 3),
            onClick = {
                update {
                    it.copy(
                        carreras =
                            if (otherSelected) it.carreras - Catalogs.otraCarrera
                            else it.carreras + Catalogs.otraCarrera,
                        otraCarrera = if (otherSelected) "" else it.otraCarrera,
                    )
                }
            },
            label = { Text(Catalogs.otraCarrera) },
            modifier = Modifier.fillMaxWidth(),
        )
        androidx.compose.animation.AnimatedVisibility(otherSelected) {
            Column {
                Input(
                    "Otra carrera de interés *",
                    d.otraCarrera,
                    { value -> update { it.copy(otraCarrera = value) } },
                    errors["otraCarrera"],
                    enabled = !busy,
                )
                Text(
                    "Esta preferencia queda por confirmar con orientación académica.",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        Text(
            "Catálogo de carreras consultado en Duoc UC. La selección expresa interés; confirma disponibilidad por sede, jornada y modalidad.",
            style = MaterialTheme.typography.bodySmall,
        )
        Select(
            "Modalidad preferida",
            d.modalidad,
            listOf("Presencial", "Online"),
            { v -> update { it.copy(modalidad = v) } },
        )
        Select(
            "Canal de contacto",
            d.canal,
            listOf("Correo", "Teléfono", "WhatsApp"),
            { v -> update { it.copy(canal = v) } },
        )
        Input(
            "Observaciones (opcional)",
            d.observaciones,
            { v -> update { it.copy(observaciones = v) } },
            errors["observaciones"],
            lines = 3,
        )
        NativeCapture(d, update)
        Button(enabled = !busy, onClick = onSave, modifier = Modifier.fillMaxWidth()) {
            if (busy) {
                CircularProgressIndicator(
                    Modifier.size(18.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(if (busy) "Guardando…" else "Guardar prospecto en dispositivo")
        }
        TextButton(onClick = onBack, enabled = !busy) { Text("Volver a datos personales") }
        Text(
            "No se envía información al sistema central en esta versión.",
            style = MaterialTheme.typography.bodySmall,
        )
    }
}
