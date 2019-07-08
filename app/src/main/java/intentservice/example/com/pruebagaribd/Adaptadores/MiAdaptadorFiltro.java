package intentservice.example.com.pruebagaribd.Adaptadores;

import android.content.Context;
import android.database.Cursor;

import java.util.List;

import intentservice.example.com.pruebagaribd.Bd.Problemas;
import intentservice.example.com.pruebagaribd.Problema;

public class MiAdaptadorFiltro extends MiAdaptador {

    private List<Problema> listaSinFiltro;
    private List<Integer> indiceFiltro;
    private Cursor cursorSinFiltro;

    private String busqueda="";
    private String filtro="";

    public MiAdaptadorFiltro(Context contexto, Problemas problemas, Cursor cursor) {
        super(contexto, problemas, cursor);
        cursorSinFiltro=cursor;
        recalculaFiltro();
    }

    public void setBusqueda(String busqueda){
        this.busqueda=busqueda.toLowerCase();
        recalculaFiltro();
    }

    public void setFiltro(String filtro){
        this.filtro=filtro.toLowerCase();
        recalculaFiltro();
    }

    public void recalculaFiltro(){

    }

}

