package com.example.iis.dto;

import java.util.List;

/**
 * Outcome of validating + importing one uploaded file (Part 1).
 *
 * @param format       "xml" or "json"
 * @param valid        whether the document passed schema validation
 * @param errors       human-readable validation problems (empty when valid)
 * @param savedOrderId database id of the stored order, or {@code null} if not saved
 */
public record FileImportResult(String format, boolean valid, List<String> errors, Long savedOrderId) {
}
