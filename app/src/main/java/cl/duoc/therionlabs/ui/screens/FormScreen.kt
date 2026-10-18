package cl.duoc.therionlabs.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.duoc.therionlabs.domain.CaptureDraft
import cl.duoc.therionlabs.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    d: CaptureDraft,
    errors: Map<String, String>,
    update: ((CaptureDraft) -> CaptureDraft) -> Unit,
    onNext: () -> Unit,
) {
    var dateOpen by remember { mutableStateOf(false) }
    if (dateOpen) {
        val state = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { dateOpen = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        state.selectedDateMillis?.let { v ->
                            val date =
                                java.time.Instant.ofEpochMilli(v)
                                    .atZone(java.time.ZoneOffset.UTC)
                                    .toLocalDate()
                            update {
                                it.copy(
                                    fechaNacimiento =
                                        date.format(
                                            java.time.format.DateTimeFormatter.ofPattern(
                                                "dd/MM/uuuu"
                                            )
                                        )
                                )
                            }
                        }
                        dateOpen = false
                    }
                ) {
                    Text("Seleccionar")
                }
            },
            dismissButton = { TextButton(onClick = { dateOpen = false }) { Text("Cancelar") } },
        ) {
            DatePicker(state = state)
        }
    }
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SectionTitle("Datos del prospecto", "Paso 1 de 2 · ${d.eventoNombre}")
        Input("RUT *", d.rut, { v -> update { it.copy(rut = v) } }, errors["rut"])
        Input("Nombre *", d.nombre, { v -> update { it.copy(nombre = v) } }, errors["nombre"])
        Input(
            "Apellidos *",
            d.apellidos,
            { v -> update { it.copy(apellidos = v) } },
            errors["apellidos"],
        )
        Input(
            "Fecha de nacimiento · DD/MM/AAAA *",
            d.fechaNacimiento,
            { v -> update { it.copy(fechaNacimiento = v) } },
            errors["fechaNacimiento"],
            KeyboardType.Number,
        )
        TextButton(onClick = { dateOpen = true }) { Text("Elegir fecha en calendario") }
        Input(
            "Celular · +56 9 1234 5678 *",
            d.celular,
            { v -> update { it.copy(celular = v) } },
            errors["celular"],
            KeyboardType.Phone,
        )
        Input(
            "Correo electrónico *",
            d.email,
            { v -> update { it.copy(email = v) } },
            errors["email"],
            KeyboardType.Email,
        )
        HorizontalDivider()
        SectionTitle("Establecimiento y actividad")
        Input(
            "Establecimiento *",
            d.establecimiento,
            { v -> update { it.copy(establecimiento = v) } },
            errors["establecimiento"],
        )
        Input(
            "Comuna de residencia *",
            d.comunaResidencia,
            { v -> update { it.copy(comunaResidencia = v) } },
            errors["comunaResidencia"],
        )
        Input(
            "Comuna del establecimiento *",
            d.comunaEstablecimiento,
            { v -> update { it.copy(comunaEstablecimiento = v) } },
            errors["comunaEstablecimiento"],
        )
        Select(
            "Actividad actual",
            d.actividadActual,
            listOf(
                "4to Medio",
                "Egresado enseñanza media",
                "Estudiante de educación superior",
                "Otra",
            ),
            { v -> update { it.copy(actividadActual = v) } },
        )
        Select(
            "Jornada de interés",
            d.jornada,
            listOf("Diurna", "Vespertina"),
            { v -> update { it.copy(jornada = v) } },
        )
        Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) {
            Text("Siguiente: intereses y visita")
        }
        Text("* Campo obligatorio", style = MaterialTheme.typography.bodySmall)
    }
}
