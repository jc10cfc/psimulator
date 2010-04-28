/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package prikazy.cisco;

import java.util.List;
import pocitac.AbstraktniPocitac;
import pocitac.Konsole;
import prikazy.AbstraktniPrikaz;

/**
 *
 * @author haldyr
 */
public abstract class CiscoPrikaz extends AbstraktniPrikaz {

    /**
     * Rika, ze je to prikaz negovany - "no ..."
     */
    boolean no;
    /**
     * Co poslal uzivatel.
     */
    protected String radka = "";
    boolean debug = false;

    public CiscoPrikaz(AbstraktniPocitac pc, Konsole kon, List<String> slova) {
        super(pc, kon, slova);
        vyplnRadku();
    }

    public CiscoPrikaz(AbstraktniPocitac pc, Konsole kon, List<String> slova, boolean no) {
        super(pc, kon, slova);
        this.no = no;
        vyplnRadku();
    }

    /**
     * Vypise jmeno tridy: s pokud je nastaveno debug=true.
     * @param s
     */
    protected void ladici(String s) {
        System.out.println("ladim: "+debug);
        if (debug) {
            System.out.println(getClass().getName()+": " + s);
        }
    }

    private void vyplnRadku() {
        int i = 0;
        for (String s : slova) {
            if (i == 0) radka += s;
            else radka += " " + s;
            i++;
        }
    }

    /**
     * Zjisti, zda je rezetec prazdny.
     * Kdyz ano, tak to jeste vypise hlasku incompleteCommand.
     * @param s
     * @return
     */
    protected boolean jePrazdny(String s) {
        if (s.equals("")) {
            incompleteCommand();
            return true;
        }
        return false;
    }

    /**
     * Tato metoda simuluje zkracovani prikazu tak, jak cini cisco.
     * Metoda se take stara o vypisy typu: IncompleteCommand, AmbigiousCommand, InvalidInputDetected.
     * @param command prikaz, na ktery se zjistuje, zda lze na nej doplnit.
     * @param cmd prikaz, ktery zadal uzivatel
     * @param min kolik musi mit mozny prikaz znaku
     * @return Vrati true, pokud retezec cmd je jedinym moznym prikazem, na ktery ho lze doplnit.
     */
    protected boolean kontrola(String command, String cmd, int min) {

        if (cmd.length() == 0) {
            incompleteCommand();
            return false;
        }

        if (cmd.length() >= min && command.startsWith(cmd)) { // lze doplnit na jeden jedinecny prikaz
            return true;
        }

        if (command.startsWith(cmd)) {
            ambiguousCommand();
        } else {
            invalidInputDetected();
        }
        return false;
    }

    /**
     * Tato metoda simuluje zkracovani prikazu tak, jak cini cisco.
     * Metoda se take stara o vypis: AmbigiousCommand.
     * @param command prikaz, na ktery se zjistuje, zda lze na nej doplnit.
     * @param cmd prikaz, ktery zadal uzivatel
     * @param min kolik musi mit mozny prikaz znaku
     * @return Vrati true, pokud retezec cmd je jedinym moznym prikazem, na ktery ho lze doplnit.
     */
    protected boolean kontrolaBezVypisu(String command, String cmd, int min) {

        if (cmd.length() == 0) {
            return false;
        }

        if (cmd.length() >= min && command.startsWith(cmd)) { // lze doplnit na jeden jedinecny prikaz
            return true;
        }

        if (command.startsWith(cmd)) {
            ambiguousCommand();
        }
        return false;
    }

    /**
     * Zpracuje radek a vrati zda se ma pokracovat k vykonani prikazu.
     * @return true - parsovani dobre dopadlo a prikaz bude vykonan. <br />
     *         false - neco je spatne zadano, dal nic nedelat
     */
    protected abstract boolean zpracujRadek();

    /**
     * Vypise hlasku do konzole "% Incomplete command.".
     */
    protected void incompleteCommand() {
        kon.posliRadek("% Incomplete command.");
    }

    /**
     * Vypise hlasku do konzole "% Ambiguous command: ".
     */
    protected void ambiguousCommand() {
        kon.posliRadek("% Ambiguous command:  \""+radka+"\"");
    }

    /**
     * Vypise hlasku do konzole "% Invalid input detected.".
     */
    protected void invalidInputDetected() {
        kon.posliRadek("\n% Invalid input detected.\n");
    }

    /**
     * Vypise hlasku do konzole, ze nase implementace nepodoporuje tento 
     */
    protected void unsupported() {
        kon.posliServisne("\nThis command is not implemented in this app \n" +
                "but it is implemented in real cisco. Use help/help_en for a hint.\n");
    }

    /**
     * Zjistuje, zda dany retezec zacina cislem.
     * @param s
     * @return
     */
    protected boolean zacinaCislem(String s) {
        if (s.length() == 0) return false;

        if (Character.isDigit(s.charAt(0))) {
            return true;
        } else {
            return false;
        }
    }

}
