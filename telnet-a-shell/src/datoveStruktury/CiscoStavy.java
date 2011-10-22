package datoveStruktury;

/**
 * Tento enum obsahuje vsechny podporovane stavy, ve kterych se muze nachazet Cisco pocitac.
 * @author Stanislav Řehák
 */
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