package co.com.registraduria.security.Modelo;
import org.springframework.data.annotation.Id;

public class Rol {
    @Id
    private String _id;
    private String nombre;
    private String descripcion;

    public Rol( String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String get_id() {
        return _id;
    }
}
