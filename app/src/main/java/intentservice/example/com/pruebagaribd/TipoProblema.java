package intentservice.example.com.pruebagaribd;

public enum TipoProblema {

    NOISE("Noise"),
    MANUFACTURE("Manufactured mistake"),
    DELIVERED("Not delivered"),
    CONSTRUCTION("Building construction mistake");

    private final String texto;

    TipoProblema(String texto){
        this.texto=texto;
    }

    public String getTexto(){return texto;}

    public static String[] getNombres(){
        String[] resultado= new String[TipoProblema.values().length];
        for (TipoProblema tipo: TipoProblema.values()){
            resultado[tipo.ordinal()]=tipo.texto;
        }
        return resultado;
    }

}
