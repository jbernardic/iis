package com.example.iis.web;

import com.example.iis.dto.FileImportResult;
import com.example.iis.service.OrderImportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/import")
public class ImportController {

    private final OrderImportService importService;

    public ImportController(OrderImportService importService) {
        this.importService = importService;
    }

    @PostMapping(value = "/orders", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> importOrders(
            @RequestParam(value = "xml", required = false) MultipartFile xml,
            @RequestParam(value = "json", required = false) MultipartFile json) throws IOException {

        Map<String, Object> body = new LinkedHashMap<>();
        boolean allValid = true;
        boolean anyFile = false;

        if (xml != null && !xml.isEmpty()) {
            anyFile = true;
            FileImportResult r = importService.importXml(xml.getBytes());
            body.put("xml", r);
            allValid &= r.valid();
        }
        if (json != null && !json.isEmpty()) {
            anyFile = true;
            FileImportResult r = importService.importJson(json.getBytes());
            body.put("json", r);
            allValid &= r.valid();
        }

        if (!anyFile) {
            body.put("error", "Provide at least one file: 'xml' and/or 'json'.");
            return ResponseEntity.badRequest().body(body);
        }

        body.put("allValid", allValid);
        return ResponseEntity.status(allValid ? HttpStatus.OK : HttpStatus.UNPROCESSABLE_ENTITY).body(body);
    }
}
