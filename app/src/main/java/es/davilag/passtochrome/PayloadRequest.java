package es.davilag.passtochrome;

public class PayloadRequest {
        private String dominio;
        private long nonce;
        private long ts;
        private String action;
        public String getDominio() {
            return dominio;
        }
        public void setDominio(String dominio) {
            this.dominio = dominio;
        }
        public long getNonce() {
            return nonce;
        }
        public void setNonce(long nonce) {
            this.nonce = nonce;
        }
        public long getTs() {
            return ts;
        }
        public void setTs(long ts) {
            this.ts = ts;
        }
        public String getAction() {
            return action;
        }
        public void setAction(String action) {
            this.action = action;
        }
}
