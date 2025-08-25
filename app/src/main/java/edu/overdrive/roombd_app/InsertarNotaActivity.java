package edu.overdrive.roombd_app;

import static edu.overdrive.roombd_app.Constantes.*;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import edu.overdrive.roombd_app.basedatos.BasedatosNota;
import edu.overdrive.roombd_app.entidades.Nota;

public class InsertarNotaActivity extends AppCompatActivity {

    private TextView et_titulo, et_contenido;
    private Button btn_Guardar;
    private BasedatosNota basedatosNota;
    private Nota nota;
    private boolean update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertar_nota);

        et_titulo = findViewById(R.id.et_titulo);
        et_contenido = findViewById(R.id.et_contenido);
        btn_Guardar = findViewById(R.id.btn_guardar);

        //Barra de navegación
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        basedatosNota = BasedatosNota.getInstance(this);

        nota = (Nota) getIntent().getSerializableExtra(NOTA);


        // Si nota es null es una nota nueva, sino es una nota para actualizar
        if (nota != null) {
            getSupportActionBar().setTitle(R.string.editar_nota);
            update = true;
            btn_Guardar.setText(R.string.modificar);

            et_titulo.setText(nota.getTitulo());
            et_contenido.setText(nota.getContenido());
        } else {
            getSupportActionBar().setTitle(R.string.insertar_nota);
            update = false;
            btn_Guardar.setText(R.string.guardar);

        }

        btn_Guardar.setOnClickListener(view -> {
            String titulo = et_titulo.getText().toString();
            String contenido = et_contenido.getText().toString();

            if (update) {
                nota.setTitulo(titulo);
                nota.setContenido(contenido);

                BasedatosNota.servicioExecutor.execute(() -> {
                    basedatosNota.notaDao().actualizarTodas(nota);

                    //Volver a la actividad anterior
                    runOnUiThread(() -> {
                        setResult(nota, RESULT_UPDATED);
                    });
                });

            } else {
                nota = new Nota(titulo, contenido);
                BasedatosNota.servicioExecutor.execute(() -> {
                    long notaId = basedatosNota.notaDao().insertarNota(nota);
                    // Asignar el id autogenerado por Room al objeto
                    nota.setId_nota(notaId);

                    //Volver a la actividad anterior
                    runOnUiThread(() -> {
                         // Asignamos el id dado por Room
                        setResult(nota, RESULT_CREATED);
                    });
                });
            }
        });
    }

    private void setResult(Nota nota, int flag) {
        setResult(flag, new Intent().putExtra(NOTA, nota));
        finish();


    }
}