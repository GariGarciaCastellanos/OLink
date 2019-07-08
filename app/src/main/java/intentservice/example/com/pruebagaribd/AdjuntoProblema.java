package intentservice.example.com.pruebagaribd;

public class AdjuntoProblema {

    String nombre;
    String url;
    String tipo;
    Long idTiempo;
    String problemaId;
    String refArchivo;

    public String getRefArchivo() {
        return refArchivo;
    }

    public void setRefArchivo(String refArchivo) {
        this.refArchivo = refArchivo;
    }

    public Long getIdTiempo() {
        return idTiempo;
    }

    public void setIdTiempo(Long idTiempo) {
        this.idTiempo = idTiempo;
    }

    public String getProblemaId() {
        return problemaId;
    }

    public void setProblemaId(String problemaId) {
        this.problemaId = problemaId;
    }

    public AdjuntoProblema() {idTiempo = System.currentTimeMillis();    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
