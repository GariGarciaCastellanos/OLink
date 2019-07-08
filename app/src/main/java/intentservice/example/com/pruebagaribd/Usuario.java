package intentservice.example.com.pruebagaribd;

public class Usuario {
    String nombre;
    String correo;
    long nTelefono;
    String delegacion;
    long inicioSesion;

    public Usuario() {
    }

    public Usuario(String nombre, String correo, long nTelefono, String delegacion, long inicioSesion) {
        this.nombre = nombre;
        this.correo = correo;
        this.nTelefono=nTelefono;
        this.delegacion=delegacion;
        this.inicioSesion = inicioSesion;
    }

    public Usuario(String nombre, String correo, long nTelefono, String delegacion) {
        this(nombre, correo, nTelefono, delegacion, System.currentTimeMillis());
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public long getnTelefono() {
        return nTelefono;
    }

    public void setnTelefono(long nTelefono) {
        this.nTelefono = nTelefono;
    }

    public String getDelegacion() {
        return delegacion;
    }

    public void setDelegacion(String delegacion) {
        this.delegacion = delegacion;
    }

    public long getInicioSesion() {
        return inicioSesion;
    }

    public void setInicioSesion(long inicioSesion) {
        this.inicioSesion = inicioSesion;
    }
}
