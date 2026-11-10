# Autenticación JWT con Spring Boot

API REST de autenticación y registro de usuarios con Java 17, Spring Boot 4.1.2, JPA, H2 y JSON Web Tokens (JWT).

## Para no técnicos: ¿qué es un JWT?

Un JWT es como una pulsera de un evento VIP. Primero verificas tu identidad en la entrada y recibes una pulsera; después puedes mostrarla para acceder a las zonas permitidas sin volver a presentar tu identificación cada vez. En esta API, el servidor entrega esa “pulsera digital” al iniciar sesión y los endpoints protegidos la verifican antes de atender cada petición.

El token firmado permite comprobar que fue emitido por la API y que no ha sido alterado. No debe compartirse con otras personas ni incluirse en lugares públicos.

## Explicación técnica: flujo de autenticación

1. El cliente envía nombre de usuario y contraseña a `POST /auth/login`.
2. El servidor busca al usuario y verifica la contraseña almacenada usando BCrypt.
3. Si las credenciales son correctas, `JwtService` genera y firma un JWT con el ID del usuario, su nombre de usuario y el ID del rol.
4. El cliente envía el token en el encabezado `Authorization: Bearer <token>` al solicitar una ruta protegida.
5. `JwtValidationFilter` valida la firma y vigencia del token, extrae los claims (`userId`, `rolId` y el `username` del subject) y los agrega a la solicitud.
6. El filtro pasa la solicitud al controlador. Si falta el token o no es válido, devuelve `401 Unauthorized` sin ejecutar el endpoint protegido.

Las rutas de autenticación bajo `/auth/` se excluyen del filtro para permitir el registro, el inicio de sesión y la renovación de tokens.

## Decisiones de arquitectura

- **Arquitectura por capas:** los controladores gestionan HTTP; los servicios contienen la lógica de negocio; los repositorios acceden a datos; las entidades representan datos persistidos; y los DTOs definen los contratos de entrada/salida. Esta separación facilita las pruebas y permite modificar una responsabilidad sin mezclarla con las demás.
- **H2 en memoria:** ofrece una base de datos rápida y sencilla para desarrollo y pruebas, sin instalar ni administrar un servidor de base de datos. Los datos se reinician al detener la aplicación.
- **DTOs de respuesta:** permiten controlar qué información sale de la API. Por ejemplo, `UserDTO` no incluye la contraseña ni su hash.
- **BCrypt:** almacena contraseñas mediante hashes con salt; no se guardan contraseñas en texto plano.

## Tecnologías

- Java 17
- Spring Boot 4.1.2
- Maven y Spring Data JPA
- H2 en memoria
- JWT con JJWT 0.12.6
- Lombok
- `spring-security-crypto` con BCrypt

## Requisitos y ejecución

1. Instala Java 17.
2. Desde la raíz del proyecto, inicia la API:

```bash
./mvnw spring-boot:run
```

La API estará disponible en `http://localhost:8080`.

Los roles `ADMIN` (ID 1) y `USER` (ID 2) se crean al iniciar la aplicación si la tabla de roles está vacía.

## Manual de usuario: Thunder Client

En Thunder Client (o Postman), crea una petición, selecciona el método indicado, escribe la URL y configura el cuerpo como JSON cuando corresponda.

### 1. Registrar usuario

**POST** `http://localhost:8080/auth/register`

En **Body → JSON**, envía:

```json
{
  "username": "ana",
  "password": "mi-contrasena",
  "email": "ana@example.com",
  "rolId": 2
}
```

Una creación exitosa responde `201 Created`:

```json
{
  "message": "usuario registrado correctamente",
  "data": {
    "id": 1,
    "username": "ana",
    "email": "ana@example.com",
    "rolId": 2
  }
}
```

La respuesta no contiene la contraseña. Si el usuario o correo ya existe o el rol no existe, la API devuelve un mensaje explicativo y `data: null`.

### 2. Iniciar sesión

**POST** `http://localhost:8080/auth/login`

En **Body → JSON**, usa las credenciales del usuario registrado:

```json
{
  "username": "ana",
  "password": "mi-contrasena"
}
```

Con credenciales correctas, responde `200 OK`:

```json
{
  "message": "Login exitoso",
  "data": {
    "jwt": "<token-jwt>"
  }
}
```

Copia el valor de `data.jwt`; lo necesitarás en las siguientes peticiones. Credenciales incorrectas responden `401 Unauthorized`.

### 3. Renovar token

**POST** `http://localhost:8080/auth/refresh`

En **Headers**, agrega:

| Key | Value |
|---|---|
| `Authorization` | `Bearer <token-jwt>` |

Usa el token recibido en el inicio de sesión. No requiere cuerpo. Un token válido y vigente responde `200 OK` con el nuevo JWT. Los tokens expirados no se pueden renovar; la respuesta será `401 Unauthorized`.

### 4. Consultar perfil protegido

**GET** `http://localhost:8080/user/profile`

En **Headers**, agrega `Authorization` con el valor `Bearer <token-jwt>` (usa el token vigente). Una respuesta exitosa tiene este formato:

```json
{
  "message": "Perfil de ana",
  "data": {
    "userId": 1,
    "username": "ana"
  }
}
```

### 5. Probar errores 401

Para comprobar que las rutas protegidas requieren un JWT:

1. Envía `GET /user/profile` sin el encabezado `Authorization`: la respuesta será `401 Unauthorized`.
2. Repite con `Authorization: Bearer token-invalido`: la respuesta será `401 Unauthorized`.
3. También responde `401 Unauthorized` si el token expiró o el encabezado no tiene el formato Bearer esperado.

Las rutas `/auth/register`, `/auth/login` y `/auth/refresh` están excluidas del filtro de validación general. El endpoint de refresh realiza su propia validación y responde 401 ante un token inválido o expirado.

## Formato de respuesta

Los endpoints que retornan datos usan este formato:

```json
{
  "message": "Descripción del resultado",
  "data": {}
}
```

`data` puede ser `null` cuando la operación no tiene datos que devolver.

## Bitácora: problemas resueltos

1. **El filtro bloqueaba el registro inicial:** se excluyeron las rutas `/auth/` en `shouldNotFilter` para permitir registro e inicio de sesión sin token previo.
2. **H2 no tenía roles para asociar al registro:** `AppConfig` agrega los roles `ADMIN` y `USER` con un `CommandLineRunner` cuando la tabla está vacía.
3. **El refresh aceptaba tokens expirados:** `JwtService` ahora lanza una excepción para tokens expirados y no los renueva.
4. **Los errores internos podían confundirse con errores de JWT:** la llamada a `filterChain.doFilter` está fuera del `try-catch` de validación para que las excepciones de los controladores no sean tratadas como fallos del token.
