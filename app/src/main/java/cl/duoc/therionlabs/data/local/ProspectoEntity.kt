package cl.duoc.therionlabs.data.local
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prospectos")
data class ProspectoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Datos del prospecto
    val rut: String,
    val nombre: String,
    val apellidos: String,
    val fechaNacimiento: String,
    val celular: String,
    val email: String,

    // Datos académicos
    val establecimiento: String,
    val comunaResidencia: String,
    val comunaEstablecimiento: String,
    val actividadActual: String,
    val jornada: String,

    // Selección de sede
    val region: String,
    val sede: String,
    val nivelInteres: String,

    // Preferencias académicas
    val carrera1: String,
    val carrera2: String?,
    val carrera3: String?
)
