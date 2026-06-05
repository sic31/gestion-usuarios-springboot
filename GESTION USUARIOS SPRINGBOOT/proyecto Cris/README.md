# Gestion Usuarios Spring Boot

Aplicación web full-stack para autenticación segura, gestión de usuarios y control de acceso basado en roles utilizando Spring Boot 3.5.14, PostgreSQL y tecnologías web modernas.

## 📋 Características

- ✅ Autenticación JWT (JSON Web Token)
- ✅ Gestión de usuarios con roles
- ✅ Control de acceso basado en roles (RBAC)
- ✅ Validación de entrada con anotaciones
- ✅ Manejo global de excepciones
- ✅ Base de datos PostgreSQL
- ✅ API REST documentada con Swagger/OpenAPI
- ✅ Uso de DTOs para separar capas
- ✅ Seguridad con Spring Security
- ✅ Contraseñas encriptadas con BCrypt

## 🛠️ Tecnologías

- **Java 21**
- **Spring Boot 3.5.14**
- **Spring Security**
- **Spring Data JPA**
- **PostgreSQL**
- **JWT (jjwt 0.12.6)**
- **Lombok**
- **Maven**
- **Swagger/OpenAPI**

## 📦 Estructura del Proyecto

```
src/main/java/com/gestionusuarios/gestionusuarios/
├── controller/          # Controladores REST
├── model/              # Entidades JPA
├── service/            # Lógica de negocio
├── repository/         # Acceso a datos
├── security/           # Configuración de seguridad y JWT
├── dto/                # Data Transfer Objects
├── exception/          # Excepciones personalizadas
└── GestionusuariosApplication.java
```

## 🚀 Configuración e Instalación

### Requisitos Previos

- Java 21 JDK instalado
- PostgreSQL 12 o superior
- Maven 3.6 o superior
- Git

### Pasos de Instalación

1. **Clonar el repositorio**

```bash
git clone https://github.com/tu-usuario/gestion-usuarios-springboot.git
cd gestion-usuarios-springboot
```

2. **Configurar variables de entorno**

Crear un archivo `.env` basado en `.env.example`:

```bash
cp .env.example .env
```

Editar `.env` con tus valores reales:

```env
DATABASE_URL=jdbc:postgresql://localhost:5432/gestionusuarios?sslmode=disable
DATABASE_USER=postgres
DATABASE_PASSWORD=tu_contraseña
JWT_SECRET=tu-clave-super-segura-minimo-32-caracteres
JWT_EXPIRATION=86400000
SPRING_PROFILES_ACTIVE=local
```

3. **Crear la base de datos**

```sql
CREATE DATABASE gestionusuarios;
\c gestionusuarios
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO roles (nombre) VALUES ('usuario');
INSERT INTO roles (nombre) VALUES ('admin');
```

4. **Compilar y ejecutar**

```bash
# Compilar
mvn clean package

# Ejecutar con perfil local
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
```

O ejecutar directamente:

```bash
java -Dspring.profiles.active=local -jar target/gestionusuarios-0.0.1-SNAPSHOT.jar
```

## 📡 Endpoints de la API

### Autenticación

#### Registro
```http
POST /api/auth/registro
Content-Type: application/json

{
  "nombre": "Juan Pérez",
  "correo": "juan@example.com",
  "password": "password123"
}
```

**Respuesta (201 Created):**
```json
{
  "id": 1,
  "nombre": "Juan Pérez",
  "correo": "juan@example.com",
  "rol": "usuario",
  "estado": "activo",
  "fechaRegistro": "2026-06-01T10:47:52",
  "ultimoAcceso": "2026-06-01T10:47:52"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "correo": "juan@example.com",
  "password": "password123"
}
```

**Respuesta (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "rol": "usuario",
  "mensaje": "Autenticación exitosa"
}
```

### Usuarios

#### Ver perfil actual
```http
GET /api/usuarios/me
Authorization: Bearer {token}
```

#### Actualizar perfil
```http
PUT /api/usuarios/me
Authorization: Bearer {token}
Content-Type: application/json

{
  "nombre": "Juan Carlos Pérez",
  "correo": "juancarlos@example.com"
}
```

#### Listar todos los usuarios (ADMIN)
```http
GET /api/admin/usuarios
Authorization: Bearer {token}
```

#### Eliminar usuario (ADMIN)
```http
DELETE /api/admin/usuarios/{id}
Authorization: Bearer {token}
```

## 🔐 Manejo de Errores

La API devuelve respuestas de error estructuradas:

```json
{
  "timestamp": "2026-06-01T10:47:52",
  "status": 400,
  "error": "Validación Fallida",
  "errores": {
    "correo": "El correo debe ser válido",
    "password": "La contraseña debe tener al menos 6 caracteres"
  },
  "path": "/api/auth/login"
}
```

### Códigos de Error Comunes

- **400 Bad Request** - Validación fallida
- **401 Unauthorized** - Credenciales inválidas o token expirado
- **404 Not Found** - Usuario o recurso no encontrado
- **409 Conflict** - El usuario ya existe
- **500 Internal Server Error** - Error del servidor

## 🔒 Seguridad

### Autenticación y Autorización

- JWT Token con expiración configurable
- Spring Security integrado
- Validación de roles en endpoints protegidos
- Contraseñas encriptadas con BCrypt

### Variables de Entorno Sensibles

Las credenciales de base de datos y JWT se deben configurar mediante variables de entorno, nunca en el código fuente.

```properties
# application.properties
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USER}
spring.datasource.password=${DATABASE_PASSWORD}
jwt.secret=${JWT_SECRET}
```

## 📚 Documentación Swagger

Accede a la documentación interactiva de la API en:

```
http://localhost:8080/swagger-ui.html
```

## 📝 Licencia

Este proyecto está bajo la licencia MIT.

## 👨‍💻 Autor

Proyecto desarrollado como parte de técnicas de programación avanzada.

