package es.davilag.passtochrome.database;

/**
 * Created by davilag on 29/11/14.
 */
public class Request {
    private String reqId;
    private String dom;
    private String nonce;

    public String getReqId() {
        return reqId;
    }

    public String getDom() {
        return dom;
    }

    public Request(String reqId, String dom, String nonce) {
        this.reqId = reqId;
        this.dom = dom;
        this.nonce = nonce;
    }

    public String getNonce() {
        return nonce;
    }
}
