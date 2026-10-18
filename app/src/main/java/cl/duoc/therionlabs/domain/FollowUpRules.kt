package cl.duoc.therionlabs.domain

object FollowUpRules {
    val states =
        linkedMapOf(
            "SIN_CONTACTAR" to "Sin contactar",
            "EN_SEGUIMIENTO" to "En seguimiento",
            "CONTACTADO" to "Contactado",
            "CERRADO" to "Cerrado",
        )
    val channels = listOf("Teléfono", "Correo", "Otro")

    fun validate(state: String, channel: String, note: String): String? =
        when {
            state !in states -> "Selecciona un estado de seguimiento válido."
            channel !in channels -> "Selecciona el canal utilizado."
            note.trim().length !in 5..500 ->
                "Describe el resultado de la gestión (5 a 500 caracteres)."
            else -> null
        }

    fun label(state: String) = states[state] ?: "Sin contactar"
}
