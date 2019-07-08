package intentservice.example.com.pruebagaribd.Bd;

import intentservice.example.com.pruebagaribd.Problema;

public interface Problemas {
    Problema elemento(int id);
    void anyade(Problema problema);
    int nuevo();
    void borrar(int id);
    int tamanyo();
    void actualiza(int id, Problema problema);
}
