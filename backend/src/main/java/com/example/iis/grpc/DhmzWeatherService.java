package com.example.iis.grpc;

import com.example.iis.config.AppProperties;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class DhmzWeatherService {

    private static final Duration CACHE_TTL = Duration.ofMinutes(10);

    private final AppProperties props;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private volatile Snapshot cache;

    public DhmzWeatherService(AppProperties props) {
        this.props = props;
    }

    public synchronized Snapshot getSnapshot() {
        if (cache != null && Duration.between(cache.fetchedAt(), Instant.now()).compareTo(CACHE_TTL) < 0) {
            return cache;
        }
        cache = fetchAndParse();
        return cache;
    }

    private Snapshot fetchAndParse() {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(props.getDhmz().getUrl()))
                    .timeout(Duration.ofSeconds(15))
                    .GET()
                    .build();
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() != 200) {
                throw new IllegalStateException("DHMZ returned HTTP " + response.statusCode());
            }

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(false);
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            Document doc = dbf.newDocumentBuilder()
                    .parse(new ByteArrayInputStream(response.body()));

            String measuredAt = readMeasuredAt(doc);

            List<City> cities = new ArrayList<>();
            NodeList grad = doc.getElementsByTagName("Grad");
            for (int i = 0; i < grad.getLength(); i++) {
                Element g = (Element) grad.item(i);
                Element podatci = directChild(g, "Podatci");
                cities.add(new City(
                        text(g, "GradIme"),
                        podatci == null ? "" : text(podatci, "Temp"),
                        podatci == null ? "" : text(podatci, "Vlaga"),
                        podatci == null ? "" : text(podatci, "Tlak"),
                        podatci == null ? "" : text(podatci, "VjetarSmjer"),
                        podatci == null ? "" : text(podatci, "VjetarBrzina"),
                        podatci == null ? "" : text(podatci, "Vrijeme")));
            }
            return new Snapshot(measuredAt, cities, Instant.now());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load DHMZ data: " + e.getMessage(), e);
        }
    }

    private static String readMeasuredAt(Document doc) {
        NodeList dt = doc.getElementsByTagName("DatumTermin");
        if (dt.getLength() == 0) {
            return "";
        }
        Element e = (Element) dt.item(0);
        String date = text(e, "Datum");
        String term = text(e, "Termin");
        return term.isEmpty() ? date : (date + " " + term + "h");
    }

    private static String text(Element parent, String tag) {
        Element child = directChild(parent, tag);
        return child == null ? "" : child.getTextContent().trim();
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

    public record City(String name, String temperature, String humidity, String pressure,
                       String windDirection, String windSpeed, String weather) {
    }

    public record Snapshot(String measuredAt, List<City> cities, Instant fetchedAt) {
    }
}
