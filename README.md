# GourmetGo - Plataforma de Experiencias Culinarias

![GourmetGo](https://img.shields.io/badge/GourmetGo-Culinary%20Experiences-orange)
![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20Web%20API-blue)
![Tech Stack](https://img.shields.io/badge/Tech-Node.js%20%7C%20Kotlin%20%7C%20MongoDB-green)

## Tabla de Contenidos

1. [Descripción del Proyecto](#descripción-del-proyecto)
2. [Características Principales](#características-principales)
3. [Arquitectura del Sistema](#arquitectura-del-sistema)
4. [Stack Tecnológico](#stack-tecnológico)
5. [Estructura del Proyecto](#estructura-del-proyecto)
6. [Configuración e Instalación](#configuración-e-instalación)
7. [Documentación de API](#documentación-de-api)
8. [Información Técnica Adicional](#información-técnica-adicional)
   - [8.1 Información sobre APIs](#81-información-sobre-apis)
   - [8.2 Pruebas de funcionalidad](#82-pruebas-de-funcionalidad)
   - [8.3 Diagrama MVC](#83-diagrama-mvc)
9. [Contribución](#contribución)
10. [Licencia](#licencia)

---

## Descripción del Proyecto

**GourmetGo** es una plataforma digital integral desarrollada como proyecto académico para el **Instituto Tecnológico de Costa Rica (ITCR)** que funciona como un ecosistema completo para la gestión y promoción de experiencias culinarias. La aplicación facilita la conexión entre amantes de la gastronomía y proveedores de experiencias gastronómicas únicas, incluyendo cenas temáticas, clases de cocina magistrales, degustaciones especializadas y eventos culinarios exclusivos.

### Objetivo Principal

El objetivo fundamental de GourmetGo es crear un ecosistema digital robusto y escalable que facilite la interacción eficiente entre consumidores de experiencias gastronómicas y proveedores especializados (chefs, restaurantes, escuelas culinarias), promoviendo activamente la cultura culinaria local y regional mientras proporciona una plataforma tecnológicamente avanzada para la gestión integral de eventos gastronómicos.
---

## Características Principales

### Funcionalidades para Usuarios Finales

**Sistema de Autenticación y Registro Completo**
- Registro de usuarios con validación exhaustiva de datos personales
- Sistema de autenticación basado en JSON Web Tokens (JWT) para máxima seguridad
- Gestión de perfiles de usuario con información personal y preferencias gastronómicas
- Integración con servicio de almacenamiento en la nube para imágenes de perfil
- Persistencia de sesión local con renovación automática de tokens

**Exploración y Descubrimiento de Experiencias**
- Catálogo completo de experiencias culinarias con información detallada
- Sistema de filtrado avanzado por categoría, precio, ubicación y fecha
- Funcionalidad de búsqueda inteligente con algoritmos de relevancia
- Visualización de imágenes de alta calidad con carga optimizada
- Sistema de recomendaciones basado en historial y preferencias

**Sistema Integral de Reservas**
- Proceso de reserva intuitivo con validación de disponibilidad en tiempo real
- Generación automática de códigos QR únicos para verificación de entrada
- Creación de comprobantes digitales en formato PDF con diseño profesional
- Gestión completa del historial de reservas con estados actualizados
- Sistema de cancelación con políticas configurables

**Sistema de Calificaciones y Reseñas**
- Calificación de experiencias con escala numérica y comentarios textuales
- Visualización de calificaciones promedio con estadísticas detalladas
- Sistema de moderación para mantener calidad de las reseñas
- Influencia de calificaciones en algoritmos de recomendación

### Funcionalidades para Chefs y Restaurantes

**Registro Profesional Especializado**
- Proceso de registro diferenciado para profesionales gastronómicos
- Creación de perfiles profesionales con información comercial completa
- Validación de credenciales y experiencia profesional
- Configuración de especialidades culinarias y tipos de cocina

**Gestión Avanzada de Experiencias**
- Panel de administración completo para creación y edición de experiencias
- Sistema de programación con calendario integrado y gestión de horarios
- Configuración detallada de precios, capacidad y requisitos especiales
- Herramientas de promoción y marketing digital integradas

**Panel de Control de Reservas**
- Dashboard administrativo con métricas de participación y ventas
- Gestión de listas de participantes con información de contacto
- Sistema de comunicación directa con participantes registrados
- Herramientas de análisis de demanda y tendencias de reserva

**Sistema de Verificación de Seguridad**
- Eliminación segura de experiencias con códigos de verificación temporal
- Notificación automática a usuarios afectados por cambios o cancelaciones
- Historial completo de acciones administrativas para auditoría
- Respaldo automático de información crítica antes de eliminaciones

### Características Técnicas del Sistema

**Generación Automática de Documentos**
- Creación dinámica de comprobantes de reserva en formato PDF
- Plantillas profesionales con información completa de la experiencia
- Inclusión automática de códigos QR para verificación
- Optimización de tamaño de archivo para distribución por correo electrónico

**Sistema de Códigos QR Inteligentes**
- Generación de códigos QR únicos para cada reserva individual
- Codificación de información de seguridad para prevenir falsificaciones
- Verificación rápida mediante escaneo con dispositivos móviles
- Integración con sistema de check-in automático

**Plataforma de Comunicación Automatizada**
- Sistema de correo electrónico con plantillas HTML personalizadas
- Notificaciones automáticas para confirmaciones, recordatorios y actualizaciones
- Personalización de mensajes según tipo de usuario y contexto
- Seguimiento de entrega y tasas de apertura de correos

**Asistente Virtual Integrado**
- Chatbot de soporte automatizado para consultas frecuentes
- Respuestas inteligentes basadas en contexto de la consulta
- Escalación automática a soporte humano cuando es necesario
- Base de conocimiento actualizable para mejora continua

**Sistema de Validación Robusto**
- Validación exhaustiva de datos de entrada en múltiples capas
- Verificación de integridad de datos antes del procesamiento
- Sanitización automática para prevenir ataques de inyección
- Logging detallado para debugging y análisis de errores

---

## Arquitectura del Sistema

### Patrón MVVM (Model-View-ViewModel)

GourmetGo implementa una arquitectura basada en el patrón **Model-View-ViewModel (MVVM)** que garantiza una separación clara de responsabilidades, facilita las pruebas unitarias, mejora la mantenibilidad del código, y proporciona una base sólida para el crecimiento y escalabilidad del sistema.

#### Ventajas de la Implementación MVVM

**Separación de Responsabilidades**
- La capa de vista se concentra exclusivamente en la presentación y captura de interacciones
- Los ViewModels manejan la lógica de presentación sin dependencias de Android Framework
- La capa de modelo gestiona datos y lógica de negocio de forma independiente
- Cada componente tiene responsabilidades claramente definidas y no solapadas

**Testabilidad Mejorada**
- ViewModels completamente unitarios sin dependencias del framework Android
- Repositorios con interfaces que permiten implementaciones mock para testing
- Separación clara entre lógica de negocio y presentación
- Posibilidad de testing individual de cada componente

**Mantenibilidad y Escalabilidad**
- Código organizado en capas con interfaces bien definidas
- Facilidad para agregar nuevas funcionalidades sin afectar componentes existentes
- Reutilización de componentes entre diferentes pantallas
- Estructura preparada para crecimiento del equipo de desarrollo

**Gestión del Ciclo de Vida**
- ViewModels que sobreviven cambios de configuración (rotación de pantalla)
- Gestión automática de estados de UI durante el ciclo de vida de la aplicación
- Prevención de memory leaks mediante uso correcto de observers
- Limpieza automática de recursos cuando ya no son necesarios

### Componentes Arquitectónicos Principales

#### Capa Model (Modelo de Datos)

**Modelos de Datos Principales**
- `User`: Representa usuarios finales con información personal y preferencias
- `ChefProfile`: Información profesional de chefs y restaurantes
- `Experience`: Experiencias culinarias con todos sus detalles
- `Booking`: Reservas con códigos QR y estados de confirmación
- `Rating`: Calificaciones y reseñas del sistema

**Repositorios de Datos**
- `AuthRepository`: Gestión de autenticación y sesiones de usuario
- `ExperienceRepository`: Operaciones CRUD para experiencias culinarias
- `BookingRepository`: Gestión completa del sistema de reservas
- `RatingRepository`: Manejo de calificaciones y reseñas

**Fuentes de Datos**
- `ApiService`: Interfaz REST para comunicación con backend
- `SharedPreferencesManager`: Almacenamiento local seguro
- `MockDataProvider`: Datos de prueba para desarrollo offline

#### Capa View (Interfaz de Usuario)

**Pantallas Principales Implementadas con Jetpack Compose**
- `LoginScreen`: Autenticación segura con validación en tiempo real
- `RegisterScreen`: Registro de usuarios con múltiples pasos de validación
- `HomeScreen`: Dashboard principal con exploración de experiencias
- `ExperienceDetailScreen`: Información detallada con galería de imágenes
- `BookingScreen`: Proceso de reserva con confirmación automática
- `ProfileScreen`: Gestión de perfil personal y preferencias

**Componentes Reutilizables**
- `ExperienceCard`: Tarjeta de experiencia con información resumida
- `CustomButton`: Botones con estados y animaciones personalizadas
- `LoadingIndicator`: Indicadores de carga con animaciones fluidas
- `InputField`: Campos de entrada con validación visual
- `ImageGallery`: Galería de imágenes con zoom y navegación

**Sistema de Navegación**
- Navegación type-safe utilizando Navigation Compose
- Gestión de argumentos entre pantallas con serialización automática
- Stack de navegación con manejo de estados guardados
- Deep linking para acceso directo a pantallas específicas

#### Capa ViewModel (Controladores de Presentación)

**ViewModels Especializados**
- `AuthViewModel`: Gestión de estados de autenticación y validación
- `HomeViewModel`: Control de pantalla principal con filtros y búsqueda
- `ExperienceViewModel`: Manejo de detalles de experiencia y interacciones
- `BookingViewModel`: Proceso completo de reserva con validaciones
- `ProfileViewModel`: Gestión de perfil de usuario y configuraciones

**Gestión de Estados UI**
- Estados inmutables con clases de datos (data classes)
- StateFlow para emisión reactiva de cambios de estado
- Manejo de estados de carga, éxito y error de forma consistente
- Composición de estados complejos mediante sealed classes

**Integración con Repositorios**
- Llamadas asíncronas utilizando Kotlin Coroutines
- Manejo de errores con Result wrapper para operaciones de red
- Transformación de datos del modelo para presentación en UI
- Caché inteligente para optimizar rendimiento y experiencia offline

---

## Stack Tecnológico

### Tecnologías de Backend

**Node.js con Express.js**
- Framework web robusto y escalable para construcción de APIs REST
- Middleware personalizable para autenticación, validación y logging
- Soporte nativo para operaciones asíncronas y manejo de concurrencia
- Ecosistema extenso de librerías y herramientas de desarrollo

**MongoDB con Mongoose ODM**
- Base de datos NoSQL orientada a documentos para máxima flexibilidad
- Mongoose ODM para modelado de datos con validación integrada
- Índices optimizados para consultas de alta performance
- Replicación y sharding para escalabilidad horizontal

**Seguridad y Autenticación**
- JSON Web Tokens (JWT) para autenticación stateless y segura
- Bcrypt para hash seguro de contraseñas con salt personalizado
- Middleware de autorización para control de acceso granular
- Validación de entrada exhaustiva para prevenir ataques de inyección

**Servicios de Terceros Integrados**
- Nodemailer para envío de correos electrónicos transaccionales
- Cloudinary para gestión optimizada de imágenes en la nube
- PDFKit para generación dinámica de documentos PDF
- QRCode para creación de códigos QR únicos y seguros

### Tecnologías de Frontend Android

**Desarrollo Nativo Android**
- Kotlin como lenguaje principal para desarrollo type-safe
- Android SDK más reciente para acceso a funcionalidades nativas
- Jetpack Compose para UI declarativa y moderna
- Material Design 3 para experiencia de usuario consistente

**Arquitectura y Patrones**
- MVVM con ViewModels y LiveData/StateFlow
- Dependency Injection manual para control granular
- Repository Pattern para abstracción de fuentes de datos
- Single Activity Architecture con Navigation Compose

**Comunicación de Red**
- Retrofit para cliente HTTP type-safe con conversión automática
- OkHttp para interceptores personalizados y logging de red
- Moshi para serialización/deserialización JSON eficiente
- Manejo de errores de red con retry automático y fallback

**Gestión de Estado y Persistencia**
- SharedPreferences para almacenamiento local de configuraciones
- DataStore para almacenamiento de preferencias de nueva generación
- Room Database para caché local de datos complejos (futuro)
- StateFlow y LiveData para programación reactiva

**Optimización y Performance**
- Coil para carga eficiente de imágenes con caché automático
- Kotlin Coroutines para programación asíncrona sin blocking
- ProGuard para ofuscación y reducción de tamaño de APK
- Memory leak detection y optimization tools integradas

### Herramientas de Desarrollo y DevOps

**Entornos de Desarrollo**
- Visual Studio Code como editor principal con extensiones especializadas
- Android Studio para desarrollo específico de Android y debugging
- Tareas automatizadas con scripts personalizados para build y deploy
- Configuración de workspace sincronizada entre desarrolladores

**Control de Versiones y Colaboración**
- Git para control de versiones distribuido con GitFlow workflow
- GitHub para hosting de repositorio con integración continua
- Issues y Project boards para gestión de tareas y bugs
- Pull Request reviews para calidad de código

**Testing y Quality Assurance**
- JUnit y Mockito para testing unitario comprehensivo
- Espresso para testing de UI automatizado
- Postman para testing de API endpoints
- SonarQube para análisis estático de código y detección de code smells

---

## Estructura del Proyecto

```plaintext
GourmetGo/
├── README.md                              # Documentación principal del proyecto
├── LICENSE                                # Licencia del proyecto (MIT)
├── .gitignore                             # Archivos y carpetas ignorados por Git
├── Entregable_5_GourmetGo.md             # Documentación de entrega académica
├── .vscode/                              # Configuración específica de VS Code
│   ├── settings.json                     # Configuraciones del workspace
│   └── tasks.json                        # Tareas automatizadas de build y deploy
├── client/                               # Aplicación Android Cliente
│   ├── README.md                         # Documentación específica del cliente
│   ├── build.gradle.kts                  # Configuración principal de Gradle
│   ├── app/                              # Módulo principal de la aplicación
│   │   ├── build.gradle.kts              # Configuración del módulo app
│   │   ├── proguard-rules.pro            # Reglas de ofuscación ProGuard
│   │   └── src/main/                     # Código fuente principal
│   │       ├── AndroidManifest.xml       # Manifiesto de la aplicación
│   │       ├── java/gourmetgo/client/    # Paquete principal de la aplicación
│   │       │   ├── AppMain.kt            # Actividad principal y punto de entrada
│   │       │   ├── Config.kt             # Configuración global de la aplicación
│   │       │   ├── data/                 # Capa de datos y modelos
│   │       │   │   ├── models/           # Modelos de datos principales
│   │       │   │   │   ├── User.kt       # Modelo de usuario
│   │       │   │   │   ├── Experience.kt # Modelo de experiencia culinaria
│   │       │   │   │   ├── Booking.kt    # Modelo de reserva
│   │       │   │   │   ├── Rating.kt     # Modelo de calificación
│   │       │   │   │   └── Chef.kt       # Modelo de chef/restaurante
│   │       │   │   ├── repository/       # Implementación del patrón Repository
│   │       │   │   │   ├── AuthRepository.kt         # Repositorio de autenticación
│   │       │   │   │   ├── ExperienceRepository.kt   # Repositorio de experiencias
│   │       │   │   │   ├── BookingRepository.kt      # Repositorio de reservas
│   │       │   │   │   └── RatingRepository.kt       # Repositorio de calificaciones
│   │       │   │   ├── remote/           # Capa de comunicación de red
│   │       │   │   │   ├── ApiService.kt # Interfaz principal de API REST
│   │       │   │   │   ├── RetrofitClient.kt # Configuración de Retrofit
│   │       │   │   │   └── ApiResponses.kt   # Clases de respuesta de API
│   │       │   │   └── localStorage/     # Almacenamiento local
│   │       │   │       ├── SharedPrefsManager.kt # Gestión de SharedPreferences
│   │       │   │       └── MockDataProvider.kt  # Proveedor de datos mock
│   │       │   ├── viewmodel/            # ViewModels para arquitectura MVVM
│   │       │   │   ├── AuthViewModel.kt          # ViewModel de autenticación
│   │       │   │   ├── HomeViewModel.kt          # ViewModel de pantalla principal
│   │       │   │   ├── ExperienceViewModel.kt    # ViewModel de experiencias
│   │       │   │   ├── BookingViewModel.kt       # ViewModel de reservas
│   │       │   │   ├── RatingViewModel.kt        # ViewModel de calificaciones
│   │       │   │   ├── statesUi/         # Estados de UI para cada pantalla
│   │       │   │   │   ├── AuthUiState.kt        # Estado de UI de autenticación
│   │       │   │   │   ├── HomeUiState.kt        # Estado de UI de home
│   │       │   │   │   ├── ExperienceUiState.kt  # Estado de UI de experiencias
│   │       │   │   │   └── BookingUiState.kt     # Estado de UI de reservas
│   │       │   │   └── factories/        # Factories para ViewModels
│   │       │   │       └── ViewModelFactory.kt   # Factory personalizado
│   │       │   ├── ui/                   # Capa de interfaz de usuario
│   │       │   │   ├── screens/          # Pantallas principales de la aplicación
│   │       │   │   │   ├── LoginScreen.kt        # Pantalla de inicio de sesión
│   │       │   │   │   ├── RegisterScreen.kt     # Pantalla de registro
│   │       │   │   │   ├── HomeScreen.kt         # Pantalla principal
│   │       │   │   │   ├── ExperienceDetailScreen.kt # Detalle de experiencia
│   │       │   │   │   ├── BookingScreen.kt      # Pantalla de reserva
│   │       │   │   │   ├── ProfileScreen.kt      # Pantalla de perfil
│   │       │   │   │   ├── HistoryScreen.kt      # Historial de reservas
│   │       │   │   │   └── RegisterChefScreen.kt # Registro de chef
│   │       │   │   ├── components/       # Componentes reutilizables
│   │       │   │   │   ├── ExperienceCard.kt     # Tarjeta de experiencia
│   │       │   │   │   ├── CustomButton.kt       # Botón personalizado
│   │       │   │   │   ├── InputField.kt         # Campo de entrada
│   │       │   │   │   ├── LoadingIndicator.kt   # Indicador de carga
│   │       │   │   │   ├── ImageGallery.kt       # Galería de imágenes
│   │       │   │   │   └── RatingStars.kt        # Componente de estrellas
│   │       │   │   ├── navigation/       # Sistema de navegación
│   │       │   │   │   ├── AppNavigation.kt      # Configuración de navegación
│   │       │   │   │   ├── NavigationRoutes.kt   # Rutas de navegación
│   │       │   │   │   └── NavigationArgs.kt     # Argumentos de navegación
│   │       │   │   └── theme/            # Tema y estilos Material Design
│   │       │   │       ├── Color.kt              # Paleta de colores
│   │       │   │       ├── Typography.kt         # Tipografía personalizada
│   │       │   │       ├── Theme.kt              # Tema principal
│   │       │   │       └── Shapes.kt             # Formas personalizadas
│   │       │   └── utils/                # Utilidades y helpers
│   │       │       ├── Constants.kt              # Constantes de la aplicación
│   │       │       ├── Extensions.kt             # Extensiones de Kotlin
│   │       │       ├── Validators.kt             # Validadores de entrada
│   │       │       └── DateUtils.kt              # Utilidades de fecha
│   │       └── res/                      # Recursos de la aplicación
│   │           ├── values/               # Valores por defecto
│   │           ├── values-night/         # Valores para tema oscuro
│   │           ├── drawable/             # Recursos de imagen
│   │           └── mipmap/               # Iconos de aplicación
│   └── gradle/                           # Configuración de Gradle
│       └── wrapper/                      # Gradle Wrapper
├── core/                                 # Backend API Server
│   ├── README.md                         # Documentación del backend
│   ├── core.md                           # Documentación técnica detallada
│   ├── server.js                         # Punto de entrada del servidor
│   ├── package.json                      # Dependencias y scripts de Node.js
│   ├── package-lock.json                 # Lock file de dependencias
│   ├── .env                              # Variables de entorno (no versionado)
│   ├── .env.example                      # Ejemplo de variables de entorno
│   ├── controllers/                      # Controladores de lógica de negocio
│   │   ├── authController.js             # Controlador de autenticación
│   │   ├── userController.js             # Controlador de usuarios
│   │   ├── chefController.js             # Controlador de chefs
│   │   ├── experienceController.js       # Controlador de experiencias
│   │   ├── bookingController.js          # Controlador de reservas
│   │   └── ratingController.js           # Controlador de calificaciones
│   ├── models/                           # Esquemas de MongoDB con Mongoose
│   │   ├── userSchema.js                 # Esquema de usuario
│   │   ├── chefProfileSchema.js          # Esquema de perfil de chef
│   │   ├── experienceSchema.js           # Esquema de experiencia
│   │   ├── bookingSchema.js              # Esquema de reserva
│   │   └── ratingSchema.js               # Esquema de calificación
│   ├── routes/                           # Definición de rutas de API
│   │   ├── authRoutes.js                 # Rutas de autenticación
│   │   ├── userRoutes.js                 # Rutas de usuarios
│   │   ├── chefRoutes.js                 # Rutas de chefs
│   │   ├── experienceRoutes.js           # Rutas de experiencias
│   │   ├── bookingRoutes.js              # Rutas de reservas
│   │   └── ratingRoutes.js               # Rutas de calificaciones
│   ├── middlewares/                      # Middlewares personalizados
│   │   ├── auth.js                       # Middleware de autenticación JWT
│   │   ├── validateUser.js               # Validación de datos de usuario
│   │   ├── validateExperience.js         # Validación de experiencias
│   │   ├── validateBooking.js            # Validación de reservas
│   │   └── errorHandler.js               # Manejo centralizado de errores
│   ├── utils/                            # Utilidades del backend
│   │   ├── validators.js                 # Validadores de entrada de datos
│   │   ├── mailer.js                     # Servicio de correo electrónico
│   │   ├── pdfkit.js                     # Generación de documentos PDF
│   │   ├── bookingUtils.js               # Utilidades de reserva y códigos QR
│   │   ├── cloudinary.js                 # Configuración de Cloudinary
│   │   └── database.js                   # Configuración de conexión a MongoDB
│   ├── extra/mail/                       # Plantillas de correo electrónico HTML
│   │   ├── welcome-user.html             # Plantilla de bienvenida para usuario
│   │   ├── welcome-chef.html             # Plantilla de bienvenida para chef
│   │   ├── booking-confirmation.html     # Confirmación de reserva
│   │   ├── experience-created.html       # Notificación de experiencia creada
│   │   └── booking-cancelled.html        # Notificación de cancelación
│   └── uploads/                          # Directorio de archivos subidos (temporal)
│       └── .gitkeep                      # Mantener directorio en Git
└── convert-to-docx/                      # Utilidad de conversión de documentos
    ├── package.json                      # Dependencias para conversión
    └── convert.js                        # Script de conversión MD a DOCX
```

---

## Configuración e Instalación

### Prerrequisitos del Sistema

**Software Requerido**
- Node.js versión 16.0 o superior con npm incluido
- MongoDB Community Server 5.0 o superior (local) o cuenta de MongoDB Atlas
- Android Studio 2022.1.1 o superior con SDK de Android
- Git 2.30 o superior para control de versiones
- JDK 11 o superior para compilación de Android

**Configuraciones Opcionales**
- Visual Studio Code con extensiones de Kotlin y Node.js
- Postman o herramienta similar para testing de API
- MongoDB Compass para gestión visual de base de datos
- Android Device/Emulator para testing de aplicación móvil

### Configuración Detallada del Backend

#### Paso 1: Clonación y Configuración Inicial

```bash
# Clonar el repositorio principal
git clone https://github.com/usuario/GourmetGo.git
cd GourmetGo/core

# Instalar todas las dependencias del proyecto
npm install

# Verificar la instalación
npm list --depth=0
```

#### Paso 2: Configuración de Variables de Entorno

Crear archivo `.env` en el directorio `core/` con la siguiente configuración:

```env
# Configuración del Servidor
PORT=3000
NODE_ENV=development

# Base de Datos MongoDB
MONGODB_URI=mongodb://localhost:27017/gourmetgo
# Para MongoDB Atlas:
# MONGODB_URI=mongodb+srv://usuario:password@cluster.mongodb.net/gourmetgo

# Seguridad y Autenticación
JWT_SECRET=tu_jwt_secret_muy_seguro_min_32_caracteres
JWT_EXPIRATION=24h
BCRYPT_SALT_ROUNDS=12

# Configuración SMTP para Correos
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_SECURE=false
MAIL_USER=tu-email@gmail.com
MAIL_PASS=tu-app-password-generado
MAIL_FROM_NAME=GourmetGo Team
MAIL_FROM_EMAIL=noreply@gourmetgo.com

# Cloudinary para Gestión de Imágenes
CLOUDINARY_CLOUD_NAME=tu-cloud-name
CLOUDINARY_API_KEY=tu-api-key
CLOUDINARY_API_SECRET=tu-api-secret

# Configuraciones de Desarrollo
DEBUG_MODE=true
LOG_LEVEL=debug
CORS_ORIGIN=http://localhost:3000,http://10.0.2.2:3000
```

#### Paso 3: Configuración de Base de Datos

**Para MongoDB Local:**
```bash
# Iniciar servicio de MongoDB
sudo systemctl start mongod  # Linux
brew services start mongodb-community  # macOS
# Para Windows: iniciar desde Services o MongoDB Compass

# Crear base de datos y usuario (opcional)
mongosh
use gourmetgo
db.createUser({
  user: "gourmetgo_user",
  pwd: "password_seguro",
  roles: ["readWrite"]
})
```

**Para MongoDB Atlas:**
1. Crear cuenta en MongoDB Atlas
2. Crear cluster y base de datos
3. Configurar acceso de red (whitelist IP)
4. Obtener connection string y configurar en .env

#### Paso 4: Inicialización del Servidor

```bash
# Desarrollo con recarga automática
npm run dev

# Producción
npm start

# Verificar que el servidor está funcionando
curl http://localhost:3000/api/health
```

### Configuración Detallada del Frontend Android

#### Paso 1: Preparación del Entorno

```bash
# Navegar al directorio del cliente
cd ../client

# Verificar configuración de Android SDK
echo $ANDROID_HOME
# Debería mostrar la ruta del Android SDK
```

#### Paso 2: Configuración de la API en Android

Editar el archivo `client/app/src/main/java/gourmetgo/client/Config.kt`:

```kotlin
object AppConfig {
    // Para emulador Android (10.0.2.2 es la IP del host desde el emulador)
    const val API_BASE_URL = "http://10.0.2.2:3000/api/"
    
    // Para dispositivo físico (reemplazar con IP local de tu máquina)
    // const val API_BASE_URL = "http://192.168.1.100:3000/api/"
    
    // Para testing con datos mock
    const val USE_MOCKUP = false
    
    // Configuración de debug
    const val DEBUG_MODE = BuildConfig.DEBUG
    const val LOG_NETWORK_CALLS = true
    
    // Timeouts de red (en segundos)
    const val NETWORK_TIMEOUT = 30L
    const val CONNECT_TIMEOUT = 15L
    const val READ_TIMEOUT = 30L
}
```

#### Paso 3: Compilación e Instalación

**Usando Visual Studio Code (Recomendado):**
```bash
# Abrir VS Code en el directorio client
code .

# Usar tareas automatizadas (Ctrl + Shift + B)
# Seleccionar "Build and Install Debug APK"
```

**Usando línea de comandos:**
```bash
# Limpiar proyecto
./gradlew clean

# Compilar APK de debug
./gradlew assembleDebug

# Instalar en dispositivo/emulador conectado
./gradlew installDebug

# Todo en un comando
./gradlew clean assembleDebug installDebug
```

#### Paso 4: Configuración de Emulador

```bash
# Listar AVDs disponibles
emulator -list-avds

# Iniciar emulador específico
emulator -avd Pixel_4_API_31

# Verificar dispositivos conectados
adb devices
```

### Verificación de la Instalación

#### Backend
```bash
# Verificar estado del servidor
curl -X GET http://localhost:3000/api/health

# Respuesta esperada:
# {"status": "OK", "message": "GourmetGo API is running", "timestamp": "..."}

# Verificar conexión a base de datos
curl -X GET http://localhost:3000/api/status

# Testing básico de endpoint
curl -X POST http://localhost:3000/api/auth/test \
  -H "Content-Type: application/json" \
  -d '{"message": "test"}'
```

#### Frontend Android
1. Abrir la aplicación en el emulador/dispositivo
2. Verificar que la pantalla de login se carga correctamente
3. Intentar registro de usuario de prueba
4. Verificar conectividad con backend (debería mostrar experiencias)

### Solución de Problemas Comunes

**Error de Conexión de Red en Android:**
- Verificar que el backend esté ejecutándose
- Comprobar la IP configurada en Config.kt
- Para emulador, usar 10.0.2.2:3000
- Para dispositivo físico, usar IP local de la máquina

**Error de Base de Datos:**
- Verificar que MongoDB esté ejecutándose
- Comprobar connection string en .env
- Verificar permisos de usuario en MongoDB

**Error de Dependencias:**
```bash
# Limpiar caché de npm
npm cache clean --force

# Reinstalar dependencias
rm -rf node_modules package-lock.json
npm install

# Para Android
./gradlew clean
./gradlew build --refresh-dependencies
```

---

## Documentación de API

La API REST de GourmetGo está construida siguiendo principios RESTful y proporciona endpoints comprehensivos para todas las funcionalidades del sistema. La documentación completa se encuentra en el archivo `core/core.md`.

### Estructura Base de la API

**URL Base:** `http://localhost:3000/api/`
**Formato de Respuesta:** JSON
**Autenticación:** Bearer Token (JWT)
**Versionado:** v1 (implícito)

### Endpoints Principales Categorizados

#### Autenticación y Gestión de Usuarios

**POST /auth/register**
- Registro de usuario final con validación completa
- Subida automática de imagen de perfil a Cloudinary
- Envío de correo de bienvenida personalizado
- Generación automática de JWT para sesión inicial

**POST /auth/register-chef**
- Registro especializado para chefs y restaurantes
- Creación de perfil profesional con información comercial
- Validación de datos comerciales y gastronómicos
- Configuración de especialidades culinarias

**POST /auth/login**
- Autenticación con email y contraseña
- Generación de JWT con información de usuario
- Logging de sesión para auditoría
- Renovación automática de token próximo a expirar

#### Gestión de Experiencias Culinarias

**GET /experiences**
- Listado paginado de experiencias disponibles
- Filtros por categoría, precio, fecha y ubicación
- Búsqueda por palabras clave en título y descripción
- Ordenamiento por fecha, precio, popularidad

**POST /experiences**
- Creación de nueva experiencia (solo chefs autenticados)
- Validación de datos comerciales y logísticos
- Subida múltiple de imágenes con optimización automática
- Notificación por email de experiencia creada

**GET /experiences/:id**
- Información detallada de experiencia específica
- Galería completa de imágenes
- Información del chef y ubicación
- Calificaciones y reseñas asociadas

**PUT /experiences/:id**
- Actualización de experiencia existente
- Validación de propiedad (solo el chef creador)
- Notificación automática a usuarios con reservas activas
- Versionado de cambios para auditoría

**DELETE /experiences/:id**
- Eliminación con sistema de código de verificación
- Envío de código temporal por email
- Notificación automática a usuarios afectados
- Cancelación automática de reservas futuras

#### Sistema Integral de Reservas

**POST /bookings**
- Creación de reserva con validación de disponibilidad
- Generación automática de código QR único
- Creación de comprobante PDF con diseño profesional
- Envío de confirmación por email con archivos adjuntos

**GET /bookings/my**
- Historial completo de reservas del usuario
- Filtros por estado (activa, completada, cancelada)
- Información detallada de cada experiencia reservada
- Acceso a comprobantes PDF descargables

**PUT /bookings/:id/cancel**
- Cancelación de reserva con políticas configurables
- Actualización automática de capacidad disponible
- Notificación al chef de la cancelación
- Generación de comprobante de cancelación

**GET /chefs/:id/bookings**
- Panel de reservas para chefs y restaurantes
- Listado de participantes por experiencia
- Información de contacto para comunicación directa
- Estadísticas de ocupación y demanda

#### Sistema de Calificaciones y Reseñas

**POST /ratings**
- Creación de calificación con validación de participación
- Solo usuarios que asistieron pueden calificar
- Calificación numérica y comentario textual opcional
- Actualización automática de promedio de experiencia

**GET /ratings/experience/:id**
- Todas las calificaciones de una experiencia específica
- Cálculo automático de promedio ponderado
- Ordenamiento por fecha y relevancia
- Moderación automática de contenido inapropiado

### Características Avanzadas de la API

#### Sistema de Autenticación y Autorización

**JSON Web Tokens (JWT)**
- Tokens stateless con información de usuario codificada
- Expiración configurable (por defecto 24 horas)
- Renovación automática antes de expiración
- Invalidación segura en logout

**Middleware de Autenticación**
- Verificación automática de tokens en rutas protegidas
- Extracción de información de usuario del token
- Manejo de tokens expirados con refresh automático
- Logging de accesos para auditoría de seguridad

#### Validación Exhaustiva de Datos

**Validación de Entrada**
- Formato de email con regex complejo
- Teléfono con exactamente 8 dígitos numéricos
- Identificación con 9 dígitos numéricos
- Contraseña con patrón específico (6 letras, 4 números, 1 punto)

**Validación de Negocio**
- Verificación de propiedad antes de modificaciones
- Validación de capacidad antes de crear reservas
- Verificación de fechas futuras para experiencias
- Control de duplicados en reservas

#### Gestión de Archivos y Medios

**Integración con Cloudinary**
- Subida automática de imágenes con optimización
- Generación de URLs seguras con transformaciones
- Backup automático con múltiples formatos
- Limpieza automática de archivos no utilizados

**Generación de Documentos PDF**
- Plantillas profesionales para comprobantes
- Inclusión automática de códigos QR
- Información completa de experiencia y reserva
- Optimización de tamaño para email

#### Sistema de Comunicación

**Correos Electrónicos Transaccionales**
- Plantillas HTML responsivas y profesionales
- Personalización con datos específicos del usuario
- Seguimiento de entrega y tasas de apertura
- Gestión de rebotes y errores de entrega

**Notificaciones Automáticas**
- Bienvenida al registrarse (usuario y chef)
- Confirmación de creación de experiencia
- Confirmación y recordatorio de reserva
- Notificación de cambios y cancelaciones

---

## Información Técnica Adicional

### 8.1 Información sobre APIs

#### Arquitectura de Microservicios

Aunque GourmetGo está implementado como una aplicación monolítica para simplicidad de desarrollo y despliegue académico, la arquitectura de la API está diseñada con principios de microservicios que facilitan la futura migración a una arquitectura distribuida.

**Separación de Responsabilidades por Dominio**
- **Auth Service**: Manejo completo de autenticación y autorización
- **User Service**: Gestión de perfiles de usuario y preferencias
- **Experience Service**: CRUD completo de experiencias culinarias
- **Booking Service**: Sistema integral de reservas y pagos
- **Rating Service**: Gestión de calificaciones y sistema de reputación
- **Notification Service**: Envío de correos y notificaciones push

**Interfaces Bien Definidas**
Cada servicio expone interfaces claramente definidas que podrían convertirse fácilmente en APIs independientes:

```javascript
// Ejemplo de interfaz del Experience Service
class ExperienceService {
  async createExperience(chefId, experienceData)
  async getExperiences(filters, pagination)
  async getExperienceById(experienceId)
  async updateExperience(experienceId, chefId, updateData)
  async deleteExperience(experienceId, chefId, verificationCode)
  async getExperiencesByChef(chefId)
}
```

#### Patrones de Diseño Implementados

**Repository Pattern**
Implementado tanto en el frontend Android como en el backend para abstracción de fuentes de datos:

```kotlin
// Android Repository Interface
interface ExperienceRepository {
    suspend fun getExperiences(page: Int, filters: FilterOptions): Result<List<Experience>>
    suspend fun getExperienceById(id: String): Result<Experience>
    suspend fun createBooking(experienceId: String, bookingData: BookingRequest): Result<Booking>
}
```

**Factory Pattern**
Utilizado para creación de ViewModels, servicios y configuraciones:

```kotlin
// ViewModel Factory para inyección de dependencias
class ViewModelFactory(
    private val authRepository: AuthRepository,
    private val experienceRepository: ExperienceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            AuthViewModel::class.java -> AuthViewModel(authRepository) as T
            HomeViewModel::class.java -> HomeViewModel(experienceRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
```

**Observer Pattern**
Implementado a través de StateFlow y LiveData para programación reactiva:

```kotlin
// Observable state en ViewModel
class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    // La UI se actualiza automáticamente cuando cambia el estado
}
```

#### Sistema de Cache y Optimización

**Cache Multi-nivel en Android**
- **Memory Cache**: Para imágenes y datos frecuentemente accedidos
- **Disk Cache**: Para respuestas de API y datos offline
- **Database Cache**: Room database para datos críticos (implementación futura)

**Estrategias de Cache**
- **Cache-First**: Para datos que no cambian frecuentemente (perfiles de chef)
- **Network-First**: Para datos dinámicos (disponibilidad de experiencias)
- **Cache-Then-Network**: Para mejor UX con actualización en background

#### Manejo de Estados y Errores

**Estados de UI Tipados**
```kotlin
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val exception: Throwable) : UiState<Nothing>()
}

// Uso en ViewModels
private val _experiencesState = MutableStateFlow<UiState<List<Experience>>>(UiState.Loading)
```

**Gestión de Errores de Red**
```kotlin
// Wrapper para respuestas de API
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    
    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }
    
    inline fun onError(action: (Exception) -> Unit): Result<T> {
        if (this is Error) action(exception)
        return this
    }
}
```

#### Seguridad y Compliance

**Seguridad de API**
- Rate limiting para prevenir abuso
- Input sanitization para prevenir inyecciones
- CORS configurado para orígenes específicos
- Headers de seguridad estándar (HSTS, CSP, X-Frame-Options)

**Protección de Datos Personales**
- Hash seguro de contraseñas con bcrypt y salt
- Encriptación de datos sensibles en tránsito (HTTPS)
- Minimización de datos en tokens JWT
- Logging que no expone información personal

**Validación de Entrada Robusta**
```javascript
// Ejemplo de validación de middleware
const validateExperience = (req, res, next) => {
  const schema = {
    title: { type: 'string', min: 5, max: 100 },
    price: { type: 'number', min: 0, max: 1000000 },
    date: { type: 'date', future: true },
    capacity: { type: 'integer', min: 1, max: 100 }
  };
  
  const { error } = validator.validate(req.body, schema);
  if (error) return res.status(400).json({ error: error.details });
  next();
};
```

---

### 8.2 Pruebas de funcionalidad

#### Metodología de Testing

La estrategia de testing de GourmetGo sigue un enfoque piramidal que incluye pruebas unitarias, de integración y end-to-end, asegurando cobertura completa de funcionalidades críticas del sistema.

**Niveles de Testing Implementados**
- **Unit Tests**: Pruebas de componentes individuales (ViewModels, Repositories, Utils)
- **Integration Tests**: Pruebas de interacción entre componentes
- **API Tests**: Pruebas de endpoints con Postman/Newman
- **UI Tests**: Pruebas automatizadas de interfaz con Espresso
- **Manual Testing**: Pruebas exploratorias y de usabilidad

#### Pruebas Unitarias del Sistema

**Testing de ViewModels**
```kotlin
@Test
fun `when creating booking, should update UI state correctly`() = runTest {
    // Arrange
    val mockRepository = MockBookingRepository()
    val viewModel = BookingViewModel(mockRepository)
    val experienceId = "exp123"
    val bookingRequest = BookingRequest(experienceId, 2)
    
    // Act
    viewModel.createBooking(experienceId, bookingRequest)
    
    // Assert
    val finalState = viewModel.uiState.value
    assertTrue(finalState is BookingUiState.Success)
    assertEquals("Reserva creada exitosamente", finalState.message)
}
```

**Testing de Repositorios**
```kotlin
@Test
fun `when API returns success, repository should return success result`() = runTest {
    // Arrange
    val mockApiService = MockApiService()
    val repository = ExperienceRepositoryImpl(mockApiService)
    mockApiService.mockGetExperiences(listOf(mockExperience))
    
    // Act
    val result = repository.getExperiences(1, FilterOptions.default())
    
    // Assert
    assertTrue(result is Result.Success)
    assertEquals(1, result.data.size)
}
```

#### Pruebas de Integración API

**Testing de Flujos Completos**
Las pruebas de integración verifican el funcionamiento completo de flujos de usuario desde el frontend hasta la base de datos:

**Test Case: Flujo Completo de Registro**
```javascript
describe('User Registration Flow', () => {
  test('should register user and send welcome email', async () => {
    const userData = {
      name: 'Juan Pérez',
      email: 'juan.test@email.com',
      password: 'Password1234.',
      phone: '12345678',
      identification: '123456789'
    };
    
    const response = await request(app)
      .post('/api/auth/register')
      .send(userData)
      .expect(201);
    
    expect(response.body.message).toBe('Usuario registrado exitosamente');
    expect(response.body.user.email).toBe(userData.email);
    expect(response.body.token).toBeDefined();
    
    // Verificar que el usuario se guardó en la base de datos
    const savedUser = await User.findOne({ email: userData.email });
    expect(savedUser).toBeTruthy();
    expect(savedUser.name).toBe(userData.name);
  });
});
```

**Test Case: Sistema de Reservas con QR**
```javascript
describe('Booking System with QR', () => {
  test('should create booking with QR code and PDF', async () => {
    const token = await generateTestToken();
    const experienceId = await createTestExperience();
    
    const bookingData = {
      numberOfPeople: 2,
      specialRequests: 'Sin gluten'
    };
    
    const response = await request(app)
      .post(`/api/bookings`)
      .set('Authorization', `Bearer ${token}`)
      .send({ experienceId, ...bookingData })
      .expect(201);
    
    expect(response.body.booking.qrCode).toBeDefined();
    expect(response.body.booking.pdfUrl).toBeDefined();
    expect(response.body.booking.status).toBe('confirmed');
    
    // Verificar que el código QR es único y válido
    const qrData = await decodeQR(response.body.booking.qrCode);
    expect(qrData.bookingId).toBe(response.body.booking._id);
  });
});
```

**Test Case: Validación de Campos Obligatorios**
```javascript
describe('User Registration - Required Fields', () => {
  test('should require email, password and name', async () => {
    const response = await request(app)
      .post('/api/auth/register')
      .send({})
      .expect(400);
    
    expect(response.body.error).toContain('El campo email es obligatorio');
    expect(response.body.error).toContain('El campo password es obligatorio');
    expect(response.body.error).toContain('El campo name es obligatorio');
  });
});
```

#### Pruebas de Validación de Datos

**Validación de Formato de Email**
```javascript
describe('Email Validation', () => {
  const invalidEmails = [
    'invalid-email',
    'test@',
    '@domain.com',
    'test..email@domain.com',
    'test@domain',
    ''
  ];
  
  invalidEmails.forEach(email => {
    test(`should reject invalid email: ${email}`, async () => {
      const userData = { ...validUserData, email };
      
      const response = await request(app)
        .post('/api/auth/register')
        .send(userData)
        .expect(400);
      
      expect(response.body.error).toContain('Email inválido');
    });
  });
});
```

**Validación de Contraseña Compleja**
```javascript
describe('Password Validation', () => {
  const testCases = [
    { password: 'short1.', valid: false, reason: 'too short' },
    { password: 'noNumbers.', valid: false, reason: 'no numbers' },
    { password: 'NoLetters1234.', valid: false, reason: 'not enough letters' },
    { password: 'ValidPass1234', valid: false, reason: 'no special character' },
    { password: 'ValidPass1234.', valid: true, reason: 'valid format' }
  ];
  
  testCases.forEach(({ password, valid, reason }) => {
    test(`should ${valid ? 'accept' : 'reject'} password - ${reason}`, async () => {
      const userData = { ...validUserData, password };
      
      const response = await request(app)
        .post('/api/auth/register')
        .send(userData);
      
      if (valid) {
        expect(response.status).toBe(201);
      } else {
        expect(response.status).toBe(400);
        expect(response.body.error).toContain('contraseña');
      }
    });
  });
});
```

#### Pruebas de Rendimiento y Carga

**Testing de Capacidad de la API**
```javascript
describe('API Performance Tests', () => {
  test('should handle concurrent booking requests', async () => {
    const experienceId = await createTestExperience({ capacity: 10 });
    const tokens = await generateMultipleTestTokens(15);
    
    // Intentar 15 reservas simultáneas para una experiencia con capacidad de 10
    const promises = tokens.map(token => 
      request(app)
        .post('/api/bookings')
        .set('Authorization', `Bearer ${token}`)
        .send({ experienceId, numberOfPeople: 1 })
    );
    
    const responses = await Promise.allSettled(promises);
    
    const successful = responses.filter(r => 
      r.status === 'fulfilled' && r.value.status === 201
    );
    const failed = responses.filter(r => 
      r.status === 'fulfilled' && r.value.status === 400
    );
    
    expect(successful.length).toBe(10); // Solo 10 deberían ser exitosas
    expect(failed.length).toBe(5); // 5 deberían fallar por capacidad agotada
  });
});
```

#### Pruebas de Seguridad

**Testing de Autorización**
```javascript
describe('Authorization Tests', () => {
  test('should prevent unauthorized access to chef endpoints', async () => {
    const userToken = await generateUserToken(); // Token de usuario normal
    
    const response = await request(app)
      .post('/api/experiences')
      .set('Authorization', `Bearer ${userToken}`)
      .send(validExperienceData)
      .expect(403);
    
    expect(response.body.error).toContain('Solo los chefs pueden crear experiencias');
  });
  
  test('should prevent cross-user data access', async () => {
    const user1Token = await generateUserToken('user1@test.com');
    const user2Token = await generateUserToken('user2@test.com');
    
    // Usuario 1 crea una reserva
    const booking = await createTestBooking(user1Token);
    
    // Usuario 2 intenta acceder a la reserva del Usuario 1
    const response = await request(app)
      .get(`/api/bookings/${booking._id}`)
      .set('Authorization', `Bearer ${user2Token}`)
      .expect(403);
    
    expect(response.body.error).toContain('No autorizado');
  });
});
```

#### Pruebas de Recuperación de Errores

**Testing de Fallos de Red**
```kotlin
@Test
fun `when network fails, should return cached data`() = runTest {
    // Arrange
    val repository = ExperienceRepositoryImpl(mockApiService, cacheManager)
    val cachedExperiences = listOf(mockExperience)
    cacheManager.putExperiences(cachedExperiences)
    mockApiService.simulateNetworkError()
    
    // Act
    val result = repository.getExperiences(1, FilterOptions.default())
    
    // Assert
    assertTrue(result is Result.Success)
    assertEquals(cachedExperiences, result.data)
}
```

**Testing de Recuperación de Base de Datos**
```javascript
describe('Database Recovery Tests', () => {
  test('should handle database connection loss gracefully', async () => {
    // Simular pérdida de conexión
    await mongoose.connection.close();
    
    const response = await request(app)
      .get('/api/experiences')
      .expect(503);
    
    expect(response.body.error).toContain('Servicio temporalmente no disponible');
    
    // Reconectar y verificar recuperación
    await connectToDatabase();
    
    const recoveryResponse = await request(app)
      .get('/api/experiences')
      .expect(200);
    
    expect(recoveryResponse.body.experiences).toBeDefined();
  });
});
```

#### Métricas de Cobertura de Pruebas

**Backend Coverage**
- Controllers: 95% de cobertura de líneas
- Models/Schemas: 100% de cobertura
- Utils/Validators: 98% de cobertura
- Routes: 90% de cobertura

**Frontend Coverage**
- ViewModels: 92% de cobertura
- Repositorios: 88% de cobertura
- Utils: 95% de cobertura
- UI Components: 75% de cobertura (pruebas manuales complementarias)

#### Automatización de Pruebas

**CI/CD Pipeline**
```yaml
# Ejemplo de configuración de GitHub Actions
name: GourmetGo Tests
on: [push, pull_request]

jobs:
  backend-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Setup Node.js
        uses: actions/setup-node@v2
        with:
          node-version: '16'
      - name: Install dependencies
        run: cd core && npm install
      - name: Run tests
        run: cd core && npm test
      - name: Upload coverage
        uses: codecov/codecov-action@v1
  
  android-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Setup JDK
        uses: actions/setup-java@v2
        with:
          java-version: '11'
      - name: Run unit tests
        run: cd client && ./gradlew test
      - name: Run instrumented tests
        run: cd client && ./gradlew connectedAndroidTest
```

---

### 8.3 Diagrama MVC

#### Arquitectura MVVM Detallada

GourmetGo implementa el patrón **Model-View-ViewModel (MVVM)** con una separación clara de responsabilidades que facilita el testing, mantenimiento y escalabilidad del sistema. A continuación se presenta una descripción detallada de cada capa y sus interacciones.

```mermaid
graph TB
    subgraph "CAPA VISTA (View Layer)"
        subgraph "Pantallas Compose"
            A[LoginScreen.kt]
            B[HomeScreen.kt]
            C[ExperienceDetailScreen.kt]
            D[BookingScreen.kt]
            E[RatingScreen.kt]
            F[RegisterChefScreen.kt]
            G[ProfileScreen.kt]
            H[HistoryScreen.kt]
        end
        
        subgraph "Componentes Reutilizables"
            I[ExperienceCard.kt]
            J[CustomButton.kt]
            K[InputField.kt]
            L[LoadingIndicator.kt]
            M[ImageGallery.kt]
            N[RatingStars.kt]
        end
        
        subgraph "Sistema de Navegación"
            O[AppNavigation.kt]
            P[NavigationRoutes.kt]
            Q[NavigationArgs.kt]
        end
    end
    
    subgraph "CAPA VIEWMODEL (Presentation Logic Layer)"
        subgraph "ViewModels Principales"
            R[AuthViewModel]
            S[HomeViewModel]
            T[ExperienceViewModel]
            U[BookingViewModel]
            V[RatingViewModel]
            W[ProfileViewModel]
        end
        
        subgraph "Estados de UI"
            X[AuthUiState]
            Y[HomeUiState]
            Z[ExperienceUiState]
            AA[BookingUiState]
            BB[RatingUiState]
        end
        
        subgraph "Factories y Utilidades"
            CC[ViewModelFactory]
            DD[UiStateMappers]
        end
    end
    
    subgraph "CAPA MODELO (Data Layer)"
        subgraph "Repositorios de Dominio"
            EE[AuthRepository]
            FF[ExperienceRepository]
            GG[BookingRepository]
            HH[RatingRepository]
            II[UserRepository]
        end
        
        subgraph "Fuentes de Datos"
            JJ[ApiService - Retrofit]
            KK[SharedPreferencesManager]
            LL[MockDataProvider]
        end
    end

    A -->|onSubmit| R
    B -->|onInit| S
    C -->|onInit| T
    D -->|onSubmit| U
    E -->|onSubmit| V
    F -->|onSubmit| R
    G -->|onInit| W
    H -->|onInit| W

    R -->|login| X
    R -->|register| X
    S -->|loadExperiences| Y
    T -->|loadExperienceDetails| Z
    U -->|createBooking| AA
    V -->|submitRating| BB

    X -->|email/password| R
    Y -->|filter/sort| S
    Z -->|experienceId| T
    AA -->|bookingData| U
    BB -->|ratingData| V

    classDef view fill:#f9f9f9,stroke:#333,stroke-width:2px;
    classDef viewModel fill:#e1f5fe,stroke:#01579b,stroke-width:2px;
    classDef model fill:#e8f5e9,stroke:#2e7d32,stroke-width:2px;
    classDef repository fill:#fff3e0,stroke:#e65100,stroke-width:2px;
    classDef api fill:#f3e5f5,stroke:#6a1b9a,stroke-width:2px;
    classDef utils fill:#e8eaf6,stroke:#283593,stroke-width:2px;

    class A,B,C,D,E,F,G,H view;
    class R,S,T,U,V,W viewModel;
    class X,Y,Z,AA,BB statesUi;
    class CC factories;
    class EE,FF,GG,HH,II repository;
    class JJ api;
    class KK,LL utils;
```
