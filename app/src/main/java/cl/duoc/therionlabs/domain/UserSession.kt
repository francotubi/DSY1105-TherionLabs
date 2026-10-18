package cl.duoc.therionlabs.domain

data class UserSession(val id: String, val nombre: String, val rol: String)

object Roles {
    const val CAPTADOR = "CAPTADOR"
    const val ADMINISTRATIVO = "ADMINISTRATIVO"
}

object AccessPolicy {
    fun canCapture(user: UserSession?) = user?.rol == Roles.CAPTADOR

    fun canManage(user: UserSession?) = user?.rol == Roles.ADMINISTRATIVO

    fun canRead(user: UserSession?, owner: String) =
        user != null && (canManage(user) || (canCapture(user) && user.id == owner))
}
