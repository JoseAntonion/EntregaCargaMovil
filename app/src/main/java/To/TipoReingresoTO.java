package To;

/**
 * Created by jmunozv on 06-09-2017.
 */

public class TipoReingresoTO {
    private String indice;
    private String descripcion;

    public TipoReingresoTO(String indice, String desc){
        this.indice = indice;
        this.descripcion = desc;
    }

    public String getIndice() {
        return indice;
    }

    public void setIndice(String indice) {
        this.indice = indice;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
