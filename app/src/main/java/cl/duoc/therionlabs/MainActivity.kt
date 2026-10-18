package cl.duoc.therionlabs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import cl.duoc.therionlabs.data.local.*
import cl.duoc.therionlabs.data.viewmodel.*
import cl.duoc.therionlabs.domain.*
import cl.duoc.therionlabs.ui.screens.*
import cl.duoc.therionlabs.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { TherionLabsAppTheme { AppRoot() } }
    }
}

@Composable
fun AppRoot(auth: AuthViewModel = viewModel(), vm: ProspectoViewModel = viewModel()) {
    val user by auth.session.collectAsStateWithLifecycle()
    val loginBusy by auth.busy.collectAsStateWithLifecycle()
    val loginError by auth.error.collectAsStateWithLifecycle()
    LaunchedEffect(user?.id) { vm.setUser(user) }
    Surface(Modifier.fillMaxSize()) {
        if (user == null) {
            Box(Modifier.safeDrawingPadding().imePadding()) {
                LoginScreen(loginBusy, loginError, auth::login)
            }
        } else {
            val current = user!!
            key(current.id) {
                SessionNavigation(current, vm) {
                    vm.clearDraft()
                    vm.setUser(null)
                    auth.logout()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionNavigation(user: UserSession, vm: ProspectoViewModel, logout: () -> Unit) {
    val nav = rememberNavController()
    val records by vm.registros.collectAsStateWithLifecycle()
    val events by vm.eventos.collectAsStateWithLifecycle()
    val followUps by vm.gestiones.collectAsStateWithLifecycle()
    val d by vm.draft.collectAsStateWithLifecycle()
    val errors by vm.errors.collectAsStateWithLifecycle()
    val eventDraft by vm.eventDraft.collectAsStateWithLifecycle()
    val eventErrors by vm.eventErrors.collectAsStateWithLifecycle()
    val busy by vm.busy.collectAsStateWithLifecycle()
    val message by vm.message.collectAsStateWithLifecycle()
    val entry by nav.currentBackStackEntryAsState()
    val route = entry?.destination?.route
    val admin = AccessPolicy.canManage(user)
    val snack = remember { SnackbarHostState() }
    var logoutOpen by remember { mutableStateOf(false) }
    var managementDirty by remember { mutableStateOf(false) }
    var pendingNavigation by remember { mutableStateOf<(() -> Unit)?>(null) }
    val captureDirty = route in listOf("formulario", "preferencias") && vm.hasUnsavedData()
    val eventDirty = route == "crearEvento" && eventDraft.hasChanges()
    fun leave(action: () -> Unit) {
        if (busy) return
        if (captureDirty || eventDirty || managementDirty) pendingNavigation = action else action()
    }
    var switchEvent by remember { mutableStateOf<EventoEntity?>(null) }
    LaunchedEffect(message) {
        message?.let {
            snack.showSnackbar(it)
            vm.clearMessage()
        }
    }
    fun go(path: String) {
        nav.navigate(path) { launchSingleTop = true }
    }
    fun tab(path: String) {
        nav.navigate(path) {
            popUpTo("eventos")
            launchSingleTop = true
        }
    }
    fun capture(event: EventoEntity) {
        if (d.eventoId != event.id) vm.selectEvent(event)
        go("formulario")
    }
    BackHandler(
        enabled = (captureDirty && route == "formulario") || eventDirty || managementDirty
    ) {
        leave { nav.popBackStack() }
    }
    BackHandler(enabled = busy) {}
    if (pendingNavigation != null)
        AlertDialog(
            onDismissRequest = { pendingNavigation = null },
            title = { Text("¿Salir sin guardar?") },
            text = {
                Text("Hay datos sin guardar. Puedes continuar completándolos o descartarlos.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val action = pendingNavigation
                        pendingNavigation = null
                        if (captureDirty) vm.clearDraft()
                        if (eventDirty) vm.clearEventDraft()
                        managementDirty = false
                        action?.invoke()
                    }
                ) {
                    Text("Descartar y salir")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingNavigation = null }) { Text("Seguir completando") }
            },
        )
    if (logoutOpen)
        AlertDialog(
            onDismissRequest = { logoutOpen = false },
            title = { Text("¿Cerrar sesión?") },
            text = {
                Text(
                    if (vm.hasUnsavedData() || eventDraft.hasChanges() || managementDirty)
                        "Los datos sin guardar del formulario se descartarán. Las fichas y gestiones guardadas se conservan."
                    else "Las fichas y gestiones guardadas se conservarán en este dispositivo."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        logoutOpen = false
                        vm.clearEventDraft()
                        logout()
                    }
                ) {
                    Text("Cerrar sesión")
                }
            },
            dismissButton = {
                TextButton(onClick = { logoutOpen = false }) { Text("Continuar trabajando") }
            },
        )
    switchEvent?.let { event ->
        AlertDialog(
            onDismissRequest = { switchEvent = null },
            title = { Text("¿Cambiar de evento?") },
            text = {
                Text("Hay un formulario sin guardar. Cambiar de evento descartará ese borrador.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.selectEvent(event)
                        switchEvent = null
                        go("formulario")
                    }
                ) {
                    Text("Cambiar evento")
                }
            },
            dismissButton = {
                TextButton(onClick = { switchEvent = null }) { Text("Conservar borrador") }
            },
        )
    }
    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Image(
                            painterResource(R.drawable.duoc_logo),
                            "Duoc UC",
                            Modifier.width(135.dp).height(40.dp),
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = DuocBlue),
                    actions = {
                        Text(
                            if (admin) "Administración" else "Captación",
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(end = 16.dp),
                        )
                    },
                )
                Box(Modifier.fillMaxWidth().height(4.dp).background(DuocYellow))
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = route == "eventos" || route == "evento/{id}",
                    enabled = !busy,
                    onClick = { leave { tab("eventos") } },
                    icon = { Icon(Icons.Outlined.Event, null) },
                    label = { Text("Eventos") },
                )
                NavigationBarItem(
                    selected = route == "registros" || route == "gestiones",
                    enabled = !busy,
                    onClick = { leave { tab(if (admin) "gestiones" else "registros") } },
                    icon = { Icon(Icons.Outlined.History, null) },
                    label = { Text(if (admin) "Gestiones" else "Mis capturas") },
                )
                NavigationBarItem(
                    selected = route == "cuenta",
                    enabled = !busy,
                    onClick = { leave { tab("cuenta") } },
                    icon = { Icon(Icons.Outlined.PersonOutline, null) },
                    label = { Text("Cuenta") },
                )
            }
        },
        snackbarHost = { SnackbarHost(snack) },
    ) { padding ->
        NavHost(
            nav,
            startDestination = "eventos",
            modifier = Modifier.padding(padding),
            enterTransition = { fadeIn() + slideInHorizontally { it / 10 } },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() },
        ) {
            composable("eventos") {
                EventsScreen(user, events, records, busy, { go("crearEvento") }, vm::deleteEvent) {
                    event ->
                    if (admin) go("evento/${event.id}")
                    else if (d.eventoId != event.id && vm.hasUnsavedData()) switchEvent = event
                    else capture(event)
                }
            }
            composable("crearEvento") {
                if (admin)
                    CreateEventScreen(
                        eventDraft,
                        eventErrors,
                        busy,
                        vm::updateEvent,
                        { vm.createEvent { nav.popBackStack() } },
                        { leave { nav.popBackStack() } },
                    )
                else RestrictedScreen()
            }
            composable("evento/{id}") { back ->
                if (admin) {
                    val id = back.arguments?.getString("id")
                    val event = events.find { it.id == id }
                    ProspectsScreen(
                        event?.nombre ?: "Registros anteriores",
                        event?.colegio ?: "Información conservada de la versión anterior",
                        records.filter { it.eventoId == id },
                        followUps,
                        true,
                        { go("ficha/$it") },
                        { nav.popBackStack() },
                    )
                } else RestrictedScreen()
            }
            composable("formulario") {
                if (AccessPolicy.canCapture(user) && d.eventoId.isNotBlank()) {
                    Column {
                        Text(
                            d.eventoNombre,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelLarge,
                        )
                        Box(Modifier.weight(1f)) {
                            FormScreen(d, errors, vm::update) {
                                if (vm.personalValid()) go("preferencias")
                            }
                        }
                    }
                } else RestrictedScreen()
            }
            composable("preferencias") {
                if (AccessPolicy.canCapture(user) && d.eventoId.isNotBlank())
                    SelectorSedeScreen(
                        d,
                        errors,
                        busy,
                        vm::update,
                        {
                            vm.save { id ->
                                nav.navigate("ficha/$id") {
                                    popUpTo("eventos")
                                    launchSingleTop = true
                                }
                            }
                        },
                        { nav.popBackStack() },
                    )
                else RestrictedScreen()
            }
            composable("registros") {
                if (!admin)
                    ProspectsScreen(
                        "Mis capturas",
                        "Prospectos registrados con tu cuenta en este dispositivo.",
                        records,
                        emptyList(),
                        false,
                        { go("ficha/$it") },
                        { tab("eventos") },
                    )
                else RestrictedScreen()
            }
            composable("ficha/{id}") { back ->
                val id = back.arguments?.getString("id")?.toIntOrNull()
                val p = records.find { it.id == id }
                DetailScreen(
                    p,
                    footer = {
                        if (admin && p != null)
                            ManagementScreen(p, followUps, busy, { managementDirty = it }) {
                                state,
                                channel,
                                note,
                                done ->
                                vm.guardarGestion(p.id, state, channel, note, done)
                            }
                    },
                    onBack = { leave { nav.popBackStack() } },
                )
            }
            composable("gestiones") {
                if (admin) FollowUpHistoryScreen(followUps, records) { go("ficha/$it") }
                else RestrictedScreen()
            }
            composable("cuenta") { AccountScreen(user) { logoutOpen = true } }
        }
    }
}

@Composable
fun RestrictedScreen() {
    Column(Modifier.padding(24.dp)) { Text("Esta función no está disponible para tu perfil.") }
}
