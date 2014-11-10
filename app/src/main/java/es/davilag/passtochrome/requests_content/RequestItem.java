package es.davilag.passtochrome.requests_content;

/**
 * Created by davilag on 5/11/14.
 */
public class RequestItem {
    private String dominio;
    private String reqId;

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    public RequestItem(String dominio,String reqId) {
        this.reqId = reqId;
        this.dominio = dominio;
    }

    public String getDominio() {
        return dominio;
    }

    public void setDominio(String dominio) {
        this.dominio = dominio;
    }
}
