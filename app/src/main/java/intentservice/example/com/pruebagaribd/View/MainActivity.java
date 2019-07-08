package intentservice.example.com.pruebagaribd.View;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import intentservice.example.com.pruebagaribd.Bd.ProblemasAsinc;
import intentservice.example.com.pruebagaribd.Bd.UsuariosFirestore;
import intentservice.example.com.pruebagaribd.PermisosUtilidades;
import intentservice.example.com.pruebagaribd.R;
import intentservice.example.com.pruebagaribd.Usuario;

public class MainActivity extends AppCompatActivity {

    //Si borro un problema, no "guarda" el id.
    //Al sacar foto, genero un Uri, pero no aparece la foto.
    //Al grabar Audio y volver a VistaLugar, no se actualiza. Actualizar
    //Al grabar video pasa igual que la foto. Luego no se puede ver.
    //Poner alguna notificacion.
    //Creo que el Thread no me funciona, porque sólo me guarda un dato

    //Conseguir que me pregunte por los permisos todos a la vez.

    Button verProblemas, contactarTecnicalSupport, verMapa;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int SOLICITUD_PERMISO_LOCALIZACION = 4;

    public static String _idUsuario;
    public static UsuariosFirestore usuarios;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _idUsuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
        usuarios = new UsuariosFirestore();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("usuarios").document(_idUsuario);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult().exists()) {
                    Usuario usuario = task.getResult().toObject(Usuario.class);
                    if ((usuario.getNombre() == null) || (usuario.getDelegacion()==null) || (usuario.getnTelefono()==0)){
                        Snackbar.make(findViewById(android.R.id.content),"Tienes datos de Usuario no rellenados.",Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });

        verProblemas = (Button) findViewById(R.id.verProblemas);
        verProblemas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, RVProblemas.class);
                startActivity(i);
            }
        });

        contactarTecnicalSupport = (Button) findViewById(R.id.contactarTecnicalSupport);
        contactarTecnicalSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Cómo quiere ponerse en contacto con Technical support?")
                        .setCancelable(true)
                        .setPositiveButton("LLAMAR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                llamarTechnicalSupport();
                            }
                        })
                        .setNeutralButton("MAIL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                enviarMailTechnicalSupport();
                            }
                        })
                        .setNegativeButton("CANCELAR", null)
                        .show();
            }
        });

        verMapa = (Button) findViewById(R.id.verMapa);
        verMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapaActivity.class);
                startActivity(intent);
            }
        });

        Permisos();

    }

    public void Permisos() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.
                ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        } else {
            PermisosUtilidades.solicitarPermiso(Manifest.permission.ACCESS_FINE_LOCATION,
                    "Sin el permiso de localización no se puede añadir la localización del " +
                            "lugar del problema.", SOLICITUD_PERMISO_LOCALIZACION, this);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.
                RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
        } else {
            PermisosUtilidades.solicitarPermiso(Manifest.permission.RECORD_AUDIO,
                    "Sin el permiso de grabación de audio no se puede grabar ningún audio ",
                    REQUEST_RECORD_AUDIO_PERMISSION, this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainactivity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:

                return true;
            case R.id.datosUsuario:
                Intent i = new Intent(MainActivity.this, UsuarioActivity.class);
                startActivity(i);
                return true;
            case R.id.cerrarSesionUsuario:
                auth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void llamarTechnicalSupport() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:999888333")));
    }

    public void enviarMailTechnicalSupport() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"technicalsupport@prueba.com"});
        startActivity(i);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }
        if (requestCode == SOLICITUD_PERMISO_LOCALIZACION) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }
}
