package To;

/**
 * Created by jmunozv on 21-09-2017.
 */

public class ComunaTO {

    private String codCiudad;
    private String codComuna;
    private String nombreComuna;

    public ComunaTO() {
    }

    public ComunaTO(String codComuna, String nombreComuna) {
        //this.codCiudad = codCiudad;
        this.codComuna = codComuna;
        this.nombreComuna = nombreComuna;
    }

    public String getCodComuna() {
        return codComuna;
    }

    public void setCodComuna(String codComuna) {
        this.codComuna = codComuna;
    }

    public String getNombreComuna() {
        return nombreComuna;
    }

    public void setNombreComuna(String nombreComuna) {
        this.nombreComuna = nombreComuna;
    }

    public String getCodCiudad() {
        return codCiudad;
    }

    public void setCodCiudad(String codCiudad) {
        this.codCiudad = codCiudad;
    }

    @Override
    public String toString() {
        return nombreComuna;
    }
}
