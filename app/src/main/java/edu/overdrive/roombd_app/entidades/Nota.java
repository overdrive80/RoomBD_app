package edu.overdrive.roombd_app.entidades;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import edu.overdrive.roombd_app.Constantes;

@Entity(tableName = Constantes.TABLA_NOTAS)
public class Nota implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id_nota;

    @ColumnInfo(name="contenido_nota")
    private String contenido;

    private String titulo;
    private Date fecha;

    @Ignore
    public Nota(){}

    public Nota(String titulo, String contenido) {
        this.contenido = contenido;
        this.titulo = titulo;
        this.fecha = new Date(System.currentTimeMillis());
    }

    // GETTERS-SETTERS
    public long getId_nota() {
        return id_nota;
    }

    public void setId_nota(long id_nota) {
        this.id_nota = id_nota;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Nota)) return false;
        Nota nota = (Nota) o;

        // Si el id ya está asignado, usar solo id
        if (id_nota != 0 && nota.id_nota != 0) {
            return id_nota == nota.id_nota;
        }

        // Si al menos una no tiene ID, comparar por contenido
        boolean fechasIguales = (fecha == null && nota.fecha == null) ||
                (fecha != null && nota.fecha != null &&
                        fecha.getTime() == nota.fecha.getTime());

        return Objects.equals(titulo, nota.titulo) &&
                Objects.equals(contenido, nota.contenido) &&
                fechasIguales;
    }

    @Override
    public int hashCode() {
        if (id_nota != 0) {
            return Objects.hash(id_nota);
        }

        // Incluir el timestamp de la fecha en lugar del objeto Date
        long fechaMillis = fecha != null ? fecha.getTime() : 0;
        return Objects.hash(titulo, contenido, fechaMillis);
    }

    @Override
    public String toString() {
        return "Nota{" +
                "id_nota=" + id_nota +
                ", contenido='" + contenido + '\'' +
                ", titulo='" + titulo + '\'' +
                ", fecha=" + fecha +
                '}';
    }
}
