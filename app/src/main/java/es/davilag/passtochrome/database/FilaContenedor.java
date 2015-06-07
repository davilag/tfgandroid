package es.davilag.passtochrome.database;

/**
 * Created by davilag on 4/12/14.
 */
public class FilaContenedor {
    private String usuario;
    private String pass;
    private String dom;
    private String iv;

    public FilaContenedor(String usuario, String pass, String dom,String iv) {
        this.usuario = usuario;
        this.pass = pass;
        this.dom = dom;
        this.iv = iv;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getPass() {
        return pass;
    }

    public String getDom() {
        return dom;
    }

    public String getIv() {
        return iv;
    }
}
