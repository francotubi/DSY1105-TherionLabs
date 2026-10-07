package cl.duoc.therionlabs.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen() {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var observacion by remember { mutableStateOf("") }

    var expandedCurso by remember { mutableStateOf(false) }
    val cursos = listOf("Cloud Computing con AWS", "Microservicios en Spring Boot", "Ciberseguridad Defensiva", "Bases de Datos Oracle")
    var selectedCurso by remember { mutableStateOf(cursos[0]) }

    val modalidades = listOf("Online", "Presencial")
    var selectedModalidad by remember { mutableStateOf(modalidades[0]) }

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
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre Completo *") },
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

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono de Contacto (+569...) *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            ExposedDropdownMenuBox(
                expanded = expandedCurso,
                onExpandedChange = { expandedCurso = !expandedCurso }
            ) {
                OutlinedTextField(
                    value = selectedCurso,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Programa o Especialización") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCurso) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedCurso,
                    onDismissRequest = { expandedCurso = false }
                ) {
                    cursos.forEach { curso ->
                        DropdownMenuItem(
                            text = { Text(curso) },
                            onClick = {
                                selectedCurso = curso
                                expandedCurso = false
                            }
                        )
                    }
                }
            }

            Text("Modalidad Preferida", style = MaterialTheme.typography.titleSmall)
            Row(verticalAlignment = Alignment.CenterVertically) {
                modalidades.forEach { modalidad ->
                    RadioButton(
                        selected = (modalidad == selectedModalidad),
                        onClick = { selectedModalidad = modalidad }
                    )
                    Text(text = modalidad, modifier = Modifier.padding(end = 16.dp))
                }
            }

            OutlinedTextField(
                value = observacion,
                onValueChange = { observacion = it },
                label = { Text("Observaciones adicionales") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            Button(
                onClick = { /* Pendiente Room */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Guardar Prospecto")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FormScreenPreview() {
    FormScreen()
}