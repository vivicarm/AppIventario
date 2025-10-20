# 📋 Guía de Configuración de Firebase para App de Inventario

Esta guía te ayudará a configurar correctamente las colecciones y campos en Firebase Firestore y Firebase Storage para tu aplicación de inventario.

---

## 📚 Índice
1. [Configuración Inicial de Firebase](#1-configuración-inicial-de-firebase)
2. [Firebase Authentication](#2-firebase-authentication)
3. [Firestore Database - Colecciones y Campos](#3-firestore-database---colecciones-y-campos)
4. [Firebase Storage](#4-firebase-storage)
5. [Reglas de Seguridad](#5-reglas-de-seguridad)
6. [Índices Compuestos](#6-índices-compuestos-opcional)

---

## 1. Configuración Inicial de Firebase

### Paso 1: Crear Proyecto en Firebase Console
1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Haz clic en "Agregar proyecto"
3. Ingresa el nombre del proyecto: `AppInventario` (o el nombre que prefieras)
4. Acepta los términos y crea el proyecto

### Paso 2: Agregar App Android
1. En la página principal del proyecto, haz clic en el ícono de Android
2. Ingresa el nombre del paquete: `com.app.myappdeinventario`
3. Descarga el archivo `google-services.json`
4. Coloca el archivo en: `app/google-services.json` (ya lo tienes configurado)

---

## 2. Firebase Authentication

### Configuración de Authentication
1. En Firebase Console, ve a **Authentication** → **Sign-in method**
2. Habilita el proveedor **Email/Password**:
   - Haz clic en "Email/Password"
   - Activa el interruptor "Habilitar"
   - Guarda los cambios

### Usuarios que se Crearán
Los usuarios se registrarán automáticamente desde la app con los siguientes datos:
- **Email**: Correo electrónico del usuario
- **Password**: Contraseña (mínimo 6 caracteres requerido por Firebase)

---

## 3. Firestore Database - Colecciones y Campos

### Paso 1: Crear Firestore Database
1. En Firebase Console, ve a **Firestore Database**
2. Haz clic en "Crear base de datos"
3. Selecciona el modo:
   - **Modo de prueba** (para desarrollo): Permite lectura/escritura durante 30 días
   - **Modo de producción** (recomendado después): Requiere reglas de seguridad
4. Selecciona la ubicación más cercana (ej: `southamerica-east1` para Sudamérica)

### Paso 2: Crear Colecciones y Estructura de Datos

#### 📁 Colección: `usuarios`
Esta colección almacena la información de los usuarios registrados.

**Ruta**: `/usuarios/{idUsuario}`

**Campos**:
```
usuarios/
└── {idUsuario} (Document ID - generado automáticamente por Firebase Auth)
    ├── idUsuario: string          // UID del usuario de Firebase Auth
    ├── nombre: string              // Nombre del usuario
    ├── apellido: string            // Apellido del usuario
    ├── genero: string              // "Hombre" o "Mujer"
    ├── email: string               // Correo electrónico
    └── password: string            // Contraseña (⚠️ NO recomendado guardar en producción)
```

**Ejemplo de documento**:
```json
{
  "idUsuario": "abc123xyz789",
  "nombre": "Juan",
  "apellido": "Pérez",
  "genero": "Hombre",
  "email": "juan.perez@example.com",
  "password": "miPassword123"
}
```

**⚠️ IMPORTANTE - Seguridad**:
- En producción, **NO debes guardar la contraseña** en Firestore
- Firebase Authentication ya maneja las contraseñas de forma segura
- Elimina el campo `password` del modelo [`Usuario.kt`](app/src/main/java/com/app/myappdeinventario/model/Usuario.kt:10) antes de producción

---

#### 📁 Colección: `categorias`
Esta colección almacena las categorías de productos.

**Ruta**: `/categorias/{idCategory}`

**Campos**:
```
categorias/
└── {idCategory} (Document ID - generado automáticamente)
    ├── idCategory: string              // ID único de la categoría
    ├── nombreCategory: string          // Nombre de la categoría
    ├── descripcionCategory: string     // Descripción de la categoría
    └── imagenCategoria: string         // URL de la imagen en Firebase Storage
```

**Ejemplo de documento**:
```json
{
  "idCategory": "cat_001",
  "nombreCategory": "Electrónica",
  "descripcionCategory": "Productos electrónicos y tecnológicos",
  "imagenCategoria": "https://firebasestorage.googleapis.com/v0/b/tu-proyecto.appspot.com/o/categorias%2F1234567890.jpg?alt=media&token=xyz"
}
```

**Cómo crear manualmente (opcional)**:
1. En Firestore Console, haz clic en "Iniciar colección"
2. ID de colección: `categorias`
3. Agrega un documento de ejemplo con los campos mencionados

---

#### 📁 Colección: `productos`
Esta colección almacena los productos del inventario.

**Ruta**: `/productos/{idProduct}`

**Campos**:
```
productos/
└── {idProduct} (Document ID - generado automáticamente)
    ├── idProduct: string           // ID único del producto
    ├── nombre: string              // Nombre del producto
    ├── precioC: number             // Precio de compra (Double)
    ├── precioV: number             // Precio de venta (Double)
    ├── precioPromocion: number     // Precio en promoción (Double)
    ├── descripcion: string         // Descripción del producto
    ├── stock: number               // Cantidad en stock (Integer)
    ├── image: array                // Array de URLs de imágenes
    ├── fechaCreacion: string       // Fecha de creación (formato: "2024-01-15")
    └── idCategory: string          // ID de la categoría asociada
```

**Ejemplo de documento**:
```json
{
  "idProduct": "prod_001",
  "nombre": "Laptop HP",
  "precioC": 800.00,
  "precioV": 1200.00,
  "precioPromocion": 1000.00,
  "descripcion": "Laptop HP 15.6 pulgadas, 8GB RAM, 256GB SSD",
  "stock": 15,
  "image": [
    "https://firebasestorage.googleapis.com/v0/b/tu-proyecto.appspot.com/o/productos%2Fimg1.jpg?alt=media",
    "https://firebasestorage.googleapis.com/v0/b/tu-proyecto.appspot.com/o/productos%2Fimg2.jpg?alt=media"
  ],
  "fechaCreacion": "2024-01-15",
  "idCategory": "cat_001"
}
```

**Tipos de datos importantes**:
- `precioC`, `precioV`, `precioPromocion`: Tipo **number** (Double en Kotlin)
- `stock`: Tipo **number** (Integer en Kotlin)
- `image`: Tipo **array** (List<String> en Kotlin)

---

#### 📁 Colección: `especificaciones`
Esta colección almacena especificaciones adicionales (actualmente en desarrollo).

**Ruta**: `/especificaciones/{idEspecificacion}`

**Campos**:
```
especificaciones/
└── {idEspecificacion} (Document ID - generado automáticamente)
    ├── idEspecificacion: string        // ID único de la especificación
    └── nombreEspecificación: string    // Nombre de la especificación
```

**Ejemplo de documento**:
```json
{
  "idEspecificacion": "spec_001",
  "nombreEspecificación": "Color: Azul"
}
```

**Nota**: Esta colección está definida pero aún no se usa en la app. Puedes crearla para uso futuro.

---

#### 📁 Colección: `variaciones`
Esta colección está definida pero vacía en el código actual. Puedes omitirla por ahora.

---

## 4. Firebase Storage

Firebase Storage se usa para almacenar imágenes de categorías y productos.

### Configuración de Storage
1. En Firebase Console, ve a **Storage**
2. Haz clic en "Comenzar"
3. Selecciona las reglas de seguridad (puedes usar las de prueba inicialmente)
4. Selecciona la ubicación (la misma que Firestore)

### Estructura de Carpetas

```
storage/
├── categorias/
│   ├── 1234567890.jpg
│   ├── 1234567891.jpg
│   └── ...
└── productos/
    ├── img_001.jpg
    ├── img_002.jpg
    └── ...
```

**Carpetas que se crearán automáticamente**:
- **`categorias/`**: Imágenes de categorías (se crean al agregar una categoría con imagen)
- **`productos/`**: Imágenes de productos (se crearán cuando implementes la subida de imágenes)

**Formato de nombres**:
- Las imágenes se nombran con timestamp: `{timestamp}.jpg`
- Ejemplo: `1705334567890.jpg`

---

## 5. Reglas de Seguridad

### Reglas de Firestore (Modo Desarrollo)

Para desarrollo, puedes usar estas reglas que permiten lectura/escritura:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Permitir lectura/escritura a todos (SOLO PARA DESARROLLO)
    match /{document=**} {
      allow read, write: if true;
    }
  }
}
```

### Reglas de Firestore (Modo Producción - Recomendado)

Para producción, usa reglas más seguras:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // Reglas para usuarios
    match /usuarios/{userId} {
      // Solo el usuario puede leer y escribir sus propios datos
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Reglas para categorías
    match /categorias/{categoryId} {
      // Todos pueden leer, solo usuarios autenticados pueden escribir
      allow read: if true;
      allow write: if request.auth != null;
    }
    
    // Reglas para productos
    match /productos/{productId} {
      // Todos pueden leer, solo usuarios autenticados pueden escribir
      allow read: if true;
      allow write: if request.auth != null;
    }
    
    // Reglas para especificaciones
    match /especificaciones/{specId} {
      allow read: if true;
      allow write: if request.auth != null;
    }
  }
}
```

**Cómo aplicar las reglas**:
1. Ve a **Firestore Database** → **Reglas**
2. Copia y pega las reglas apropiadas
3. Haz clic en "Publicar"

---

### Reglas de Storage (Modo Desarrollo)

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read, write: if true;
    }
  }
}
```

### Reglas de Storage (Modo Producción - Recomendado)

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    
    // Reglas para imágenes de categorías
    match /categorias/{imageId} {
      allow read: if true;
      allow write: if request.auth != null 
                   && request.resource.size < 5 * 1024 * 1024  // Máximo 5MB
                   && request.resource.contentType.matches('image/.*');
    }
    
    // Reglas para imágenes de productos
    match /productos/{imageId} {
      allow read: if true;
      allow write: if request.auth != null 
                   && request.resource.size < 5 * 1024 * 1024  // Máximo 5MB
                   && request.resource.contentType.matches('image/.*');
    }
  }
}
```

**Cómo aplicar las reglas**:
1. Ve a **Storage** → **Reglas**
2. Copia y pega las reglas apropiadas
3. Haz clic en "Publicar"

---

## 6. Índices Compuestos (Opcional)

Si necesitas hacer consultas complejas, Firebase puede pedirte crear índices. Por ejemplo:

### Índice para buscar productos por categoría y ordenar por fecha
```
Colección: productos
Campos indexados:
  - idCategory (Ascendente)
  - fechaCreacion (Descendente)
```

**Cómo crear índices**:
1. Ve a **Firestore Database** → **Índices**
2. Haz clic en "Agregar índice"
3. Selecciona la colección y los campos
4. Guarda el índice

**Nota**: Firebase te sugerirá automáticamente crear índices cuando ejecutes consultas que los requieran.

---

## 📝 Checklist de Configuración

Usa esta lista para verificar que todo está configurado:

- [ ] Proyecto de Firebase creado
- [ ] App Android agregada con el paquete correcto
- [ ] Archivo `google-services.json` descargado y colocado en `app/`
- [ ] Firebase Authentication habilitado (Email/Password)
- [ ] Firestore Database creado
- [ ] Colección `usuarios` lista (se crea automáticamente al registrar usuarios)
- [ ] Colección `categorias` creada
- [ ] Colección `productos` creada
- [ ] Colección `especificaciones` creada (opcional)
- [ ] Firebase Storage habilitado
- [ ] Reglas de seguridad de Firestore configuradas
- [ ] Reglas de seguridad de Storage configuradas

---

## 🚀 Prueba tu Configuración

### 1. Probar Registro de Usuario
1. Ejecuta la app en tu dispositivo/emulador
2. Ve a la pantalla de registro
3. Completa el formulario:
   - Nombre: "Juan"
   - Apellido: "Pérez"
   - Género: Selecciona "Hombre" o "Mujer"
   - Email: "test@example.com"
   - Password: "123456"
4. Haz clic en "Registrarse"
5. Verifica en Firebase Console:
   - **Authentication**: Debe aparecer el usuario con el email
   - **Firestore** → `usuarios`: Debe aparecer un documento con los datos

### 2. Probar Agregar Categoría
1. Inicia sesión en la app
2. Ve a la pantalla de agregar categoría
3. Completa el formulario:
   - Nombre: "Electrónica"
   - Descripción: "Productos electrónicos"
   - Imagen: Selecciona una imagen
4. Guarda la categoría
5. Verifica en Firebase Console:
   - **Firestore** → `categorias`: Debe aparecer la categoría
   - **Storage** → `categorias/`: Debe aparecer la imagen

### 3. Probar Agregar Producto
1. Ve a la pantalla de agregar producto
2. Completa el formulario con datos de prueba
3. Guarda el producto
4. Verifica en Firebase Console:
   - **Firestore** → `productos`: Debe aparecer el producto

---

## ⚠️ Problemas Comunes y Soluciones

### Error: "Permission denied"
**Causa**: Las reglas de seguridad están bloqueando la operación.
**Solución**: Verifica que las reglas de Firestore/Storage permitan la operación. En desarrollo, usa las reglas permisivas.

### Error: "Object does not exist at location"
**Causa**: La imagen no se subió correctamente a Storage.
**Solución**: 
- Verifica que Storage esté habilitado
- Verifica las reglas de Storage
- Asegúrate de que la app tenga permisos para acceder a las imágenes

### Error: "The email address is badly formatted"
**Causa**: El formato del email no es válido.
**Solución**: Asegúrate de ingresar un email válido (ej: usuario@dominio.com)

### Error: "The password must be 6 characters long or more"
**Causa**: Firebase requiere contraseñas de al menos 6 caracteres.
**Solución**: Usa contraseñas más largas.

---

## 📚 Recursos Adicionales

- [Documentación de Firebase](https://firebase.google.com/docs)
- [Firestore Data Model](https://firebase.google.com/docs/firestore/data-model)
- [Firebase Authentication](https://firebase.google.com/docs/auth)
- [Firebase Storage](https://firebase.google.com/docs/storage)
- [Security Rules](https://firebase.google.com/docs/rules)

---

## 🔄 Próximos Pasos

1. **Implementar subida de imágenes para productos**: Actualmente solo las categorías tienen imágenes
2. **Implementar variaciones y especificaciones**: Las colecciones están definidas pero no se usan
3. **Agregar validaciones**: Validar datos antes de guardar en Firebase
4. **Implementar consultas**: Listar productos, categorías, etc.
5. **Mejorar seguridad**: Eliminar el campo `password` de la colección `usuarios`

---

**¡Configuración completada! 🎉**

Si tienes algún problema, revisa la sección de "Problemas Comunes" o consulta la documentación oficial de Firebase.