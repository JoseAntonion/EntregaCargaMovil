package To;

public class ArchivoOdtPorPatenteTO {

    private String planilla;
    private String numeroODT;
    private String estadoODT;
    private String formaPago;
    private String codBarra;
    private int numeroPiezas;
    private int valorOdt;

    public int getValorOdt() {
        return valorOdt;
    }

    public void setValorOdt(int valorOdt) {
        this.valorOdt = valorOdt;
    }

    public String getPlanilla() {
        return planilla;
    }

    public void setPlanilla(String planilla) {
        this.planilla = planilla;
    }

    public String getNumeroODT() {
        return numeroODT;
    }

    public void setNumeroODT(String numeroODT) {
        this.numeroODT = numeroODT;
    }

    public String getEstadoODT() {
        return estadoODT;
    }

    public void setEstadoODT(String estadoODT) {
        this.estadoODT = estadoODT;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }

    public String getCodBarra() {
        return codBarra;
    }

    public void setCodBarra(String codBarra) {
        this.codBarra = codBarra;
    }

    public int getNumeroPiezas() {
        return numeroPiezas;
    }

    public void setNumeroPiezas(int numeroPiezas) {
        this.numeroPiezas = numeroPiezas;
    }
}
