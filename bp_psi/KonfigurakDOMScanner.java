/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;

/**
 *
 * @author haldyr
 */
public class KonfigurakDOMScanner {

    /**
     * Document document
     */
    Document document;

    /**
     * Create new KonfigurakDOMScanner with Document.
     */
    public KonfigurakDOMScanner(Document document) {
        this.document = document;
    }

    /**
     * Scan through Document document.
     */
    public void visitDocument() {
        Element element = document.getDocumentElement();
        if ((element != null) && element.getTagName().equals("ip")) {
            visitElement_ip(element);
        }
        if ((element != null) && element.getTagName().equals("jmeno")) {
            visitElement_jmeno(element);
        }
        if ((element != null) && element.getTagName().equals("konfigurak")) {
            visitElement_konfigurak(element);
        }
        if ((element != null) && element.getTagName().equals("mac")) {
            visitElement_mac(element);
        }
        if ((element != null) && element.getTagName().equals("pocitac")) {
            visitElement_pocitac(element);
        }
        if ((element != null) && element.getTagName().equals("pripojenoK")) {
            visitElement_pripojenoK(element);
        }
        if ((element != null) && element.getTagName().equals("rozhrani")) {
            visitElement_rozhrani(element);
        }
    }

    /**
     * Scan through Element named ip.
     */
    void visitElement_ip(Element element) {
        // <ip>
// element.getValue();
        NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case Node.CDATA_SECTION_NODE:
                    break;
                case Node.ELEMENT_NODE:
                    Element nodeElement = (Element) node;
                    break;
                case Node.PROCESSING_INSTRUCTION_NODE:
                    break;
                case Node.TEXT_NODE:
                    break;
            }
        }
    }

    /**
     * Scan through Element named jmeno.
     */
    void visitElement_jmeno(Element element) {
        // <jmeno>
// element.getValue();
        NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case Node.CDATA_SECTION_NODE:
                    break;
                case Node.ELEMENT_NODE:
                    Element nodeElement = (Element) node;
                    break;
                case Node.PROCESSING_INSTRUCTION_NODE:
                    break;
                case Node.TEXT_NODE:
                    break;
            }
        }
    }

    /**
     * Scan through Element named konfigurak.
     */
    void visitElement_konfigurak(Element element) {
        // <konfigurak>
// element.getValue();
        NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case Node.CDATA_SECTION_NODE:
                    break;
                case Node.ELEMENT_NODE:
                    Element nodeElement = (Element) node;
                    if (nodeElement.getTagName().equals("pocitac")) {
                        visitElement_pocitac(nodeElement);
                    }
                    break;
                case Node.PROCESSING_INSTRUCTION_NODE:
                    break;
            }
        }
    }

    /**
     * Scan through Element named mac.
     */
    void visitElement_mac(Element element) {
        // <mac>
// element.getValue();
        NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case Node.CDATA_SECTION_NODE:
                    break;
                case Node.ELEMENT_NODE:
                    Element nodeElement = (Element) node;
                    break;
                case Node.PROCESSING_INSTRUCTION_NODE:
                    break;
                case Node.TEXT_NODE:
                    break;
            }
        }
    }

    /**
     * Scan through Element named pocitac.
     */
    void visitElement_pocitac(Element element) {
        // <pocitac>
// element.getValue();
        NamedNodeMap attrs = element.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            Attr attr = (Attr) attrs.item(i);
            if (attr.getName().equals("id")) {
            }
        }
        NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case Node.CDATA_SECTION_NODE:
                    break;
                case Node.ELEMENT_NODE:
                    Element nodeElement = (Element) node;
                    if (nodeElement.getTagName().equals("rozhrani")) {
                        visitElement_rozhrani(nodeElement);
                    }
                    break;
                case Node.PROCESSING_INSTRUCTION_NODE:
                    break;
            }
        }
    }

    /**
     * Scan through Element named pripojenoK.
     */
    void visitElement_pripojenoK(Element element) {
        // <pripojenoK>
// element.getValue();
        NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case Node.CDATA_SECTION_NODE:
                    break;
                case Node.ELEMENT_NODE:
                    Element nodeElement = (Element) node;
                    break;
                case Node.PROCESSING_INSTRUCTION_NODE:
                    break;
                case Node.TEXT_NODE:
                    break;
            }
        }
    }

    /**
     * Scan through Element named rozhrani.
     */
    void visitElement_rozhrani(Element element) {
        // <rozhrani>
// element.getValue();
        NamedNodeMap attrs = element.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            Attr attr = (Attr) attrs.item(i);
            if (attr.getName().equals("id")) {
            }
        }
        NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case Node.CDATA_SECTION_NODE:
                    break;
                case Node.ELEMENT_NODE:
                    Element nodeElement = (Element) node;
                    if (nodeElement.getTagName().equals("ip")) {
                        visitElement_ip(nodeElement);
                    }
                    if (nodeElement.getTagName().equals("jmeno")) {
                        visitElement_jmeno(nodeElement);
                    }
                    if (nodeElement.getTagName().equals("mac")) {
                        visitElement_mac(nodeElement);
                    }
                    if (nodeElement.getTagName().equals("pripojenoK")) {
                        visitElement_pripojenoK(nodeElement);
                    }
                    break;
                case Node.PROCESSING_INSTRUCTION_NODE:
                    break;
            }
        }
    }
}
