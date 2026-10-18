# Captura digital móvil · Duoc UC

Equipo: Diego Barra, Franco Tubi, Maicol García y Benjamín Osorio.

Versión integrada con los ajustes acordados para la Evaluación 2. Android, Kotlin, Jetpack Compose y Material 3. Permite capturar prospectos por evento y gestionar su contacto desde un perfil administrativo. La persistencia es local mediante Room.

## Abrir y ejecutar

1. Extraer el ZIP en una carpeta nueva.
2. Android Studio > Open > seleccionar la carpeta que contiene `settings.gradle.kts`.
3. Usar el JDK integrado de Android Studio y el Gradle wrapper del proyecto.
4. Instalar Android SDK Platform 37 y Build Tools 36.0.0 si se solicitan. Esperar la sincronización; la primera vez necesita internet.
5. Seleccionar un emulador o teléfono Android 8.0/API 26 o superior y ejecutar `app`.

También se puede instalar `Captura-Duoc-EV2.apk`. Es un APK debug de revisión, con identificador `cl.duoc.therionlabs.preview` y nombre **Captura Duoc · Revisión**. Se instala por separado de la versión anterior y tiene sus propios datos. El proyecto incluye migraciones hasta la base versión 4 para conservar los datos al actualizar con el mismo identificador y firma. Si Android rechaza el APK por una firma distinta, ejecuta el código en Android Studio con la misma instalación que usaste antes; no desinstales si quieres conservar sus datos.

## Cuentas locales de prueba

| Usuario | Contraseña | Perfil |
|---|---|---|
| captador | Captador2026! | Captador 1 |
| captador2 | Captador2026! | Captador 2 |
| administrativo | Admin2026! | Administrativo |

Son cuentas de demostración inicializadas en el dispositivo. Sus contraseñas se almacenan mediante PBKDF2 con salt. La sesión termina al cerrar el proceso; no constituye autenticación de servidor. Utilizar únicamente datos ficticios para las pruebas.

## Recorrido

El administrativo crea primero un evento con nombre, institución, fecha, comuna y descripción. Después cierra sesión y el captador elige ese evento, completa los datos personales, intereses y recursos opcionales, guarda la ficha y consulta sus propias capturas. El administrativo consulta los eventos y sus prospectos en el mismo dispositivo, abre el marcador o una aplicación de correo y registra manualmente cada gestión. El historial conserva responsable, fecha, canal, estado y observación.

El catálogo incluye 81 nombres de carreras consultados en páginas oficiales de Duoc UC, organizados en nueve escuelas, con buscador y hasta tres intereses. «Otra / no la encuentro» exige escribir la preferencia y queda por confirmar. Ver `docs/CATALOGO.md`: la oferta por sede, jornada y modalidad requiere confirmación.

## Alcance de esta entrega

La información de ambas cuentas se comparte únicamente dentro de esta instalación. No hay API, sincronización entre teléfonos, envío al sistema central ni recuperación de contraseñas. Corresponden a EV3. Por eso una ficha conserva su estado de envío pendiente aunque el administrativo registre que ya contactó al prospecto: son estados distintos.

Una instalación nueva comienza sin eventos: administración crea el primero. No se precargan actividades ficticias. Al actualizar, se retiran del listado los tres eventos de demostración de versiones anteriores (DEM-01, DEM-02 y DEM-03). Los eventos creados por administración se conservan. Las fichas anteriores no se borran. Administración puede eliminar un evento únicamente si todavía no tiene prospectos; se solicita confirmación y la restricción también se aplica en la base de datos. No hay edición de eventos, corrección de fichas ni bajas de prospectos en este alcance. Cámara y ubicación son opcionales; la aplicación solicita sus permisos durante el uso. Abrir el marcador o el correo no realiza una llamada ni envía un mensaje automáticamente.

## Documentación

- `DATOS-DE-PRUEBA.md`: recorrido para el equipo.
- `GUIA-CODIGO.md`: estructura y explicación de las clases.
- `VERIFICACION.md`: resultados y controles pendientes.
- `docs/CATALOGO.md`: procedencia y límites del catálogo académico.

## Validaciones de esta revisión

Se impide un RUT repetido dentro del mismo evento, normalizando puntos, guion, espacios y letra K. El mismo RUT sí puede participar en otro evento. Se mantiene el control de correo repetido por evento. Comprobación e inserción se realizan en una transacción de Room.

Las gestiones exigen resultado y observación; responsable, fecha y hora se guardan automáticamente. Los botones de guardado se bloquean mientras se procesa. Al abandonar una captura, un evento nuevo o una observación de gestión sin guardar, se pide confirmación. Volver del paso 2 al paso 1 conserva el borrador.

## Entrega sencilla

El proyecto se entrega sin carpetas de pruebas unitarias ni instrumentadas. Las validaciones de formularios, roles, duplicados y persistencia permanecen dentro de la aplicación.

«Eliminar evento» aparece solo al administrativo y solo en eventos vacíos. Si se registra un prospecto antes de confirmar, la operación se rechaza y no elimina sus datos. El captador no puede eliminar eventos.
