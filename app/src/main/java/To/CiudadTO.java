package To;

/**
 * Created by jmunozv on 21-09-2017.
 */

public class CiudadTO {

    private String codCiudad;
    private String nombreCiudad;

    public CiudadTO(String codCiudad, String nombreCiudad) {
        this.codCiudad = codCiudad;
        this.nombreCiudad = nombreCiudad;
    }

    public CiudadTO() {
    }

    public String getCodCiudad() {
        return codCiudad;
    }

    public void setCodCiudad(String codCiudad) {
        this.codCiudad = codCiudad;
    }

    public String getNombreCiudad() {
        return nombreCiudad;
    }

    public void setNombreCiudad(String nombreCiudad) {
        this.nombreCiudad = nombreCiudad;
    }

    @Override
    public String toString() {
        return nombreCiudad;
    }
}
