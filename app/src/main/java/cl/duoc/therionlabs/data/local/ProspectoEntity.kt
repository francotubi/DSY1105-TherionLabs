package cl.duoc.therionlabs.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "prospectos",
    indices = [Index(value = ["eventoId", "emailNormalizado"], unique = true)],
)
data class ProspectoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val rut: String,
    val nombre: String,
    val apellidos: String,
    val fechaNacimiento: String,
    val celular: String,
    val email: String,
    val establecimiento: String,
    val comunaResidencia: String,
    val comunaEstablecimiento: String,
    val actividadActual: String,
    val jornada: String,
    val region: String,
    val sede: String,
    val nivelInteres: String,
    val carrera1: String,
    val carrera2: String?,
    val carrera3: String?,
    @ColumnInfo(defaultValue = "''") val eventoId: String = "",
    @ColumnInfo(defaultValue = "''") val eventoNombre: String = "",
    @ColumnInfo(defaultValue = "''") val captador: String = "",
    @ColumnInfo(defaultValue = "''") val emailNormalizado: String = "",
    @ColumnInfo(defaultValue = "0") val fechaCaptura: Long = 0,
    @ColumnInfo(defaultValue = "'PENDIENTE'") val estado: String = "PENDIENTE",
    @ColumnInfo(defaultValue = "'Presencial'") val modalidad: String = "Presencial",
    @ColumnInfo(defaultValue = "'Correo'") val canal: String = "Correo",
    @ColumnInfo(defaultValue = "''") val observaciones: String = "",
    @ColumnInfo(defaultValue = "''") val captadorId: String = "",
    @ColumnInfo(defaultValue = "''") val otraCarrera: String = "",
    val fotoUri: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val precisionMetros: Float? = null,
    val fechaUbicacion: Long? = null,
)
