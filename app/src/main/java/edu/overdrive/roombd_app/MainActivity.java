package edu.overdrive.roombd_app;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.overdrive.roombd_app.adaptadores.AdaptadorNotas;
import edu.overdrive.roombd_app.basedatos.BasedatosNota;
import edu.overdrive.roombd_app.entidades.Nota;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView tvTitulo;
    private RecyclerView recyclerView;
    private AdaptadorNotas adaptadorNotas;
    private BasedatosNota basedatosNota;
    private List<Nota> listaNotas;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        inicializarVistas();
        configurarLista();
    }

    private void configurarLista() {

        basedatosNota = BasedatosNota.getInstance(this);

        BasedatosNota.servicioExecutor.execute(() -> {
            listaNotas = basedatosNota.notaDao().getTodas();

            // Ahora regresamos al hilo principal para tocar la UI
            runOnUiThread(() -> {
                AdaptadorNotas.OnNotaClickListener listener = new AdaptadorNotas.OnNotaClickListener() {
                    @Override
                    public void onNotaClick(int position) {
                        Nota notaSeleccionada = listaNotas.get(position);
                        Toast.makeText(MainActivity.this, "Click en: " + notaSeleccionada.getTitulo(), Toast.LENGTH_SHORT).show();
                    }
                };

                adaptadorNotas = new AdaptadorNotas(listaNotas, this, listener);
                recyclerView.setAdapter(adaptadorNotas);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
            });
        });
    }

    private void inicializarVistas() {
        tvTitulo = findViewById(R.id.tv_titulo);
        recyclerView = findViewById(R.id.recyclerView);

    }
}