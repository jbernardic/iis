package com.example.iis.dto;

import java.util.List;

public record FileImportResult(String format, boolean valid, List<String> errors, Long savedOrderId) {
}
