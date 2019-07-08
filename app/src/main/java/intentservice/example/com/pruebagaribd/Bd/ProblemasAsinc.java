package intentservice.example.com.pruebagaribd.Bd;

import intentservice.example.com.pruebagaribd.Problema;

public interface ProblemasAsinc {

    interface EscuchadorElemento{
        void onRespuesta(Problema problema);
    }

    interface EscuchadorTamanyo{
        void onRespuesta(long tamanyo);
    }

    void elemento(String id, EscuchadorElemento escuchador);
    void anyade(Problema problema);
    String nuevo();
    void borrar(String id);
    void actualiza(String id, Problema problema);
    void tamanyo(EscuchadorTamanyo escuchador);
}
