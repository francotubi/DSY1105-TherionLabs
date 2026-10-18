# Verificación de la entrega

Esta entrega no contiene pruebas unitarias ni instrumentadas.

Antes de entregar se comprueban la compilación del APK, su firma, la estructura del ZIP y las consultas de almacenamiento. No se presenta como una ejecución visual en un dispositivo real.

## Revisión en Android Studio o teléfono

1. Iniciar como administrativo. Los eventos de demostración anteriores no deben aparecer.
2. Crear un evento vacío y eliminarlo. Cancelar primero el diálogo para comprobar que se conserva, luego confirmar y comprobar que desaparece.
3. Crear otro evento, registrar un prospecto como captador y volver al administrativo. El evento debe mostrar sus prospectos y no debe ofrecer eliminación.
4. Comprobar que el captador no tiene acciones de creación o eliminación.
5. Repetir un RUT dentro del mismo evento, con puntos/guion diferentes y otro correo: debe impedirlo. En un evento diferente debe permitirlo.
6. Registrar una gestión con resultado y observación y comprobar responsable y fecha automáticos.
7. Comprobar conservación de datos tras cerrar el proceso, avisos de salida y bloqueo de guardado.
8. Probar cámara y ubicación con permisos aceptados y rechazados.

La revisión visual y las funcionalidades nativas deben comprobarse en el emulador o teléfono del equipo. El backend y la sincronización entre dispositivos corresponden a EV3.
