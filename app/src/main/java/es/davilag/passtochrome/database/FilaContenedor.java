package es.davilag.passtochrome.database;

/**
 * Created by davilag on 4/12/14.
 */
public class FilaContenedor {
    private String usuario;
    private String pass;
    private String dom;

    public FilaContenedor(String usuario, String pass, String dom) {
        this.usuario = usuario;
        this.pass = pass;
        this.dom = dom;
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
}
