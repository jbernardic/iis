package com.example.iis.validation;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
public class XmlSchemaValidator {

    private Schema schema;

    @PostConstruct
    void init() throws Exception {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        trySetProperty(factory, XMLConstants.ACCESS_EXTERNAL_DTD, "");
        trySetProperty(factory, XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        try (InputStream xsd = new ClassPathResource("schema/order.xsd").getInputStream()) {
            this.schema = factory.newSchema(new StreamSource(xsd));
        }
    }

    public List<String> validate(byte[] xml) {
        return validate(new StreamSource(new ByteArrayInputStream(xml)));
    }

    public List<String> validate(Path path) throws IOException {
        try (InputStream in = Files.newInputStream(path)) {
            return validate(new StreamSource(in));
        }
    }

    public List<String> validate(Source source) {
        CollectingErrorHandler handler = new CollectingErrorHandler();
        try {
            Validator validator = schema.newValidator();
            trySetProperty(validator, XMLConstants.ACCESS_EXTERNAL_DTD, "");
            trySetProperty(validator, XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            validator.setErrorHandler(handler);
            validator.validate(source);
        } catch (SAXException e) {
            handler.messages.add("Fatal: " + e.getMessage());
        } catch (IOException e) {
            handler.messages.add("I/O error reading XML: " + e.getMessage());
        }
        return handler.messages;
    }

    private static void trySetProperty(SchemaFactory factory, String name, String value) {
        try {
            factory.setProperty(name, value);
        } catch (Exception ignored) {
        }
    }

    private static void trySetProperty(Validator validator, String name, String value) {
        try {
            validator.setProperty(name, value);
        } catch (Exception ignored) {
        }
    }

    private static final class CollectingErrorHandler implements ErrorHandler {
        private final List<String> messages = new ArrayList<>();

        @Override
        public void warning(SAXParseException e) {
            messages.add(format("Warning", e));
        }

        @Override
        public void error(SAXParseException e) {
            messages.add(format("Error", e));
        }

        @Override
        public void fatalError(SAXParseException e) {
            messages.add(format("Fatal", e));
        }

        private static String format(String severity, SAXParseException e) {
            return severity + " [line " + e.getLineNumber() + ", col " + e.getColumnNumber() + "]: "
                    + e.getMessage();
        }
    }
}
