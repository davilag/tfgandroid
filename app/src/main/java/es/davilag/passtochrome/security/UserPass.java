package es.davilag.passtochrome.security;

/**
 * Created by davilag on 9/5/15.
 */
public class UserPass {
    private String user;
    private String password;

    public UserPass(String user, String password){
        this.user = user;
        this.password = password;
    }
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
