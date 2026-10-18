package cl.duoc.therionlabs.data.viewmodel

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import cl.duoc.therionlabs.data.local.AppDatabase
import cl.duoc.therionlabs.data.local.EventoEntity
import cl.duoc.therionlabs.data.local.ProspectoEntity
import cl.duoc.therionlabs.data.repository.OperacionRepository
import cl.duoc.therionlabs.data.repository.ProspectoRepository
import cl.duoc.therionlabs.domain.*
import cl.duoc.therionlabs.domain.CaptureDraft
import cl.duoc.therionlabs.domain.Validators
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProspectoViewModel(application: Application, private val saved: SavedStateHandle) :
    AndroidViewModel(application) {
    private val repository = ProspectoRepository(AppDatabase.getDatabase(application))
    private val operations = OperacionRepository(AppDatabase.getDatabase(application))
    private val activeUser = MutableStateFlow<UserSession?>(null)
    val eventos =
        operations.eventos.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList(),
        )
    val gestiones =
        combine(operations.seguimientos, activeUser) { data, user ->
                if (AccessPolicy.canManage(user)) data else emptyList()
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val eventDraft = saved.getStateFlow("eventDraft", EventDraft())
    private val _eventErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val eventErrors = _eventErrors.asStateFlow()
    val draft = saved.getStateFlow("draft", CaptureDraft())
    val registros =
        combine(repository.todosLosProspectos, activeUser) { data, user ->
                data.filter { AccessPolicy.canRead(user, it.captadorId) }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList(),
            )
    private val _errors = MutableStateFlow<Map<String, String>>(emptyMap())
    val errors = _errors.asStateFlow()
    private val _busy = MutableStateFlow(false)
    val busy = _busy.asStateFlow()
    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    fun setUser(user: UserSession?) {
        if (activeUser.value?.id != user?.id) {
            activeUser.value = user
            clearEventDraft()
            saved["draft"] = CaptureDraft(captador = user?.nombre.orEmpty())
            _errors.value = emptyMap()
            _message.value = null
        }
    }

    fun updateEvent(transform: (EventDraft) -> EventDraft) {
        if (_busy.value || !AccessPolicy.canManage(activeUser.value)) return
        saved["eventDraft"] = transform(eventDraft.value)
        if (_eventErrors.value.isNotEmpty())
            _eventErrors.value = EventRules.validate(eventDraft.value)
    }

    fun clearEventDraft() {
        saved["eventDraft"] = EventDraft()
        _eventErrors.value = emptyMap()
    }

    fun deleteEvent(id: String) {
        val user = activeUser.value ?: return
        if (_busy.value || !AccessPolicy.canManage(user)) return
        _busy.value = true
        viewModelScope.launch {
            try {
                operations.eliminarEvento(user, id)
                _message.value = "Evento eliminado."
            } catch (e: IllegalArgumentException) {
                _message.value = e.message ?: "No se pudo eliminar el evento."
            } catch (_: Exception) {
                _message.value = "No se pudo eliminar. Reintenta."
            } finally {
                _busy.value = false
            }
        }
    }

    fun createEvent(done: () -> Unit) {
        val user = activeUser.value ?: return
        if (_busy.value || !AccessPolicy.canManage(user)) return
        val value = eventDraft.value
        _eventErrors.value = EventRules.validate(value)
        if (_eventErrors.value.isNotEmpty()) return
        _busy.value = true
        viewModelScope.launch {
            try {
                operations.crearEvento(user, value)
                clearEventDraft()
                _message.value = "Evento creado. Ya se puede seleccionar para registrar prospectos."
                done()
            } catch (e: IllegalArgumentException) {
                _message.value = e.message ?: "No se pudo crear el evento."
            } catch (_: Exception) {
                _message.value =
                    "No se pudo guardar. Los datos del evento se conservan para reintentar."
            } finally {
                _busy.value = false
            }
        }
    }

    fun selectEvent(event: EventoEntity): Boolean {
        val user = activeUser.value ?: return false
        if (!AccessPolicy.canCapture(user) || !event.activo || EventRules.isDemo(event.id))
            return false
        saved["draft"] =
            CaptureDraft(
                captador = user.nombre,
                eventoId = event.id,
                eventoNombre = event.nombre,
                establecimiento = event.colegio,
            )
        _errors.value = emptyMap()
        return true
    }

    fun hasUnsavedData(): Boolean {
        val d = draft.value
        return listOf(
                d.rut,
                d.nombre,
                d.apellidos,
                d.email,
                d.celular,
                d.fechaNacimiento,
                d.comunaResidencia,
                d.comunaEstablecimiento,
                d.observaciones,
                d.region,
                d.sede,
                d.otraCarrera,
            )
            .any { it.isNotBlank() } ||
            d.carreras.isNotEmpty() ||
            d.fotoUri != null ||
            d.latitud != null
    }

    fun clearDraft() {
        saved["draft"] = CaptureDraft(captador = activeUser.value?.nombre.orEmpty())
        _errors.value = emptyMap()
    }

    fun guardarGestion(id: Int, state: String, channel: String, note: String, done: () -> Unit) {
        val user = activeUser.value ?: return
        if (!AccessPolicy.canManage(user) || _busy.value) return
        val error = FollowUpRules.validate(state, channel, note)
        if (error != null) {
            _message.value = error
            return
        }
        _busy.value = true
        viewModelScope.launch {
            try {
                operations.guardarGestion(user, id, state, channel, note)
                _message.value = "Gestión registrada."
                done()
            } catch (_: Exception) {
                _message.value = "No se pudo guardar la gestión. Conserva los datos y reintenta."
            } finally {
                _busy.value = false
            }
        }
    }

    fun update(transform: (CaptureDraft) -> CaptureDraft) {
        if (_busy.value || !AccessPolicy.canCapture(activeUser.value)) return
        saved["draft"] = transform(draft.value)
        if (_errors.value.isNotEmpty())
            _errors.value = Validators.personal(draft.value) + Validators.academic(draft.value)
    }

    fun personalValid(): Boolean {
        _errors.value = Validators.personal(draft.value)
        return _errors.value.isEmpty()
    }

    fun clearMessage() {
        _message.value = null
    }

    fun save(onSaved: (Int) -> Unit) {
        if (_busy.value) return
        val user = activeUser.value ?: return
        if (!AccessPolicy.canCapture(user)) return
        val d = draft.value
        _errors.value = Validators.personal(d) + Validators.academic(d)
        if (_errors.value.isNotEmpty()) {
            _message.value = "Revisa los campos indicados antes de guardar."
            return
        }
        _busy.value = true
        viewModelScope.launch {
            try {
                val event =
                    AppDatabase.getDatabase(getApplication<Application>())
                        .operacionDao()
                        .evento(d.eventoId)
                require(event != null && event.activo && !EventRules.isDemo(event.id)) {
                    "El evento no está disponible."
                }
                val email = d.email.trim().lowercase(Locale.ROOT)
                val id =
                    repository
                        .guardarProspecto(
                            ProspectoEntity(
                                rut = Validators.normalizeRut(d.rut),
                                nombre = d.nombre.trim(),
                                apellidos = d.apellidos.trim(),
                                fechaNacimiento = d.fechaNacimiento,
                                celular = Validators.normalizePhone(d.celular),
                                email = email,
                                establecimiento = d.establecimiento.trim(),
                                comunaResidencia = d.comunaResidencia.trim(),
                                comunaEstablecimiento = d.comunaEstablecimiento.trim(),
                                actividadActual = d.actividadActual,
                                jornada = d.jornada,
                                region = d.region,
                                sede = d.sede,
                                nivelInteres = d.nivelInteres,
                                carrera1 = d.carreras[0],
                                carrera2 = d.carreras.getOrNull(1),
                                carrera3 = d.carreras.getOrNull(2),
                                eventoId = d.eventoId,
                                eventoNombre = d.eventoNombre,
                                captador = user.nombre,
                                captadorId = user.id,
                                otraCarrera = d.otraCarrera.trim(),
                                emailNormalizado = email,
                                fechaCaptura = System.currentTimeMillis(),
                                modalidad = d.modalidad,
                                canal = d.canal,
                                observaciones = d.observaciones.trim(),
                                fotoUri = d.fotoUri,
                                latitud = d.latitud,
                                longitud = d.longitud,
                                precisionMetros = d.precisionMetros,
                                fechaUbicacion = d.fechaUbicacion,
                            )
                        )
                        .toInt()
                saved["draft"] =
                    CaptureDraft(
                        captador = d.captador,
                        eventoId = d.eventoId,
                        eventoNombre = d.eventoNombre,
                    )
                _errors.value = emptyMap()
                _message.value = "Registro guardado en este dispositivo."
                onSaved(id)
            } catch (e: DuplicateProspectException) {
                _message.value = e.message
            } catch (_: SQLiteConstraintException) {
                _message.value =
                    "Ya existe un prospecto con ese correo en esta visita. No se guardó un duplicado."
            } catch (_: Exception) {
                _message.value =
                    "No se pudo guardar el registro. Tus datos permanecen en el formulario para reintentar."
            } finally {
                _busy.value = false
            }
        }
    }
}
