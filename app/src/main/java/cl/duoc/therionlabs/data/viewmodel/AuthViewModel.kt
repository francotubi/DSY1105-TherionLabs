package cl.duoc.therionlabs.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.therionlabs.data.local.AppDatabase
import cl.duoc.therionlabs.data.repository.OperacionRepository
import cl.duoc.therionlabs.domain.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = OperacionRepository(AppDatabase.getDatabase(app))
    private val _session = MutableStateFlow<UserSession?>(null)
    val session = _session.asStateFlow()
    private val _busy = MutableStateFlow(false)
    val busy = _busy.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun login(name: String, password: String) {
        if (_busy.value) return
        if (name.isBlank() || password.isBlank()) {
            _error.value = "Ingresa usuario y contraseña."
            return
        }
        _busy.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                repo.initialize()
                val user = repo.login(name, password)
                if (user == null) _error.value = "Usuario o contraseña incorrectos."
                else _session.value = user
            } catch (_: Exception) {
                _error.value = "No fue posible abrir la sesión local. Vuelve a intentar."
            } finally {
                _busy.value = false
            }
        }
    }

    fun logout() {
        _session.value = null
        _error.value = null
    }
}
