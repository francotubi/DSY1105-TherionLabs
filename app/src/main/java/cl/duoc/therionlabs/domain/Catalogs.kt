package cl.duoc.therionlabs.domain

object Catalogs {
    const val otraCarrera = "Otra / no la encuentro"
    val sedes =
        linkedMapOf(
            "Región Metropolitana" to
                listOf(
                    "Alameda",
                    "Antonio Varas",
                    "Maipú",
                    "Melipilla",
                    "Padre Alonso de Ovalle",
                    "Plaza Norte",
                    "Plaza Oeste",
                    "Plaza Vespucio",
                    "Puente Alto",
                    "San Bernardo",
                    "San Carlos de Apoquindo",
                    "San Joaquín",
                ),
            "Región de Valparaíso" to listOf("Viña del Mar", "Valparaíso"),
            "Región del Biobío" to
                listOf("San Andrés de Concepción", "Campus Arauco", "Campus Nacimiento"),
            "Región de La Araucanía" to listOf("Villarrica"),
            "Región de Los Lagos" to listOf("Puerto Montt"),
        )
    val carrerasPorEscuela =
        linkedMapOf(
            "Administración y Negocios" to
                listOf(
                    "Administración Pública",
                    "Administración de Empresas",
                    "Auditoría",
                    "Auditoría y Análisis de Negocios",
                    "Comercio Exterior",
                    "Contabilidad General Mención Legislación Tributaria",
                    "Contabilidad Tributaria",
                    "Ingeniería en Administración Mención Finanzas",
                    "Ingeniería en Administración Mención Gestión de Personas",
                    "Ingeniería en Administración Mención Innovación y Emprendimiento",
                    "Ingeniería en Comercio Exterior",
                    "Ingeniería en Control de Gestión",
                    "Ingeniería en Gestión Logística",
                    "Ingeniería en Gestión de Personas",
                    "Ingeniería en Marketing Digital",
                    "Técnico en Administración",
                    "Técnico en Gestión Logística",
                    "Técnico en Operaciones Logísticas",
                ),
            "Comunicación" to
                listOf(
                    "Animación Digital",
                    "Comunicación Audiovisual",
                    "Ingeniería en Sonido",
                    "Publicidad",
                    "Relaciones Públicas y Comunicación Organizacional",
                    "Tecnología en Sonido e Iluminación",
                    "Técnico Audiovisual",
                    "Técnico en Trabajo Social",
                ),
            "Construcción" to
                listOf(
                    "Conservación y Restauración del Patrimonio",
                    "Ingeniería en Administración de Seguridad Privada",
                    "Ingeniería en Construcción",
                    "Ingeniería en Prevención de Riesgos",
                    "Técnico Modelador Proyectista",
                    "Técnico Topógrafo Geomático",
                    "Técnico en Construcción",
                    "Técnico en Prevención de Riesgos",
                    "Técnico en Prevención de Riesgos Laborales",
                ),
            "Diseño" to
                listOf(
                    "Desarrollo y Diseño Web",
                    "Diseño Gráfico",
                    "Diseño Industrial e Innovación en Productos",
                    "Diseño de Ambientes",
                    "Diseño de Vestuario",
                    "Ilustración para Contextos Globales",
                ),
            "Gastronomía" to listOf("Gastronomía", "Gastronomía Internacional"),
            "Informática y Telecomunicaciones" to
                listOf(
                    "Analista Programador",
                    "Analista Programador Computacional",
                    "Desarrollo de Aplicaciones",
                    "Ingeniería en Ciberseguridad",
                    "Ingeniería en Ciencia de Datos",
                    "Ingeniería en Desarrollo de Software",
                    "Ingeniería en Informática",
                    "Ingeniería en Infraestructura Tecnológica",
                    "Ingeniería en Redes y Telecomunicaciones",
                    "Técnico de Redes y Telecomunicaciones",
                ),
            "Ingeniería y Recursos Naturales" to
                listOf(
                    "Ingeniería Agrícola",
                    "Ingeniería Industrial",
                    "Ingeniería en Automatización y Control Industrial",
                    "Ingeniería en Electricidad y Automatización Industrial",
                    "Ingeniería en Mantenimiento Industrial",
                    "Ingeniería en Maquinaria y Vehículos Pesados",
                    "Ingeniería en Mecánica Automotriz y Autotrónica",
                    "Ingeniería en Minas",
                    "Ingeniería en Recursos Naturales y Sostenibilidad",
                    "Técnico Veterinario y Pecuario",
                    "Técnico en Calidad de Alimentos",
                    "Técnico en Control y Monitoreo Remoto de Procesos Mineros",
                    "Técnico en Desabolladura y Pintura Automotriz",
                    "Técnico en Electricidad y Automatización Industrial",
                    "Técnico en Electricidad y Energías Renovables",
                    "Técnico en Geología",
                    "Técnico en Mantenimiento Industrial",
                    "Técnico en Maquinaria y Vehículos Pesados",
                    "Técnico en Mecánica Automotriz y Autotrónica",
                    "Técnico en Operación y Supervisión de Procesos Mineros",
                ),
            "Salud y Bienestar" to
                listOf(
                    "Informática Biomédica",
                    "Preparador Físico",
                    "Técnico en Enfermería",
                    "Técnico en Laboratorio Clínico, Banco de Sangre e Imagenología",
                    "Técnico en Odontología",
                    "Técnico en Química y Farmacia",
                ),
            "Turismo y Hospitalidad" to
                listOf(
                    "Administración en Turismo y Hospitalidad",
                    "Técnico en Turismo y Hospitalidad",
                ),
        )
    val carrerasOficiales = carrerasPorEscuela.values.flatten().distinct().sorted()
    val carreras = carrerasOficiales + otraCarrera

    fun normalize(value: String) =
        java.text.Normalizer.normalize(value, java.text.Normalizer.Form.NFD)
            .replace(Regex("""\p{M}+"""), "")
            .lowercase(java.util.Locale.ROOT)
            .trim()
}
