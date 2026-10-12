# CHN Tarjetas de Crédito

Sistema web para la gestión de clientes, solicitudes de tarjetas de crédito, tarjetas emitidas, consumos, pagos y movimientos.

## Tecnologías utilizadas

### Backend
- Java y Spring Boot.
- Spring Web para la API REST.
- Spring Data JPA para el acceso a datos.
- Flyway para las migraciones de base de datos.
- Maven para la compilación y ejecución de pruebas.

### Frontend
- Angular.
- TypeScript.
- Angular Material.

### Base de datos
- Microsoft SQL Server.
- Docker Compose para ejecutar el contenedor de base de datos.

## Funcionalidades

### Clientes
- Consultar el listado de clientes.
- Consultar información de clientes mediante la API.
- Registrar clientes.
- Actualizar clientes existentes.

### Solicitudes de tarjetas
- Consultar las solicitudes registradas.
- Crear solicitudes de tarjetas de crédito.
- Consultar una solicitud por su identificador.
- Aprobar solicitudes.
- Rechazar solicitudes.
- Registrar comentarios de decisión y, cuando corresponda, el monto aprobado.

### Tarjetas de crédito
- Consultar las tarjetas emitidas.
- Consultar el detalle de una tarjeta.
- Consultar el historial de movimientos.
- Registrar consumos.
- Registrar pagos.
- Actualizar y consultar los saldos a través de las operaciones disponibles en la API.

## Requisitos previos

Instalar o disponer de las siguientes herramientas:

- Java, compatible con la versión configurada en el proyecto.
- Docker Desktop con Docker Compose.
- Node.js y npm, compatibles con la versión de Angular utilizada.
- Acceso a la base de datos SQL Server.
- PowerShell en Windows.

## Estructura del proyecto

```text
chn-tarjetas/
├── backend/
│   ├── src/
│   ├── mvnw.cmd
│   └── pom.xml
├── frontend/
│   ├── src/
│   ├── package.json
│   └── angular.json
├── .env
└── README.md
```

La estructura anterior representa los directorios principales del proyecto; pueden existir archivos adicionales.

## Configuración de la base de datos

El proyecto utiliza SQL Server y configura la conexión del backend mediante variables de entorno.

La configuración de Spring Boot contempla estas variables:

| Variable | Descripción |
|---|---|
| `DB_URL` | URL JDBC de conexión a SQL Server. |
| `DB_USER` | Usuario de la base de datos. |
| `DB_PASSWORD` | Contraseña del usuario de la base de datos. |

La URL predeterminada configurada para el backend es:

```text
jdbc:sqlserver://localhost:1433;databaseName=chn_cards;encrypt=true;trustServerCertificate=true
```

El backend tiene habilitadas las migraciones de Flyway y la validación del esquema mediante Hibernate.

**Importante:** no elimines el volumen persistente de SQL Server para resolver problemas de conexión, ya que puede contener los datos existentes. La contraseña configurada actualmente en el contenedor debe coincidir con la utilizada por el backend.

## Cómo ejecutar el proyecto

Abre una terminal de PowerShell.

### 1. Iniciar SQL Server

Desde la raíz del proyecto:

```powershell
cd D:\proyectos\chn-tarjetas
docker compose up -d db
```

Comprueba que el servicio esté en ejecución:

```powershell
docker compose ps
```

Espera a que SQL Server esté listo antes de iniciar el backend.

### 2. Configurar las variables de conexión

El siguiente comando lee `MSSQL_SA_PASSWORD` desde el archivo `.env` y la asigna a las variables que consume Spring Boot:

```powershell
cd D:\proyectos\chn-tarjetas

Get-Content .env | ForEach-Object {
    if ($_ -match '^\s*MSSQL_SA_PASSWORD\s*=(.*)$') {
        $env:DB_USER = 'sa'
        $env:DB_PASSWORD = $Matches[1].Trim().Trim('"').Trim("'")
    }
}
```

La contraseña debe corresponder a la contraseña efectiva del usuario `sa` del contenedor existente.

Si la base de datos utiliza una contraseña distinta a la de `.env`, se debe configurar `DB_PASSWORD` con la contraseña correcta, sin modificar ni recrear el volumen.

### 3. Ejecutar el backend

En la misma terminal, después de configurar las variables:

```powershell
cd D:\proyectos\chn-tarjetas\backend
.\mvnw.cmd spring-boot:run
```

El backend queda disponible en:

```text
http://localhost:8080
```

Mantén esta terminal abierta mientras utilizas la aplicación.

### 4. Ejecutar el frontend

Abre otra terminal de PowerShell:

```powershell
cd D:\proyectos\chn-tarjetas\frontend
npx ng serve
```

Abre en el navegador la dirección que indique Angular; normalmente es:

```text
http://localhost:4200
```

## Documentación de la API

Con el backend en ejecución, la documentación interactiva de Swagger está disponible en:

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

Desde Swagger se pueden consultar los parámetros, los cuerpos de las solicitudes, las respuestas y los códigos HTTP de los endpoints.

## Endpoints principales

Los siguientes endpoints corresponden a las operaciones implementadas en el proyecto.

### Clientes

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/clientes` | Consultar los clientes. |
| `GET` | `/api/clientes/{id}` | Consultar un cliente por ID. |
| `POST` | `/api/clientes` | Registrar un cliente. |
| `PUT` | `/api/clientes/{id}` | Actualizar un cliente. |

### Solicitudes

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/solicitudes` | Consultar las solicitudes. |
| `GET` | `/api/solicitudes/{id}` | Consultar una solicitud por ID. |
| `POST` | `/api/solicitudes` | Crear una solicitud. |
| `PUT` | `/api/solicitudes/{id}/aprobar` | Aprobar una solicitud. |
| `PUT` | `/api/solicitudes/{id}/rechazar` | Rechazar una solicitud. |

### Tarjetas y movimientos

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/tarjetas` | Consultar las tarjetas. |
| `GET` | `/api/tarjetas/{id}` | Consultar una tarjeta por ID. |
| `GET` | `/api/tarjetas/{tarjetaId}/movimientos` | Consultar los movimientos de una tarjeta. |
| `POST` | `/api/tarjetas/{tarjetaId}/consumos` | Registrar un consumo. |
| `POST` | `/api/tarjetas/{tarjetaId}/pagos` | Registrar un pago. |

Para conocer los campos obligatorios y los formatos exactos de cada solicitud, consulta los esquemas publicados en Swagger.

## Compilación y pruebas

### Backend

Desde la carpeta `backend`, con SQL Server disponible y las variables de conexión configuradas:

```powershell
.\mvnw.cmd clean test
```

Este comando limpia los resultados de compilaciones anteriores, compila el proyecto y ejecuta las pruebas automatizadas.

Algunas pruebas pueden requerir una conexión funcional a SQL Server. Si ocurre un error de autenticación, verifica `DB_USER`, `DB_PASSWORD` y el estado del contenedor.

### Frontend

Desde la carpeta `frontend`:

```powershell
npx ng build
```

El resultado de compilación se genera normalmente en:

```text
frontend/dist/frontend
```

## Validaciones de negocio

La API contempla, entre otras, validaciones para evitar:

- Registrar un pago superior al saldo pendiente.
- Registrar un consumo que supere el crédito disponible.

Cuando una operación no cumple las reglas de negocio, la API puede responder con un error HTTP `422` y un detalle explicativo.

## Consideraciones

- SQL Server debe estar disponible antes de iniciar el backend.
- Las credenciales de conexión deben configurarse mediante variables de entorno.
- El frontend y el backend se ejecutan en terminales independientes durante el desarrollo.
- Swagger es el punto de referencia para los contratos detallados de la API.
- No se deben eliminar volúmenes persistentes de Docker como método para solucionar errores de autenticación.
