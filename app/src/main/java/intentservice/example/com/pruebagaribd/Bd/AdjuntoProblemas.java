package intentservice.example.com.pruebagaribd.Bd;

import intentservice.example.com.pruebagaribd.AdjuntoProblema;

public interface AdjuntoProblemas {
    AdjuntoProblema elemento(int id);
    void anyade(AdjuntoProblema adjuntoProblema);
    int nuevo();
    void borrar(int id);
    int tamanyo();
    void actualiza(int id, AdjuntoProblema adjuntoProblema);
}
