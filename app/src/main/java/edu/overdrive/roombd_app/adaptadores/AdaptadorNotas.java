package edu.overdrive.roombd_app.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.overdrive.roombd_app.R;
import edu.overdrive.roombd_app.entidades.Nota;

public class AdaptadorNotas extends RecyclerView.Adapter<AdaptadorNotas.ViewHolder> {

    private List<Nota> listaNotas;
    private Context contexto;
    LayoutInflater layoutInflater;
    private OnNotaClickListener listener;

    public AdaptadorNotas(List<Nota> listaNotas, Context contexto, OnNotaClickListener listener) {
        this.layoutInflater = LayoutInflater.from(contexto);
        this.listaNotas = listaNotas;
        this.contexto = contexto;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdaptadorNotas.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View vista = layoutInflater.inflate(R.layout.item_lista_notas, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorNotas.ViewHolder holder, int position) {
        holder.bind(listaNotas.get(position));
    }

    @Override
    public int getItemCount() {
        return listaNotas != null ? listaNotas.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo;
        TextView tvContenido;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitulo = itemView.findViewById(R.id.tv_titulo);
            tvContenido = itemView.findViewById(R.id.tvContenido);

            //Establecemos el listern
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getBindingAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        listener.onNotaClick(position);
                    }
                }
            });
        }

        //Vinculamos valores de BBDD con vistas
        public void bind(Nota nota) {
            tvTitulo.setText(nota.getTitulo());
            tvContenido.setText(nota.getContenido());

        }
    }

    public interface OnNotaClickListener {
        void onNotaClick(int position);
    }
}
