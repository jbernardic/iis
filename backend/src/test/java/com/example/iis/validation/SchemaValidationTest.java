package com.example.iis.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SchemaValidationTest {

    private static XmlSchemaValidator xml;
    private static JsonSchemaValidator json;

    private static final String VALID_XML = """
            <order>
              <number>2001</number><status>processing</status><currency>EUR</currency>
              <total>99.98</total>
              <billing>
                <firstName>Ivan</firstName><lastName>Horvat</lastName>
                <email>ivan@example.com</email><city>Zagreb</city><country>HR</country>
              </billing>
            </order>""";

    private static final String INVALID_XML = """
            <order>
              <number>2002</number><status>shipped</status><currency>Euro</currency>
              <total>-5</total>
              <billing>
                <firstName>Ana</firstName><lastName>Anic</lastName>
                <email>not-an-email</email><city>Split</city><country>Croatia</country>
              </billing>
            </order>""";

    private static final String VALID_JSON = """
            {"number":"3001","status":"completed","currency":"USD","total":199.0,
             "billing":{"firstName":"Marko","lastName":"Maric","email":"m@example.com",
                        "city":"Osijek","country":"HR"}}""";

    private static final String INVALID_JSON = """
            {"number":"3002","status":"shipped","currency":"usd","total":-10,
             "billing":{"firstName":"Petra","email":"bad","city":"Rijeka","country":"HRV"},
             "unexpected":true}""";

    @BeforeAll
    static void setup() throws Exception {
        xml = new XmlSchemaValidator();
        xml.init();
        json = new JsonSchemaValidator(new ObjectMapper());
        json.init();
    }

    @Test
    void validXmlPasses() {
        assertThat(xml.validate(VALID_XML.getBytes(StandardCharsets.UTF_8))).isEmpty();
    }

    @Test
    void invalidXmlReportsErrors() {
        List<String> errors = xml.validate(INVALID_XML.getBytes(StandardCharsets.UTF_8));
        assertThat(errors).isNotEmpty();
    }

    @Test
    void validJsonPasses() {
        assertThat(json.validate(VALID_JSON.getBytes(StandardCharsets.UTF_8))).isEmpty();
    }

    @Test
    void invalidJsonReportsErrors() {
        List<String> errors = json.validate(INVALID_JSON.getBytes(StandardCharsets.UTF_8));
        assertThat(errors).isNotEmpty();
        assertThat(errors.size()).isGreaterThanOrEqualTo(5);
    }
}
