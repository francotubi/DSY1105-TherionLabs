package cl.duoc.therionlabs.domain

import java.io.Serializable

data class CaptureDraft(
    val captador: String = "",
    val eventoId: String = "",
    val eventoNombre: String = "",
    val rut: String = "",
    val nombre: String = "",
    val apellidos: String = "",
    val fechaNacimiento: String = "",
    val celular: String = "",
    val email: String = "",
    val establecimiento: String = "",
    val comunaResidencia: String = "",
    val comunaEstablecimiento: String = "",
    val actividadActual: String = "4to Medio",
    val jornada: String = "Diurna",
    val region: String = "",
    val sede: String = "",
    val nivelInteres: String = "Alto",
    val carreras: List<String> = emptyList(),
    val otraCarrera: String = "",
    val modalidad: String = "Presencial",
    val canal: String = "Correo",
    val observaciones: String = "",
    val fotoUri: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val precisionMetros: Float? = null,
    val fechaUbicacion: Long? = null,
) : Serializable
