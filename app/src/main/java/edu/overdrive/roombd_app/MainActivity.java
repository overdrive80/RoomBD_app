package edu.overdrive.roombd_app;

import static edu.overdrive.roombd_app.Constantes.*;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import edu.overdrive.roombd_app.adaptadores.AdaptadorNotas;
import edu.overdrive.roombd_app.basedatos.BasedatosNota;
import edu.overdrive.roombd_app.entidades.Nota;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdaptadorNotas adaptadorNotas;
    private BasedatosNota basedatosNota;
    private final List<Nota> listaNotas = new ArrayList<>();
    private int posicion;

    // ActivityResultLauncher para insertar/actualizar notas
    private ActivityResultLauncher<Intent> notaActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configurar el ActivityResultLauncher
        configurarActivityResultLauncher();

        //Vincular vistas
        inicializarVistas();

        //Configurar la lista con RecyclerView
        configurarLista();
    }

    // Configuramos la respuesta de las acciones
    private void configurarActivityResultLauncher() {
        notaActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent data = result.getData();
                int resultCode = result.getResultCode();

                // Si los campos estaban vacios no se guardan las notas
                if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(getApplicationContext(), R.string.campos_vacios, Toast.LENGTH_LONG).show();
                    return;
                }

                if (resultCode > 0 && data != null) {
                    Nota nota = (Nota) data.getSerializableExtra(NOTA);

                    // Nueva nota
                    if (resultCode == RESULT_CREATED) {
                        listaNotas.add(nota);
                        adaptadorNotas.notifyItemInserted(listaNotas.size());
                        recyclerView.smoothScrollToPosition(listaNotas.size());

                    } else if (resultCode == RESULT_UPDATED) {
                        // Actualizar nota
                        listaNotas.set(posicion, nota);
                        adaptadorNotas.notifyItemChanged(posicion);
                    }
                }
            }
        });
    }

    private void inicializarVistas() {
        recyclerView = findViewById(R.id.recyclerView);

        // Barra de navegación
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Boton flotante
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, InsertarNotaActivity.class);
            notaActivityResultLauncher.launch(intent);
        });
    }

    private void configurarLista() {
        basedatosNota = BasedatosNota.getInstance(this);

        // Poblar de datos la lista
        BasedatosNota.servicioExecutor.execute(() -> {
            List<Nota> notasFromDB = basedatosNota.notaDao().getTodas();

            // Ahora regresamos al hilo principal para tocar la UI
            runOnUiThread(() -> {
                // Usar ESTA lista, no crear una nueva
                listaNotas.clear();
                listaNotas.addAll(notasFromDB);

                AdaptadorNotas.OnNotaClickListener listener = new AdaptadorNotas.OnNotaClickListener() {
                    @Override
                    public void onNotaClick(int position) {
                        //Guardamos la posicion del item pulsado
                        MainActivity.this.posicion = position;
                        generarDialogo();
                    }
                };

                adaptadorNotas = new AdaptadorNotas(listaNotas, this, listener);
                recyclerView.setAdapter(adaptadorNotas);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
            });
        });
    }

    private void generarDialogo() {
        // AlertDialog
        new AlertDialog.Builder(MainActivity.this).setItems(new String[]{getString(R.string.borrar), getString(R.string.modificar)}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Nota notaSeleccion = listaNotas.get(posicion);

                if (i == 0) {
                    // Borrar
                    borrarNota(notaSeleccion);
                } else if (i == 1) {
                    // Actualizar
                    actualizarNota(notaSeleccion);
                }
            }
        }).show();
    }

    private void actualizarNota(Nota notaSeleccion) {
        Intent intent = new Intent(MainActivity.this, InsertarNotaActivity.class);
        intent.putExtra(NOTA, notaSeleccion);
        notaActivityResultLauncher.launch(intent);
    }

    private void borrarNota(Nota notaBorrar) {
        basedatosNota.notaDao().borrarTodas(notaBorrar);

        listaNotas.remove(posicion);
        adaptadorNotas.notifyItemRemoved(posicion);
        adaptadorNotas.notifyItemRangeChanged(posicion, listaNotas.size());
    }
}