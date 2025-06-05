# Guía de Compilación y Despliegue - Aplicación Android (Kotlin)

> **Manual técnico para la compilación y despliegue de la aplicación Android GourmetGo utilizando Visual Studio Code**

## Requisitos Previos

Para proceder con la compilación, es necesario verificar que se encuentren instalados los siguientes componentes:

- **Android Studio** - Entorno de desarrollo integrado para Android, incluye el SDK necesario
- **Android Debug Bridge (ADB)** - Herramienta de línea de comandos incluida con Android Studio
- **Visual Studio Code** - Editor de código fuente con soporte para extensiones
- **Emulador Android** o dispositivo físico Android habilitado para depuración

## Configuración del Entorno de Desarrollo

### Verificación de Android Debug Bridge

Ejecute el siguiente comando en la terminal integrada de Visual Studio Code para verificar la conectividad con dispositivos Android:

```bash
adb devices
```

La salida esperada debe mostrar los dispositivos conectados:

```plaintext
List of devices attached
emulator-5554   device
```

### Estructura del Proyecto

Verifique que la estructura del proyecto corresponda con la siguiente organización:

```plaintext
GourmetGo/
├── client/          # Aplicación Android (Kotlin)
├── core/           # Servidor Backend (Node.js)
└── .vscode/        # Configuración de Visual Studio Code
```

## Metodología de Compilación

### Opción A: Utilización de Tareas Automatizadas (Recomendado)

El entorno de desarrollo incluye tareas preconfiguradas para optimizar el flujo de trabajo:

- **Compilar e Instalar**: `Ctrl + Shift + P` → "Tasks: Run Task" → "Compilar e Instalar App"
- **Ejecutar Aplicación**: `Ctrl + Shift + P` → "Tasks: Run Task" → "Ejecutar App"
- **Proceso Completo**: `Ctrl + Shift + P` → "Tasks: Run Task" → "Compilar + Instalar + Ejecutar"

### Método de Acceso Directo

Para un acceso más eficiente, utilice el siguiente atajo de teclado:

```plaintext
Ctrl + Shift + B
```

Este comando ejecuta automáticamente la compilación e instalación de la aplicación.

### Opción B: Compilación Manual por Terminal

#### Navegación al Directorio del Proyecto

```bash
cd "C:\Users\[NOMBRE-USUARIO]\Documents\GitHub\GourmetGo\client"
```

#### Compilación e Instalación

```bash
./gradlew installDebug
```

#### Ejecución de la Aplicación

```bash
adb shell am start -n gourmetgo.client/.AppMain
```

## Procedimiento de Desarrollo

### Flujo de Trabajo Estándar

1. **Guardar archivos modificados** (`Ctrl + S`)
2. **Ejecutar compilación e instalación** (`Ctrl + Shift + B`)
3. **Verificar cambios en el emulador**

### Compilación Automática Continua

Para proyectos con modificaciones frecuentes, utilice el modo de compilación continua:

```bash
./gradlew --continuous installDebug
```

Este comando recompila automáticamente la aplicación al detectar cambios en los archivos fuente.

## Comandos de Gestión del Sistema

### Administración de la Aplicación

```bash
# Verificar aplicaciones instaladas
adb shell pm list packages | findstr gourmet

# Desinstalar la aplicación
adb uninstall gourmetgo.client

# Visualizar registros en tiempo real
adb logcat

# Limpiar artefactos de compilación
./gradlew clean
```

### Administración del Emulador

```bash
# Listar dispositivos conectados
adb devices

# Reiniciar Android Debug Bridge
adb kill-server
adb start-server
```

## Resolución de Problemas

### Error: "ADB no reconocido como comando"

**Solución:**

- Verificar la instalación de Android Studio
- Agregar la ruta del SDK al PATH del sistema:
  - Localizar directorio: `C:\Users\[USUARIO]\AppData\Local\Android\Sdk\platform-tools`
  - Añadir al PATH en las variables de entorno del sistema

### Error: "No devices found"

**Solución:**

- Iniciar Android Studio y ejecutar un emulador
- Conectar dispositivo físico con depuración USB habilitada

### Error: "Build failed"

**Solución:**

- Ejecutar limpieza del proyecto: `./gradlew clean`
- Verificar sintaxis del código Kotlin
- Intentar nueva compilación

### Error: "La aplicación no aparece en el emulador"

**Solución:**

- Verificar instalación exitosa: `adb shell pm list packages | findstr gourmet`
- Ejecutar manualmente: `adb shell am start -n gourmetgo.client/.AppMain`

## Configuración Automatizada

### Archivo tasks.json

Las siguientes tareas se encuentran preconfiguradas en el entorno de desarrollo:

- Compilar e Instalar App
- Ejecutar App
- Compilar + Instalar + Ejecutar
- Limpiar Proyecto

## Procedimiento Resumido

### Pasos Esenciales para Desarrollo

1. **Abrir Visual Studio Code** en el directorio raíz de GourmetGo
2. **Realizar modificaciones** en archivos `.kt`
3. **Ejecutar compilación** mediante `Ctrl + Shift + B`
4. **Verificar implementación** en el emulador Android

## Conclusión

Este sistema de compilación automatizada permite un desarrollo eficiente de la aplicación Android, reduciendo significativamente los tiempos de compilación y despliegue. La implementación de tareas automatizadas en Visual Studio Code optimiza el flujo de trabajo y minimiza la posibilidad de errores durante el proceso de desarrollo.

### Recomendación Técnica

Para obtener la máxima eficiencia, se recomienda utilizar la secuencia completa automatizada: `Ctrl + Shift + P` → "Compilar + Instalar + Ejecutar" para proyectos en desarrollo activo.

---

*Manual técnico - Proyecto GourmetGo - Compilación de aplicación Android con Visual Studio Code*
