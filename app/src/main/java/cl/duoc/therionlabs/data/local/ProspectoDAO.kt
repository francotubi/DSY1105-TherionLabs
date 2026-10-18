package cl.duoc.therionlabs.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProspectoDAO {
    @Query(
        "SELECT EXISTS(SELECT 1 FROM prospectos WHERE eventoId = :event AND REPLACE(REPLACE(REPLACE(UPPER(rut), '.', ''), '-', ''), ' ', '') = :rut)"
    )
    suspend fun existeRut(event: String, rut: String): Boolean

    @Query(
        "SELECT EXISTS(SELECT 1 FROM prospectos WHERE eventoId = :event AND LOWER(TRIM(email)) = :email)"
    )
    suspend fun existeCorreo(event: String, email: String): Boolean

    @Insert suspend fun insertarProspecto(prospecto: ProspectoEntity): Long

    @Query("SELECT * FROM prospectos ORDER BY fechaCaptura DESC, id DESC")
    fun obtenerProspectos(): Flow<List<ProspectoEntity>>

    @Query("SELECT * FROM prospectos WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): ProspectoEntity?
}
