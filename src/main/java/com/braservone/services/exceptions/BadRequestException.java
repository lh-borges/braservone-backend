// src/main/java/com/braservone/services/exceptions/BadRequestException.java
package com.braservone.services.exceptions;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BadRequestException(
    @JsonProperty("mensage") String mensage,
    @JsonProperty("status")  int status
) {}
