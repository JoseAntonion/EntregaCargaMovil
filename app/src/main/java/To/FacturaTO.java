package To;

/**
 * Created by jmunozv on 27-09-2017.
 */

public class FacturaTO {

    private String rutFactura;
    private String razonFactura;
    private String direccionFactura;
    private String fonoFactura;
    private String giroFactura;
    private int comunaFactura;
    private String descComuna;
    private int ciudadFactura;
    private String descCiudad;
    private int totalFactura;

    public FacturaTO(String rutFactura, String razonFactura, String direccionFactura, String fonoFactura, String giroFactura, int comunaFactura, String descComuna, int ciudadFactura,
                     String descCiudad, int totalFactura) {
        this.rutFactura = rutFactura;
        this.razonFactura = razonFactura;
        this.direccionFactura = direccionFactura;
        this.fonoFactura = fonoFactura;
        this.giroFactura = giroFactura;
        this.comunaFactura = comunaFactura;
        this.descComuna = descComuna;
        this.ciudadFactura = ciudadFactura;
        this.descCiudad = descCiudad;
        this.totalFactura = totalFactura;
    }

    public FacturaTO() {
    }

    public String getRutFactura() {
        return rutFactura;
    }

    public void setRutFactura(String rutFactura) {
        this.rutFactura = rutFactura;
    }

    public String getRazonFactura() {
        return razonFactura;
    }

    public void setRazonFactura(String razonFactura) {
        this.razonFactura = razonFactura;
    }

    public String getDireccionFactura() {
        return direccionFactura;
    }

    public void setDireccionFactura(String direccionFactura) {
        this.direccionFactura = direccionFactura;
    }

    public String getFonoFactura() {
        return fonoFactura;
    }

    public void setFonoFactura(String fonoFactura) {
        this.fonoFactura = fonoFactura;
    }

    public String getGiroFactura() {
        return giroFactura;
    }

    public void setGiroFactura(String giroFactura) {
        this.giroFactura = giroFactura;
    }

    public int getComunaFactura() {
        return comunaFactura;
    }

    public void setComunaFactura(int comunaFactura) {
        this.comunaFactura = comunaFactura;
    }

    public int getCiudadFactura() {
        return ciudadFactura;
    }

    public void setCiudadFactura(int cuidadFactura) {
        this.ciudadFactura = cuidadFactura;
    }

    public int getTotalFactura() {
        return totalFactura;
    }

    public void setTotalFactura(int totalFactura) {
        this.totalFactura = totalFactura;
    }

    public String getDescComuna() {
        return descComuna;
    }

    public void setDescComuna(String descComuna) {
        this.descComuna = descComuna;
    }

    public String getDescCiudad() {
        return descCiudad;
    }

    public void setDescCiudad(String descCiudad) {
        this.descCiudad = descCiudad;
    }
}
