package co.udes.castellanos.tareas.exception;

/**
 * Excepción lanzada cuando una tarea no existe en la base de datos.
 * Mapeada a HTTP 404 en el manejador global de excepciones.
 */
public class TareaNotFoundException extends RuntimeException {

    public TareaNotFoundException(Long id) {
        super("Tarea no encontrada: " + id);
    }

    public TareaNotFoundException(String mensaje) {
        super(mensaje);
    }
}
