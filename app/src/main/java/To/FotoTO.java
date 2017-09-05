package To;

/**
 * Created by jmunozv on 01-09-2017.
 */

public class FotoTO {

    private String id;
    private byte[]foto;

    public byte[] getFoto() {
        return foto;
    }

    public String getId() {
        return id;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    public void setId(String id) {
        this.id = id;
    }
}
