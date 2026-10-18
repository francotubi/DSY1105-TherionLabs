# Recorrido de revisión del equipo

Usar datos ficticios. Solo las cuentas locales se crean al iniciar la aplicación.

Primero ingresar con `administrativo` / `Admin2026!` y pulsar «Crear evento». Usar «Visita de orientación», institución «Colegio de prueba A», fecha «20/10/2026», comuna «Santiago» y descripción «Orientación de carreras». Guardar y comprobar que aparece. Cerrar sesión.

1. Ingresar con `captador` y `Captador2026!`.
2. Abrir «Visita de orientación», del Colegio de prueba A. Iniciar una captura.
3. Completar una persona ficticia: RUT `12.345.678-5`, fecha de nacimiento `01/01/2005`, teléfono `+56912345678` y correo `persona@example.cl`. Completar los demás campos obligatorios con datos de prueba.
4. Elegir región, sede y hasta tres intereses. Buscar «Informática». Probar también «Otra / no la encuentro»: primero sin texto para comprobar el error, luego con «Área por definir». Quitar esa opción y comprobar que se borra su texto.
5. Probar cámara y ubicación si el dispositivo los permite; rechazar permisos también debe permitir continuar con los campos obligatorios.
6. Guardar y revisar ficha. Volver a Mis capturas, buscarla y comprobar que permanece al cerrar y volver a abrir la app.
7. Cerrar sesión e ingresar con `captador2` y `Captador2026!`. No debe ver la captura del primer captador.
8. Cerrar sesión e ingresar con `administrativo` y `Admin2026!`. Abrir el evento y la ficha registrada.
9. Verificar que teléfono y correo abren sus aplicaciones correspondientes. No enviar mensajes reales ni realizar llamadas durante la prueba.
10. Registrar una gestión con estado «En seguimiento», canal «Correo» y observación «Se preparará información de carreras». Revisar el historial.
11. Registrar una segunda gestión «Contactado» con una observación distinta. Ambas deben conservarse; el estado de envío de la ficha continúa pendiente.
12. Revisar «Gestiones», cerrar la app y confirmar que se conservan fichas y seguimientos.

La aplicación no comparte datos entre dispositivos en EV2. Para esta revisión, captador y administrativo deben usar la misma instalación.

## Comprobaciones adicionales

- Intentar guardar un evento con campos vacíos y una fecha inexistente: mostrar errores. Repetir el mismo nombre/institución/fecha: impedir el duplicado.
- En el mismo evento, repetir el RUT de una ficha guardada usando un correo distinto y cambiando su formato: impedirlo y conservar el borrador.
- Crear un segundo evento y registrar ese RUT allí: permitirlo.
- Dejar una captura, un evento nuevo o una observación sin guardar y navegar a otra pestaña: mostrar confirmación. Cancelar debe conservar los datos.
- Pulsar guardar repetidamente: el botón se bloquea durante la operación.
- Cerrar y abrir el proceso: eventos, prospectos y gestiones guardados permanecen; se debe iniciar sesión nuevamente.

## Eventos antiguos y eliminación

Los tres eventos de demostración de las versiones anteriores ya no aparecen en el catálogo. No se eliminan automáticamente sus fichas guardadas. Para comprobar eliminación, crear un evento nuevo sin prospectos y usar «Eliminar evento» desde administración. Después de registrar un prospecto, esa acción ya no estará disponible.
