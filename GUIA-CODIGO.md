# Guía del código

## Estructura

`domain` contiene los datos del formulario, validaciones, catálogo, roles y reglas de seguimiento. No depende de las pantallas.

`data/local` contiene las entidades y consultas de Room. Una ficha conserva su evento y captador; cada seguimiento referencia una ficha y un administrativo. La base versión 4 incluye migraciones de versiones 1, 2 y 3 sin borrado destructivo. Las fichas antiguas sin captador asignado quedan disponibles para administración; no se adjudican a una persona nueva.

`data/repository` realiza las operaciones de almacenamiento. OperacionRepository inicializa las cuentas locales, crea eventos únicamente por solicitud del administrativo, verifica contraseñas y valida el rol guardado antes de registrar gestiones.

`data/viewmodel` mantiene el estado de pantalla y coordina repositorios. AuthViewModel controla acceso y sesión; ProspectoViewModel controla borrador, validación, registros visibles y seguimiento. StateFlow comunica los cambios a Compose.

`ui/screens` contiene pantallas separadas por responsabilidad. `ui/components` contiene controles reutilizables y captura nativa. `native` abre cámara, ubicación, marcador y correo. `MainActivity` organiza navegación y accesos según el perfil.

## Flujo para explicar

1. El login busca la cuenta local y compara su contraseña con PBKDF2; el rol procede de la cuenta guardada.
2. El administrativo crea un evento mediante EventDraft y EventRules. Después el captador selecciona ese evento y completa CaptureDraft. Las validaciones muestran errores antes de insertar.
3. El repositorio guarda ProspectoEntity. Room emite una nueva lista; el ViewModel filtra por captador.
4. El administrativo consulta la ficha y registra SeguimientoEntity. Cada gestión se agrega al historial, conservando las anteriores.
5. El último seguimiento define el estado de contacto mostrado. No cambia el estado de envío al servidor.

## Carreras

Catalogs contiene nombres oficiales agrupados por escuela. El buscador ignora mayúsculas y tildes. «Otra» ocupa un lugar dentro del máximo de tres y exige entre 3 y 120 caracteres. Si el texto coincide con una carrera del catálogo, se solicita elegirla de la lista. La ficha guarda el texto por separado para no incorporarlo automáticamente a la oferta oficial.

## Próxima evaluación

Sustituir el acceso local por autenticación de servidor y conectar eventos, prospectos y gestiones a una API. La autorización también debe validarse en el servidor. Definir sincronización, conflictos, estados de envío y manejo de errores antes de permitir uso con datos reales.

El código se entrega integrado para revisión del equipo. Cualquier distribución posterior debe reflejar lo que cada integrante revise, implemente o pruebe realmente.

## Control de duplicados

ProspectoRepository consulta RUT normalizado y correo dentro del evento antes de insertar, en una misma transacción. La consulta de RUT también normaliza fichas antiguas. El índice único de evento/correo se conserva. Un duplicado produce un mensaje y conserva el formulario para corregirlo.

Crear eventos también valida el rol guardado en la base y rechaza el mismo nombre, institución y fecha. La migración 3 a 4 incorpora la comuna sin eliminar los datos anteriores. Los tres identificadores de demostración se excluyen del catálogo; los eventos creados por administración conservan sus datos.

## Eliminación de eventos vacíos

OperacionRepository comprueba el rol guardado y utiliza una transacción de Room. La consulta DELETE incluye NOT EXISTS para impedir eliminar un evento asociado a cualquier prospecto, incluso de otro captador. ProspectoRepository vuelve a comprobar la existencia del evento dentro de su transacción de guardado para evitar registros huérfanos si otro proceso intenta eliminarlo.
