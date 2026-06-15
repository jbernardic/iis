package com.example.iis.service;

import com.example.iis.soap.OrderInfo;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Part 2: filters the prepared {@code <orders>} XML file with XPath, returning
 * only the orders whose number/status/currency/customer/email/city/product name
 * contains the given term (case-insensitive, diacritics-aware).
 */
@Service
public class OrderSearchService {

    // translate() maps these uppercase characters to lowercase so the match is
    // case-insensitive in XPath 1.0 (which has no lower-case() function).
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZČĆŽŠĐ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyzčćžšđ";

    private static final String EXPRESSION =
            "/orders/order["
                    + "contains(translate(number,$up,$low),$term) or "
                    + "contains(translate(status,$up,$low),$term) or "
                    + "contains(translate(currency,$up,$low),$term) or "
                    + "contains(translate(billing/firstName,$up,$low),$term) or "
                    + "contains(translate(billing/lastName,$up,$low),$term) or "
                    + "contains(translate(billing/email,$up,$low),$term) or "
                    + "contains(translate(billing/city,$up,$low),$term) or "
                    + "contains(translate(lineItems/lineItem/name,$up,$low),$term)"
                    + "]";

    public List<OrderInfo> search(Path xmlFile, String term) throws Exception {
        String needle = term == null ? "" : term.toLowerCase(Locale.ROOT);

        Document doc = parse(xmlFile);

        XPath xpath = XPathFactory.newInstance().newXPath();
        Map<String, Object> vars = new HashMap<>();
        vars.put("up", UPPER);
        vars.put("low", LOWER);
        vars.put("term", needle);
        xpath.setXPathVariableResolver(name -> vars.get(name.getLocalPart()));

        XPathExpression expr = xpath.compile(EXPRESSION);
        NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

        List<OrderInfo> result = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            Element order = (Element) nodes.item(i);
            OrderInfo info = new OrderInfo();
            info.setNumber(text(order, "number"));
            info.setStatus(text(order, "status"));
            info.setCurrency(text(order, "currency"));
            info.setTotal(text(order, "total"));
            String first = nested(order, "billing", "firstName");
            String last = nested(order, "billing", "lastName");
            info.setCustomerName((first + " " + last).trim());
            info.setEmail(nested(order, "billing", "email"));
            info.setCity(nested(order, "billing", "city"));
            result.add(info);
        }
        return result;
    }

    private static Document parse(Path xmlFile) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(false);
        // Harden against XXE.
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbf.setExpandEntityReferences(false);
        try (InputStream in = Files.newInputStream(xmlFile)) {
            return dbf.newDocumentBuilder().parse(in);
        }
    }

    /** First direct child element with the given tag, or empty string. */
    private static String text(Element parent, String tag) {
        Element child = directChild(parent, tag);
        return child == null ? "" : child.getTextContent().trim();
    }

    private static String nested(Element parent, String tag1, String tag2) {
        Element child = directChild(parent, tag1);
        return child == null ? "" : text(child, tag2);
    }

    private static Element directChild(Element parent, String tag) {
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && tag.equals(node.getNodeName())) {
                return (Element) node;
            }
        }
        return null;
    }
}
