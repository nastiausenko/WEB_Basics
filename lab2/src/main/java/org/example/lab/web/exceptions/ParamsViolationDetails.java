package org.example.lab.web.exceptions;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class ParamsViolationDetails {
    String fieldName;
    String reason;
}