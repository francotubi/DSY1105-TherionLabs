package cl.duoc.therionlabs.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorSedeScreen(navController: NavController) {

    // Valores seleccion de region
    var expandedRegion by remember { mutableStateOf(false) }
    var regionSeleccionada by remember { mutableStateOf("") }

    // Valores seleccion de sede
    var expandedSede by remember { mutableStateOf(false) }
    var sedeSeleccionada by remember { mutableStateOf("") }

    val datosSedes = remember {
        mapOf(
            "Región Metropolitana" to listOf(
                "Sede Alameda (Santiago centro)",
                "Antonio Varas (Providencia)",
                "Maipú",
                "Melipilla",
                "Padre Alonso de Ovalle (Stgo centro)",
                "Plaza Norte",
                "Plaza Oeste (Cerrillos)",
                "Plaza Vespucio",
                "Puente Alto",
                "San Bernardo",
                "San Carlos de Apoquindo (Las Condes)",
                "San Joaquin"
            ),
            "Región de Valparaiso" to listOf("Valparaiso", "Viña del Mar"),
            "Región del Biobío" to listOf("Concepción", "Campus Arauco", "Campus Nacimiento"),
            "Región de la Araucania" to listOf("Campus Villarica"),
            "Región de los Lagos" to listOf("Puerto Montt")
        )
    }

    val regiones = remember { datosSedes.keys.toList() }

    val sedesFiltradas = remember(regionSeleccionada) {
        datosSedes[regionSeleccionada] ?: emptyList()
    }

    // Selector de interés
    val interes = listOf("Interés Alto", "Interés Medio", "Interés Bajo")
    var selectedInteres by remember { mutableStateOf(interes[0]) }

    // Selector de carreras

    val carrerasSeleccionadas = remember { mutableStateListOf<String>() }

    var menuExpandido by remember { mutableStateOf(false) }

    val datosCarreras = remember {
        mapOf(
            "Administración y Negocios" to listOf(
                "Auditoría",
                "Ingeniería en Administración",
                "Técnico en Gestión Logística"
            ),
            "Informática y Telecomunicaciones" to listOf(
                "Ingeniería en Informática",
                "Ingeniería en Ciberseguridad",
                "Analista Programador"
            ),
            "Salud y Bienestar" to listOf(
                "Informática Biomédica",
                "Técnico de Enfermería",
                "Técnico en Química y Farmacia"
            ),
            "Diseño" to listOf("Diseño Gráfico", "Diseño de Vestuario", "Ilustración")
        )
    }

    // Campos nulos manager
    var mostrarErrores by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seleccion de sede") },
                actions = {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "Modo Local",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            Text("Selecciona una Región", style = MaterialTheme.typography.titleMedium)

            ExposedDropdownMenuBox(
                expanded = expandedRegion,
                onExpandedChange = { expandedRegion = !expandedRegion }
            ) {
                OutlinedTextField(
                    value = regionSeleccionada,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Selecciona una región") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRegion) },
                    isError = mostrarErrores && regionSeleccionada.isBlank(),
                    supportingText = {
                        if (mostrarErrores && regionSeleccionada.isBlank()) {
                            Text("Debes seleccionar una región")
                        }
                    },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedRegion,
                    onDismissRequest = { expandedRegion = false }
                ) {
                    regiones.forEach { region ->
                        DropdownMenuItem(
                            text = { Text(text = region) },
                            onClick = {
                                regionSeleccionada = region
                                sedeSeleccionada = ""
                                expandedRegion = false
                            }
                        )
                    }
                }
            }

            Text("Selecciona TU SEDE de interés", style = MaterialTheme.typography.titleMedium)
            ExposedDropdownMenuBox(
                expanded = expandedSede,
                onExpandedChange = {
                    if (regionSeleccionada.isNotEmpty()) expandedSede = !expandedSede
                }
            ) {
                OutlinedTextField(
                    value = sedeSeleccionada,
                    onValueChange = {},
                    readOnly = true,
                    enabled = regionSeleccionada.isNotEmpty(),
                    label = { Text("Selecciona una sede *") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSede)
                    },
                    isError = mostrarErrores && sedeSeleccionada.isBlank(),
                    supportingText = {
                        if (mostrarErrores && sedeSeleccionada.isBlank()) {
                            Text("Debes seleccionar una sede")
                        }
                    },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedSede,
                    onDismissRequest = { expandedSede = false }
                ) {
                    sedesFiltradas.forEach { sede ->
                        DropdownMenuItem(
                            text = { Text(sede) },
                            onClick = {
                                sedeSeleccionada = sede
                                expandedSede = false
                            }
                        )
                    }
                }
            }

            Text(
                "Marca tu nivel de interés por estudiar en DUOC UC",
                style = MaterialTheme.typography.titleMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                interes.forEach { interesItem ->
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = interesItem == selectedInteres,
                            onClick = { selectedInteres = interesItem }
                        )
                        Text(
                            text = interesItem,
                            modifier = Modifier.padding(start = 2.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Text(
                "Busca y agrega una carrera:",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            ExposedDropdownMenuBox(
                expanded = menuExpandido,
                onExpandedChange = { menuExpandido = !menuExpandido }
            ) {
                OutlinedTextField(
                    value = carrerasSeleccionadas.joinToString(", "),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Selecciona una carrera *") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpandido)
                    },
                    isError = mostrarErrores && carrerasSeleccionadas.isEmpty(),
                    supportingText = {
                        if (mostrarErrores && carrerasSeleccionadas.isEmpty()) {
                            Text("Debes seleccionar al menos una carrera")
                        }
                    },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = menuExpandido,
                    onDismissRequest = { menuExpandido = false }
                ) {
                    datosCarreras.forEach { (escuela, listaDeCarreras) ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    escuela.uppercase(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray
                                )
                            },
                            onClick = {},
                            enabled = false
                        )
                        listaDeCarreras.forEach { carrera ->
                            val yaSeleccionada = carrerasSeleccionadas.contains(carrera)
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        if (yaSeleccionada) "✓ $carrera" else "   $carrera",
                                        color = if (yaSeleccionada)
                                            MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = {
                                    if (yaSeleccionada) {
                                        carrerasSeleccionadas.remove(carrera)
                                    } else if (carrerasSeleccionadas.size < 3) {
                                        carrerasSeleccionadas.add(carrera)
                                    }
                                    menuExpandido = false
                                }
                            )
                        }
                    }
                }
            }

            Text("Mis Preferencias (Máximo 3):", fontSize = 16.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (i in 0..2) {
                    val carrera = carrerasSeleccionadas.getOrNull(i)
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (carrera != null)
                                MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.weight(1f).heightIn(min = 80.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(6.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "${i + 1}° Opción",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                carrera ?: "Vacío",
                                fontSize = 12.sp,
                                maxLines = 2
                            )
                        }
                    }
                }
            }

            if (carrerasSeleccionadas.isNotEmpty()) {
                TextButton(onClick = { carrerasSeleccionadas.clear() }) {
                    Text("Limpiar selección")
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Button(
                onClick = {
                    mostrarErrores = true
                    val regionValida = regionSeleccionada.isNotBlank()
                    val sedeValida = sedeSeleccionada.isNotBlank()
                    val carrerasValidas = carrerasSeleccionadas.size in 1..3
                    val interesValido = selectedInteres.isNotBlank()

                    if (regionValida && sedeValida && carrerasValidas && interesValido) {
                        // Aquí irá la implementación del guardado en Room.
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Guardar Prospecto")
            }
        }
    }
}
