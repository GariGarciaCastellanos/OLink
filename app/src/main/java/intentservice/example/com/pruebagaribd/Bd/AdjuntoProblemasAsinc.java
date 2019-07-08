package intentservice.example.com.pruebagaribd.Bd;

import intentservice.example.com.pruebagaribd.AdjuntoProblema;
import intentservice.example.com.pruebagaribd.Problema;

public interface AdjuntoProblemasAsinc {

    interface EscuchadorElemento{
        void onRespuesta(AdjuntoProblema adjuntoProblema);
    }

    interface EscuchadorTamanyo{
        void onRespuesta(long tamanyo);
    }

    void elemento(String id, ProblemasAsinc.EscuchadorElemento escuchador);
    void anyade(AdjuntoProblema adjuntoProblema);
    String nuevo();
    void borrar(String id);
    void actualiza(String id, AdjuntoProblema adjuntoProblema);
    void tamanyo(ProblemasAsinc.EscuchadorTamanyo escuchador);

}
