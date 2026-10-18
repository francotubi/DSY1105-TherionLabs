package cl.duoc.therionlabs.data.repository

import androidx.room.withTransaction
import cl.duoc.therionlabs.data.local.*
import cl.duoc.therionlabs.domain.*
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class OperacionRepository(private val db: AppDatabase) {
    private val dao = db.operacionDao()
    val eventos = dao.eventos().map { items -> items.filterNot { EventRules.isDemo(it.id) } }
    val seguimientos = dao.seguimientos()

    suspend fun initialize() =
        withContext(Dispatchers.IO) {
            db.withTransaction {
                if (dao.totalUsuarios() == 0) {
                    val accounts =
                        listOf(
                                listOf(
                                    "CAP-01",
                                    "captador",
                                    "Captador de terreno",
                                    Roles.CAPTADOR,
                                    "Captador2026!",
                                ),
                                listOf(
                                    "CAP-02",
                                    "captador2",
                                    "Segundo captador",
                                    Roles.CAPTADOR,
                                    "Captador2026!",
                                ),
                                listOf(
                                    "ADM-01",
                                    "administrativo",
                                    "Administración de prospectos",
                                    Roles.ADMINISTRATIVO,
                                    "Admin2026!",
                                ),
                            )
                            .map { row ->
                                val salt = PasswordHasher.salt()
                                UsuarioEntity(
                                    row[0],
                                    row[1],
                                    row[2],
                                    row[3],
                                    salt,
                                    PasswordHasher.hash(row[4], salt),
                                )
                            }
                    dao.insertarUsuarios(accounts)
                }
            }
        }

    suspend fun eliminarEvento(user: UserSession, id: String) = db.withTransaction {
        require(dao.usuarioPorId(user.id)?.rol == Roles.ADMINISTRATIVO) {
            "Solo administración puede eliminar eventos."
        }
        requireNotNull(dao.evento(id)) { "El evento ya no existe." }
        require(dao.eliminarEventoVacio(id) == 1) {
            "No se puede eliminar un evento con prospectos registrados."
        }
    }

    suspend fun crearEvento(user: UserSession, draft: EventDraft): String = db.withTransaction {
        require(dao.usuarioPorId(user.id)?.rol == Roles.ADMINISTRATIVO) {
            "Solo administración puede crear eventos."
        }
        require(EventRules.validate(draft).isEmpty()) { "Revisa los campos del evento." }
        val events = dao.listaEventos().filterNot { EventRules.isDemo(it.id) }
        require(
            events.none {
                Catalogs.normalize(it.nombre) == Catalogs.normalize(draft.nombre) &&
                    Catalogs.normalize(it.colegio) == Catalogs.normalize(draft.colegio) &&
                    it.fecha == draft.fecha
            }
        ) {
            "Ya existe ese evento para la misma institución y fecha."
        }
        val id = java.util.UUID.randomUUID().toString()
        dao.insertarEventos(
            listOf(
                EventoEntity(
                    id,
                    draft.nombre.trim(),
                    draft.colegio.trim(),
                    draft.fecha,
                    draft.actividad.trim(),
                    comuna = draft.comuna.trim(),
                )
            )
        )
        id
    }

    suspend fun login(name: String, password: String): UserSession? =
        withContext(Dispatchers.IO) {
            val user = dao.usuario(name.trim().lowercase(Locale.ROOT))
            if (user != null && PasswordHasher.matches(password, user.salt, user.passwordHash))
                UserSession(user.id, user.nombre, user.rol)
            else null
        }

    suspend fun guardarGestion(
        user: UserSession,
        prospectoId: Int,
        state: String,
        channel: String,
        note: String,
    ) = db.withTransaction {
        val persisted = requireNotNull(dao.usuarioPorId(user.id))
        require(persisted.rol == Roles.ADMINISTRATIVO) {
            "Solo el perfil administrativo puede registrar gestiones."
        }
        requireNotNull(db.prospectoDao().obtenerPorId(prospectoId)) {
            "El prospecto ya no está disponible."
        }
        val error = FollowUpRules.validate(state, channel, note)
        require(error == null) { error.orEmpty() }
        dao.insertarSeguimiento(
            SeguimientoEntity(
                prospectoId = prospectoId,
                administrativoId = user.id,
                administrativoNombre = persisted.nombre,
                estado = state,
                canal = channel,
                observacion = note.trim(),
                fecha = System.currentTimeMillis(),
            )
        )
    }
}
