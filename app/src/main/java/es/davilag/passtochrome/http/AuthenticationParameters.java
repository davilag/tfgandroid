package es.davilag.passtochrome.http;

import java.io.InputStream;

/**
 * Authentication parameters, including client cert, server cert, user name, and password.
 */
public class AuthenticationParameters {
    private InputStream clientCertificate = null;
    private String clientCertificatePassword = null;
    private String caCertificate = null;

    public InputStream getClientCertificate() {
        return clientCertificate;
    }

    public void setClientCertificate(InputStream clientCertificate) {
        this.clientCertificate = clientCertificate;
    }

    public String getClientCertificatePassword() {
        return clientCertificatePassword;
    }

    public void setClientCertificatePassword(String clientCertificatePassword) {
        this.clientCertificatePassword = clientCertificatePassword;
    }

    public String getCaCertificate() {
        return caCertificate;
    }

    public void setCaCertificate(String caCertificate) {
        this.caCertificate = caCertificate;
    }
}
