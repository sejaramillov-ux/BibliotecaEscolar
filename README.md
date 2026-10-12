# Sistema de Gestión de Biblioteca Escolar - EFT Desarrollo Orientado a Objetos II

Aplicación de escritorio en Java (Swing) con MySQL (JDBC) para gestionar libros,
estudiantes, préstamos y devoluciones de una biblioteca escolar.

## Funcionalidades
- **Autenticación** con roles: *bibliotecario* (gestión total) y *estudiante* (préstamo, devolución y consulta).
- **Gestión de libros**: registrar, modificar, eliminar y listar (con categoría y stock).
- **Gestión de estudiantes**: registrar, modificar, eliminar y listar.
- **Préstamos y devoluciones**: verificación de stock, fecha de vencimiento automática (7 días),
  detección y constancia de atrasos.
- **Reportes**: libros más prestados, historial por estudiante y libros actualmente en préstamo.

## Patrones y buenas prácticas
- **MVC**: `model` (datos), `view` (JFrame/JPanel), `controller` (intermediarios).
- **DAO**: un DAO por entidad, todos implementan la interfaz genérica `CrudDAO<T>`.
- **Singleton**: `util.DatabaseConnection.getInstance()` entrega una única conexión.
- **POO**: clase abstracta `Persona` heredada por `Usuario` y `Estudiante` (polimorfismo con `getTipo()`).

## Concurrencia
- El préstamo se registra en un **hilo independiente** (`service.PrestamoTask`), sin congelar la interfaz.
- `service.PrestamoService` usa métodos **synchronized** y transacciones para mantener consistente el stock.
- Botón *"Simular 3 préstamos simultáneos"* para probar el control de stock con varios hilos.

## Estructura
```
src/
├── app/         Main
├── model/       Persona (abstracta), Usuario, Estudiante, Libro, Categoria, Prestamo
├── dao/         CrudDAO (interfaz), UsuarioDAO, EstudianteDAO, LibroDAO, CategoriaDAO, PrestamoDAO
├── controller/  AuthController, LibroController, EstudianteController, PrestamoController
├── service/     PrestamoService (synchronized), PrestamoTask (hilo)
├── view/        LoginFrame, MainFrame, paneles de cada módulo
└── util/        DatabaseConnection (Singleton), Mensajes, Validador
sql/             Scripts de creación y poblado
lib/             Conector MySQL (JDBC)
```

## Ejecución
1. Ejecutar en MySQL los scripts `sql/Script_crea_tablas_biblioteca.sql` y `sql/Script_poblado_tablas_biblioteca.sql`.
2. Abrir el proyecto en IntelliJ IDEA y verificar el conector en `lib/`.
3. Ajustar usuario/clave de MySQL en `util/DatabaseConnection.java` si es necesario.
4. Ejecutar `app.Main` e iniciar sesión con un usuario del script de poblado (correo o RUT + contraseña).
