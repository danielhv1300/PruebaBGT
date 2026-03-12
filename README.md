
# BTG PACTUAL - SISTEMA DE GESTIÓN DE FONDOS (BACKGROUNDS)


Este proyecto es una API REST robusta desarrollada en Java 22 con Spring
Boot 4, diseñada para gestionar la suscripción y cancelación de fondos de
inversión. Incluye seguridad avanzada con JWT, persistencia en MongoDB y
una arquitectura orientada a la calidad.

--------------------------------------------------------------------------
1. TECNOLOGÍAS Y CUALIDADES
--------------------------------------------------------------------------
* Core: Java 22, Spring Boot 4.
* Seguridad: Spring Security + JSON Web Token (JWT).
* Persistencia: MongoDB (NoSQL) con Spring Data MongoDB.
* Validación: Bean Validation (@Valid) en DTOs.
* Notificaciones: Patrón Strategy para envíos vía SMS y EMAIL.
* Calidad: Pruebas unitarias con JUnit 5, Mockito y Datafaker.
* Despliegue: Preparado para AWS CloudFormation mediante variables de entorno.

--------------------------------------------------------------------------
2. SEGURIDAD Y AUTENTICACIÓN
--------------------------------------------------------------------------
La API utiliza seguridad basada en Stateless JWT.
* Aislamiento de Usuarios: El sistema valida que el userId de la petición
  coincida con el usuario autenticado en el token, impidiendo que un usuario
  acceda a datos ajenos (403 Forbidden).

--------------------------------------------------------------------------
3. DOCUMENTACIÓN DE ENDPOINTS (EJEMPLOS JSON)
--------------------------------------------------------------------------

A. REGISTRO DE USUARIO
POST /api/users/register
JSON:
```bash
{
  "name": "Daniel Hernandez",
  "email": "daniel.hernandez@example.com",
  "password": "SecurePassword123!",
  "phoneNumber": "+573001234567"
}
```


B. LOGIN (OBTENCIÓN DE TOKEN)
POST /api/auth/login
JSON:
```bash
{
  "email": "daniel.hernandez@example.com",
  "password": "SecurePassword123!"
}
```

C. SUSCRIBIRSE A UN FONDO (Requiere Token)
POST /api/funds/subscribe
JSON:
```bash
{
  "userId": "65f123abc456def789",
  "fundId": "65f789ghi012jkl345",
  "notificationType": "EMAIL"
}
```
(Valores permitidos para notificationType: "SMS" o "EMAIL")

D. CANCELAR SUSCRIPCIÓN (Requiere Token)
POST /api/funds/cancel/{userId}/{transactionId}

E. HISTORIAL DE TRANSACCIONES (Requiere Token)
GET /api/funds/history/{userId}

--------------------------------------------------------------------------
4. DESPLIEGUE EN AWS (CLOUDFORMATION)
--------------------------------------------------------------------------
La aplicación utiliza configuración dinámica mediante variables de entorno:

* MONGODB_URI: URI de conexión (ej: mongodb://user:pass@host:27017/db)
* JWT_SECRET: Firma secreta para los tokens.
* JWT_EXPIRATION_MS: Tiempo de expiración (default 3600000ms).

--------------------------------------------------------------------------
5. EJECUCIÓN DE PRUEBAS
--------------------------------------------------------------------------
Se implementó el patrón Test Data Builder para asegurar la robustez.

Comando para ejecutar:

```bash
./gradlew test
```

Escenarios cubiertos:
- Registro y login.
- Suscripción con validación de saldo.
- Control de suscripciones duplicadas.
- Resiliencia en fallos de notificación.
