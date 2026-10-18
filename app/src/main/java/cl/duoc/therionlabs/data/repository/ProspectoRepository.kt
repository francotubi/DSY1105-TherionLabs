package cl.duoc.therionlabs.data.repository

import androidx.room.withTransaction
import cl.duoc.therionlabs.data.local.AppDatabase
import cl.duoc.therionlabs.data.local.ProspectoEntity
import cl.duoc.therionlabs.domain.DuplicateProspectException
import cl.duoc.therionlabs.domain.EventRules
import cl.duoc.therionlabs.domain.Validators
import java.util.Locale

class ProspectoRepository(private val db: AppDatabase) {
    private val dao = db.prospectoDao()
    val todosLosProspectos = dao.obtenerProspectos()

    suspend fun guardarProspecto(prospecto: ProspectoEntity) = db.withTransaction {
        val event = db.operacionDao().evento(prospecto.eventoId)
        require(event != null && event.activo && !EventRules.isDemo(event.id)) {
            "El evento ya no está disponible."
        }
        val rut = Validators.normalizeRut(prospecto.rut)
        val email = prospecto.email.trim().lowercase(Locale.ROOT)
        if (dao.existeRut(prospecto.eventoId, rut))
            throw DuplicateProspectException("Este RUT ya está registrado en este evento.")
        if (dao.existeCorreo(prospecto.eventoId, email))
            throw DuplicateProspectException("Este correo ya está registrado en este evento.")
        dao.insertarProspecto(prospecto.copy(rut = rut, email = email, emailNormalizado = email))
    }

    suspend fun obtenerProspectoPorId(id: Int) = dao.obtenerPorId(id)
}
