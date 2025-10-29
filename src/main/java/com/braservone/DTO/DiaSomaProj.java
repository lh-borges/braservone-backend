// src/main/java/com/projetopetroleo/abastecimento/dto/DiaSomaProj.java
package com.braservone.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DiaSomaProj {
    LocalDate getDia();
    BigDecimal getSomaValorTotal();
    Double getSomaKm();
    Double getSomaLitros();
    Long getQtd();
}
