package cl.duoc.therionlabs.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities =
        [
            ProspectoEntity::class,
            UsuarioEntity::class,
            EventoEntity::class,
            SeguimientoEntity::class,
        ],
    version = 4,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun prospectoDao(): ProspectoDAO

    abstract fun operacionDao(): OperacionDAO

    companion object {
        @Volatile private var instance: AppDatabase? = null
        val MIGRATION_1_2 =
            object : Migration(1, 2) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    listOf(
                            "eventoId TEXT NOT NULL DEFAULT ''",
                            "eventoNombre TEXT NOT NULL DEFAULT ''",
                            "captador TEXT NOT NULL DEFAULT ''",
                            "emailNormalizado TEXT NOT NULL DEFAULT ''",
                            "fechaCaptura INTEGER NOT NULL DEFAULT 0",
                            "estado TEXT NOT NULL DEFAULT 'PENDIENTE'",
                            "modalidad TEXT NOT NULL DEFAULT 'Presencial'",
                            "canal TEXT NOT NULL DEFAULT 'Correo'",
                            "observaciones TEXT NOT NULL DEFAULT ''",
                            "fotoUri TEXT",
                            "latitud REAL",
                            "longitud REAL",
                            "precisionMetros REAL",
                            "fechaUbicacion INTEGER",
                        )
                        .forEach { db.execSQL("ALTER TABLE prospectos ADD COLUMN $it") }
                    db.execSQL(
                        "UPDATE prospectos SET eventoId = 'LEGADO-' || id, eventoNombre = 'Registro anterior sin evento', emailNormalizado = LOWER(TRIM(email))"
                    )
                    db.execSQL(
                        "CREATE UNIQUE INDEX index_prospectos_eventoId_emailNormalizado ON prospectos(eventoId,emailNormalizado)"
                    )
                }
            }

        val MIGRATION_2_3 =
            object : Migration(2, 3) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        "ALTER TABLE prospectos ADD COLUMN captadorId TEXT NOT NULL DEFAULT ''"
                    )
                    db.execSQL(
                        "ALTER TABLE prospectos ADD COLUMN otraCarrera TEXT NOT NULL DEFAULT ''"
                    )
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS usuarios (id TEXT NOT NULL, usuario TEXT NOT NULL, nombre TEXT NOT NULL, rol TEXT NOT NULL, salt TEXT NOT NULL, passwordHash TEXT NOT NULL, PRIMARY KEY(id))"
                    )
                    db.execSQL(
                        "CREATE UNIQUE INDEX IF NOT EXISTS index_usuarios_usuario ON usuarios(usuario)"
                    )
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS eventos (id TEXT NOT NULL, nombre TEXT NOT NULL, colegio TEXT NOT NULL, fecha TEXT NOT NULL, actividad TEXT NOT NULL, activo INTEGER NOT NULL, PRIMARY KEY(id))"
                    )
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS seguimientos (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, prospectoId INTEGER NOT NULL, administrativoId TEXT NOT NULL, administrativoNombre TEXT NOT NULL, estado TEXT NOT NULL, canal TEXT NOT NULL, observacion TEXT NOT NULL, fecha INTEGER NOT NULL, estadoEnvio TEXT NOT NULL, FOREIGN KEY(prospectoId) REFERENCES prospectos(id) ON UPDATE NO ACTION ON DELETE RESTRICT, FOREIGN KEY(administrativoId) REFERENCES usuarios(id) ON UPDATE NO ACTION ON DELETE RESTRICT)"
                    )
                    db.execSQL(
                        "CREATE INDEX IF NOT EXISTS index_seguimientos_prospectoId ON seguimientos(prospectoId)"
                    )
                    db.execSQL(
                        "CREATE INDEX IF NOT EXISTS index_seguimientos_administrativoId ON seguimientos(administrativoId)"
                    )
                }
            }

        val MIGRATION_3_4 =
            object : Migration(3, 4) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL("ALTER TABLE eventos ADD COLUMN comuna TEXT NOT NULL DEFAULT ''")
                }
            }

        fun getDatabase(context: Context): AppDatabase =
            instance
                ?: synchronized(this) {
                    instance
                        ?: Room.databaseBuilder(
                                context.applicationContext,
                                AppDatabase::class.java,
                                "therionlabs_database",
                            )
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                            .build()
                            .also { instance = it }
                }
    }
}
