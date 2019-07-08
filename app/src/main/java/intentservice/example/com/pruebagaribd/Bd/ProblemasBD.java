package intentservice.example.com.pruebagaribd.Bd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import intentservice.example.com.pruebagaribd.Problema;
import intentservice.example.com.pruebagaribd.TipoProblema;

public class ProblemasBD extends SQLiteOpenHelper implements Problemas {
    Context contexto;

    public ProblemasBD(Context contexto) {
        super(contexto, "problemas", null, 1);
        this.contexto = contexto;
    }

    @Override
    public void onCreate(SQLiteDatabase bd) {
        bd.execSQL("CREATE TABLE problemas (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT, " +
                "descripcion TEXT, " +
                "tipo INTEGER, " +
                "codigoAscensor TEXT, " +
                "latitud REAL, " +
                "longitud REAL, " +
                "importancia REAL, " +
                "fecha BIGINT, " +
                "foto TEXT, " +
                "grabacionAceleracion TEXT, " +
                "audio TEXT, " +
                "video TEXT)");
        bd.execSQL("INSERT INTO problemas VALUES (null, " +
                "'Maquina parada', " +
                "'La maquina se ha parado', " +
                TipoProblema.MANUFACTURE.ordinal() +
                ",'EXFR', 43.2918111, -1.9885133, 5.0, " +
                System.currentTimeMillis() + ", '', " + "'', " + "''," + "'' " + ")");
        bd.execSQL("INSERT INTO problemas VALUES (null, " +
                "'Ruido desagradable', " + "'El ascensor hace mucho ruido', " + TipoProblema.NOISE.ordinal() +
                ", 'EXFR', 39.481106, -0.340987, 4.0, " + System.currentTimeMillis() + ", '', " + "'', " + "''," + "'' " + ")");
        bd.execSQL("INSERT INTO problemas VALUES (null, " +
                "'Maquina parada', " + "'Cortocircuito', " + TipoProblema.MANUFACTURE.ordinal() +
                ", 'EXFR', 41.481106, -1.340987, 4.0, " + System.currentTimeMillis() + ", '', " + "'', " + "''," + "'' " + ")");
        bd.execSQL("INSERT INTO problemas VALUES (null, " +
                "'Error fabricacion', " + "'La cabina esta dañada.', " + TipoProblema.MANUFACTURE.ordinal() +
                ", 'EXFR', 45.481106, -0.750987, 4.0, " + System.currentTimeMillis() + ", '', " + "'', " + "''," + "'' " + ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static Problema extraeProblema(Cursor cursor) {
        Problema problema = new Problema();
        problema.setNombre(cursor.getString(1));
        problema.setDescripcion(cursor.getString(2));
        problema.setTipo(TipoProblema.values()[cursor.getInt(3)]);
        problema.setCodigoAscensor(cursor.getString(4));
        problema.setLatitud(cursor.getDouble(5));
        problema.setLongitud(cursor.getDouble(6));
        problema.setImportancia(cursor.getFloat(7));
        problema.setFecha(cursor.getLong(8));
//        problema.setFoto(cursor.getString(9));
//        problema.setGrabacionAceleracion(cursor.getString(10));
//        problema.setAudio(cursor.getString(11));
//        problema.setVideo(cursor.getString(12));
        return problema;
    }

    public Cursor extraeCursor() {
        String consulta = "SELECT * FROM problemas";
        SQLiteDatabase bd = getReadableDatabase();
        return bd.rawQuery(consulta, null);
    }

    public Cursor extraeCursorFiltroTipo(int filtro) {
        String consulta = "SELECT * FROM problemas WHERE tipo = " + filtro;
        SQLiteDatabase bd = getReadableDatabase();
        return bd.rawQuery(consulta, null);
    }

    public Cursor extraeCursorFiltroBusqueda(String busqueda) {
        String consulta = "SELECT * FROM problemas WHERE descripcion = " + busqueda;
        SQLiteDatabase bd = getReadableDatabase();
        return bd.rawQuery(consulta, null);
    }

    @Override
    public Problema elemento(int id) {
        Problema problema = null;
        SQLiteDatabase bd = getReadableDatabase();
        Cursor cursor = bd.rawQuery("SELECT * FROM problemas WHERE _id = " + id, null);
        if (cursor.moveToNext()) {
            problema = extraeProblema(cursor);
        }
        return problema;
    }

    @Override
    public void anyade(Problema problema) {
        SQLiteDatabase bd = getWritableDatabase();
        bd.execSQL("INSERT INTO problemas VALUES(null,"
                + problema.getNombre() + ", "
                + problema.getDescripcion() + ", "
                + problema.getTipo().ordinal() + ", "
                + problema.getCodigoAscensor() + ", "
                + problema.getLatitud() + ", "
                + problema.getLongitud() + ", "
                + problema.getImportancia() + ", " + problema.getFecha() +
//                        ", "
//                + problema.getFoto() + ", "
//                + problema.getGrabacionAceleracion() + ", "
//                + problema.getAudio() + ", "
//                + problema.getVideo() +
                ")");
    }

    @Override
    public int nuevo() {
        int _id = -1;
        Problema problema = new Problema();
        SQLiteDatabase bd = getWritableDatabase();
        bd.execSQL("INSERT INTO problemas (fecha,foto,grabacionAceleracion,audio,video) " + "VALUES ( " +
                problema.getFecha() + ",'','','','')");
        Cursor c = bd.rawQuery("SELECT _id FROM problemas WHERE fecha = " +
                problema.getFecha(), null);
        if (c.moveToNext()) {
            _id = c.getInt(0);
        }
        c.close();
        bd.close();
        return _id;
    }

    @Override
    public void borrar(int id) {
        SQLiteDatabase bd = getWritableDatabase();
        bd.execSQL("DELETE FROM problemas WHERE _id= " + id);
        bd.close();
    }

    @Override
    public int tamanyo() {
        SQLiteDatabase bd = getReadableDatabase();
        Cursor cursor = bd.rawQuery("SELECT * FROM problemas", null);
        return cursor.getCount();
    }

    @Override
    public void actualiza(int id, Problema problema) {
        SQLiteDatabase bd = getWritableDatabase();
        ContentValues data = new ContentValues();
        data.put("nombre", problema.getNombre());
        data.put("descripcion", problema.getDescripcion());
        data.put("tipo", problema.getTipo().ordinal());
        data.put("codigoAscensor", problema.getCodigoAscensor());
        data.put("latitud", problema.getLatitud());
        data.put("longitud", problema.getLongitud());
        data.put("importancia", problema.getImportancia());
        data.put("fecha", problema.getFecha());
//        data.put("foto", problema.getFoto());
//        data.put("grabacionAceleracion", problema.getGrabacionAceleracion());
//        data.put("audio", problema.getAudio());
//        data.put("video", problema.getVideo());
        bd.update("problemas", data, "_id=" + id, null);
        bd.close();
    }
}
