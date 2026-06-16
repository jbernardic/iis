package com.example.client.web;

import com.example.client.model.ImportResponseView;
import com.example.client.service.BackendClient;
import com.example.client.service.WeatherGrpcClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class ServicesController {

    private final BackendClient backend;
    private final WeatherGrpcClient weather;

    public ServicesController(BackendClient backend, WeatherGrpcClient weather) {
        this.backend = backend;
        this.weather = weather;
    }

    @GetMapping("/import")
    public String importForm() {
        return "import";
    }

    @PostMapping("/import")
    public String doImport(@RequestParam(value = "xml", required = false) MultipartFile xml,
                           @RequestParam(value = "json", required = false) MultipartFile json,
                           Model model) throws IOException {
        byte[] x = (xml != null && !xml.isEmpty()) ? xml.getBytes() : null;
        byte[] j = (json != null && !json.isEmpty()) ? json.getBytes() : null;
        if (x == null && j == null) {
            model.addAttribute("error", "Choose at least one file (XML and/or JSON).");
            return "import";
        }
        ImportResponseView result = backend.importOrders(
                x, xml != null ? xml.getOriginalFilename() : "order.xml",
                j, json != null ? json.getOriginalFilename() : "order.json");
        model.addAttribute("result", result);
        return "import";
    }

    @GetMapping("/soap")
    public String soapPage() {
        return "soap";
    }

    @PostMapping("/soap")
    public String soapSearch(@RequestParam String term, Model model) {
        model.addAttribute("term", term);
        try {
            model.addAttribute("results", backend.soapSearch(term));
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "soap";
    }

    @GetMapping("/xml")
    public String xmlPage() {
        return "xml";
    }

    @PostMapping("/xml/validate")
    public String xmlValidate(Model model) {
        try {
            model.addAttribute("validation", backend.validateXml());
            model.addAttribute("xml", backend.generatedXml());
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "xml";
    }

    @GetMapping("/weather")
    public String weatherPage() {
        return "weather";
    }

    @PostMapping("/weather")
    public String weatherSearch(@RequestParam String query, Model model) {
        model.addAttribute("query", query);
        try {
            WeatherGrpcClient.Result result = weather.getTemperature(query);
            model.addAttribute("rows", result.rows());
            model.addAttribute("measuredAt", result.measuredAt());
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "weather";
    }

    @GetMapping("/graphql")
    public String graphqlPage(Model model) {
        if (!model.containsAttribute("query")) {
            model.addAttribute("query", "{\n  orders { number status total billing { city } }\n  activeSource\n}");
        }
        return "graphql";
    }

    @PostMapping("/graphql/run")
    public String graphqlRun(@RequestParam String query, Model model) {
        model.addAttribute("query", query);
        try {
            model.addAttribute("result", backend.graphql(query));
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "graphql";
    }

    @PostMapping("/graphql/mutate")
    public String graphqlMutate(@RequestParam String query, Model model) {
        model.addAttribute("query", query);
        try {
            model.addAttribute("result", backend.graphql(query));
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "graphql";
    }
}
