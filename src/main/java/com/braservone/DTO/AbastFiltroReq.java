// src/main/java/com/projetopetroleo/abastecimento/dto/AbastFiltroReq.java
package com.braservone.DTO;

import java.time.LocalDate;

public record AbastFiltroReq(
    LocalDate inicio,   // inclusive
    LocalDate fim,      // inclusive
    String placa        // opcional; null/blank = todos
) {}
