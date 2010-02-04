package datoveStruktury;

public enum CiscoStavy {

        USER("user"),
        ROOT("root"),
        CONFIG("config"),
        IFACE("config-if");
        private String term;

        private CiscoStavy(String term) {
            this.term = term;
        }
    }