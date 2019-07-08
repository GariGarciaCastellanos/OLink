package intentservice.example.com.pruebagaribd;

public class Problema {

    String nombre;
    String descripcion;
    TipoProblema tipo;
    String codigoAscensor;
    Double latitud;
    Double longitud;
    Float importancia;
    String creador;
    Long fecha;

    public Problema(String nombre,String descripcion,TipoProblema tipo,String codigoAscensor,Double latitud,Double longitud,
                    Float importancia,String creador){
        this.nombre=nombre;
        this.descripcion=descripcion;
        this.tipo=tipo;
        this.codigoAscensor=codigoAscensor;
        this.latitud=latitud;
        this.longitud=longitud;
        this.importancia=importancia;
        this.creador=creador;
        fecha = System.currentTimeMillis();
    }

    public Problema() {
        fecha = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Problema{" +
                "nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", tipo='" + tipo + '\'' +
                ", codigoAscensor='" + codigoAscensor + '\'' +
                ", latitud=" + latitud +
                ", longitud=" + longitud +
                ", importancia=" + importancia +
                ", fecha=" + fecha +
                '}';
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

    public String getCodigoAscensor() {
        return codigoAscensor;
    }

    public void setCodigoAscensor(String codigoAscensor) {
        this.codigoAscensor = codigoAscensor;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public Float getImportancia() {
        return importancia;
    }

    public void setImportancia(Float importancia) {
        this.importancia = importancia;
    }

    public Long getFecha() {
        return fecha;
    }

    public void setFecha(Long fecha) {
        this.fecha = fecha;
    }

    public void setTipo(TipoProblema tipo) {
        this.tipo=tipo;
    }

    public TipoProblema getTipo(){
        return tipo;
    }

    public String getCreador() {
        return creador;
    }

    public void setCreador(String creador) {
        this.creador = creador;
    }
}
