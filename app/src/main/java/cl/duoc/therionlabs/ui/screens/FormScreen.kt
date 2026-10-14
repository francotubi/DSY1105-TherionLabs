package cl.duoc.therionlabs.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(navController: NavHostController) {

    // Datos personales
    var rutProspecto by remember { mutableStateOf("")}
    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("")}
    var establecimiento by remember {mutableStateOf("")}
    var fechaNacimiento by remember { mutableStateOf("") }
    var celular by remember { mutableStateOf("") }

    // Datos de actividad u enseñanzas
    var comunaResidencia by remember { mutableStateOf("") }
    var comunaEstablecimiento by remember { mutableStateOf("") }

    var expandedActividad by remember { mutableStateOf(false) }
    val actividadActual = listOf("4to Medio", "Egresado enseñanza media", "Otra")
    var actividadSeleccionada by remember {mutableStateOf(actividadActual[0])}

    var email by remember { mutableStateOf("") }

    val jornadas = listOf("Diurna", "Vespertina")
    var selectedJornada by remember { mutableStateOf(jornadas[0]) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("THERIONLABS - Registro") },
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
        )
            {

            Text(
                text = "Datos personales",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
                )

            OutlinedTextField(
                value = rutProspecto,
                onValueChange = {rutProspecto = it },
                label = { Text("Ingresa tu rut *")},
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
                OutlinedTextField(
                    value = apellidos,
                    onValueChange = { apellidos = it },
                    label = { Text("Apellidos *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = fechaNacimiento,
                    onValueChange = { fechaNacimiento = it },
                    label = { Text("Fecha nacimiento *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = celular,
                    onValueChange = { celular = it },
                    label = { Text("Celular *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text(
                    text = "Datos Establecimiento",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                OutlinedTextField(
                    value = establecimiento,
                    onValueChange = { establecimiento = it },
                    label = { Text("Establecimiento: *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = comunaResidencia,
                    onValueChange = { comunaResidencia = it },
                    label = { Text("Comuna de residencia *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = comunaEstablecimiento,
                    onValueChange = { comunaEstablecimiento = it },
                    label = { Text("Comuna de su establecimiento *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )


            val isEmailValid = email.isEmpty() || email.contains("@")
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico *") },
                isError = !isEmailValid,
                supportingText = {
                    if (!isEmailValid) Text("Formato de correo no válido")
                },
                trailingIcon = {
                    if (!isEmailValid) Text("!", color = MaterialTheme.colorScheme.error)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            ExposedDropdownMenuBox(
                expanded = expandedActividad,
                onExpandedChange = { expandedActividad = !expandedActividad }
            ) {
                OutlinedTextField(
                    value = actividadSeleccionada,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Actividad actual") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedActividad) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedActividad,
                    onDismissRequest = { expandedActividad = false }
                ) {
                    actividadActual.forEach { actividadActual ->
                        DropdownMenuItem(
                            text = { Text(actividadActual) },
                            onClick = {
                                actividadSeleccionada = actividadActual
                                expandedActividad = false
                            }
                        )
                    }
                }
            }

            Text("Jornada de estudio preferida", style = MaterialTheme.typography.titleSmall)
            Row(verticalAlignment = Alignment.CenterVertically) {
                jornadas.forEach { jornadas ->
                    RadioButton(
                        selected = (jornadas == selectedJornada),
                        onClick = { selectedJornada = jornadas }
                    )
                    Text(text = jornadas, modifier = Modifier.padding(end = 16.dp))
                }
            }

            Button(
                onClick = { navController.navigate("selector_sede") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("SIGUIENTE")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FormScreenPreview() {
    FormScreen(navController = rememberNavController())
}