
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
* Despliegue: Preparado mediante variables de entorno.

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

--------------------------------------------------------------------------

# Parte 2 SQL

Para la parte 2 de la prueba tecnica se realizo la solicitud de generar una consulta de SQL basado en un 
esquema entregado en la documentacion de la prueba tecnica.

--------------------------------------------------------------------------
1. EJECUCION DE DOCKER-COMPOSE PARA PRUEBA CON POSTGRESQL
--------------------------------------------------------------------------

- En la carpeta punto_2_sql se encuentra el archivo docker-compose-sql.yml, el cual se puede ejecutar con el siguiente
comando:

```bash
  docker-compose -f docker-compose-sql.yml up -d
```
--------------------------------------------------------------------------
2. CONSULTA SQL SOLICITADA
--------------------------------------------------------------------------

- Al ejecutar el docker-compose se crea la base de datos BGT y adicional el ejecuta el script de inicializacion de 
datos, init.sql en donde se encuentra las tablas del esquema indicado para la prueba y la incersion de datos de prueba
para la consulta solicitada.

- Requerimiento: Obtener los nombres de los clientes que tienen inscrito algún producto disponible solo en
  las sucursales que visitan.

```sql
SELECT DISTINCT c.nombre, c.apellidos
FROM Cliente c
JOIN Inscripcion i ON c.id = i.idCliente
JOIN Producto p ON i.idProducto = p.id
WHERE NOT EXISTS (
    SELECT 1
    FROM Disponibilidad d
    WHERE d.idProducto = p.id
    AND d.idSucursal NOT IN (
        SELECT v.idSucursal
        FROM Visitan v
        WHERE v.idCliente = c.id
    )
);
```
La consulta retornaria la informacion de la siguiente forma:

| nombre | apellidos |
|--------|-----------|
| Daniel | Hernandez |