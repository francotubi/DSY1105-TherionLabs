package cl.duoc.therionlabs.domain

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

object Validators {
    fun normalizeRut(value: String) = value.replace(".", "").replace("-", "").trim().uppercase()

    fun validRut(value: String): Boolean {
        val rut = normalizeRut(value)
        if (!Regex("""[0-9]{7,8}[0-9K]""").matches(rut)) return false
        var factor = 2
        val sum =
            rut.dropLast(1).reversed().sumOf { digit ->
                val v = digit.digitToInt() * factor
                factor = if (factor == 7) 2 else factor + 1
                v
            }
        val check =
            when (val r = 11 - sum % 11) {
                11 -> "0"
                10 -> "K"
                else -> r.toString()
            }
        return rut.takeLast(1) == check
    }

    fun validEmail(value: String) =
        value.trim().length <= 150 &&
            Regex(
                    """^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?(?:\.[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?)+$"""
                )
                .matches(value.trim())

    fun normalizePhone(value: String): String {
        val v = value.filter(Char::isDigit)
        return when {
            v.length == 9 -> "+56$v"
            v.length == 11 && v.startsWith("56") -> "+$v"
            else -> value.trim()
        }
    }

    fun validPhone(value: String) = Regex("""\+569[0-9]{8}""").matches(normalizePhone(value))

    fun validDate(value: String): Boolean =
        try {
            val date =
                LocalDate.parse(
                    value,
                    DateTimeFormatter.ofPattern("dd/MM/uuuu")
                        .withResolverStyle(ResolverStyle.STRICT),
                )
            date <= LocalDate.now() && date >= LocalDate.now().minusYears(110)
        } catch (_: Exception) {
            false
        }

    fun personal(d: CaptureDraft): Map<String, String> = buildMap {
        if (d.captador.trim().length !in 3..100)
            put("captador", "Identifica al captador (3 a 100 caracteres).")
        if (d.eventoId.isBlank()) put("eventoId", "Selecciona la visita o actividad.")
        if (!validRut(d.rut)) put("rut", "RUT inválido. Incluye su dígito verificador.")
        if (d.nombre.trim().length !in 2..80)
            put("nombre", "Ingresa un nombre de 2 a 80 caracteres.")
        if (d.apellidos.trim().length !in 2..100)
            put("apellidos", "Ingresa apellidos de 2 a 100 caracteres.")
        if (!validDate(d.fechaNacimiento))
            put("fechaNacimiento", "Usa DD/MM/AAAA y una fecha válida no futura.")
        if (!validPhone(d.celular)) put("celular", "Usa un celular chileno: +56 9 y ocho dígitos.")
        if (!validEmail(d.email))
            put("email", "Ingresa un correo válido, por ejemplo nombre@dominio.cl.")
        if (d.establecimiento.trim().length !in 3..150)
            put("establecimiento", "Ingresa el establecimiento (3 a 150 caracteres).")
        if (d.comunaResidencia.trim().length !in 2..100)
            put("comunaResidencia", "Ingresa tu comuna de residencia.")
        if (d.comunaEstablecimiento.trim().length !in 2..100)
            put("comunaEstablecimiento", "Ingresa la comuna del establecimiento.")
    }

    fun academic(d: CaptureDraft): Map<String, String> = buildMap {
        if (!Catalogs.sedes.containsKey(d.region)) put("region", "Selecciona una región.")
        if (d.sede !in (Catalogs.sedes[d.region] ?: emptyList()))
            put("sede", "Selecciona una sede de esa región.")
        if (
            d.carreras.size !in 1..3 ||
                d.carreras.distinct().size != d.carreras.size ||
                d.carreras.any { it !in Catalogs.carreras }
        )
            put("carreras", "Selecciona de una a tres carreras diferentes.")
        if (Catalogs.otraCarrera in d.carreras) {
            if (d.otraCarrera.trim().length !in 3..120)
                put("otraCarrera", "Escribe la carrera que buscas (3 a 120 caracteres).")
            else if (
                Catalogs.carrerasOficiales.any {
                    Catalogs.normalize(it) == Catalogs.normalize(d.otraCarrera)
                }
            )
                put(
                    "otraCarrera",
                    "La carrera ya aparece en el catálogo. Selecciónala de la lista.",
                )
        } else if (d.otraCarrera.isNotBlank())
            put("otraCarrera", "Elimina el texto o selecciona Otra / no la encuentro.")
        if (d.observaciones.length > 500) put("observaciones", "Máximo 500 caracteres.")
    }
}
