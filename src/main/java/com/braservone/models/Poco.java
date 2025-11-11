package com.braservone.models;

import java.math.BigDecimal;

import com.braservone.enums.Bacia;
import com.braservone.enums.StatusPoco;
import com.braservone.enums.TipoFluido;
import com.braservone.enums.TipoPoco;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
    name = "pocos",
    indexes = {
        @Index(name = "idx_poco_status", columnList = "status"),
        @Index(name = "idx_poco_bacia", columnList = "bacia"),
        @Index(name = "idx_poco_nome_campo", columnList = "nome_campo")
    }
)
public class Poco {

    @Id
    @Column(name = "codigo_anp", nullable = false, length = 64, unique = true)
    private String codigoAnp;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_poco", length = 32, nullable = false)
    private TipoPoco tipoPoco;

    @Size(max = 120)
    @Column(name = "nome_campo", length = 120)
    private String nomeCampo;

    @Enumerated(EnumType.STRING)
    @Column(name = "bacia", length = 64)
    private Bacia bacia;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 48)
    private StatusPoco status;

    @Enumerated(EnumType.STRING)
    @Column(name = "fluido", length = 32)
    private TipoFluido fluido;

    @Size(max = 120)
    @Column(name = "localidade", length = 120)
    private String local;

    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    @Column(name = "latitude", precision = 15, scale = 6)
    private BigDecimal latitude;

    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    @Column(name = "longitude", precision = 15, scale = 6)
    private BigDecimal longitude;

    public void atualizar(Poco src) {
        if (src == null) return;

        if (notBlank(src.getNomeCampo())) this.nomeCampo = src.getNomeCampo();
        if (src.getBacia() != null)        this.bacia = src.getBacia();
        if (src.getStatus() != null)       this.status = src.getStatus();
        if (src.getFluido() != null)       this.fluido = src.getFluido();
        if (src.getTipoPoco() != null)     this.tipoPoco = src.getTipoPoco();
        if (notBlank(src.getLocal()))      this.local = src.getLocal();
        if (src.getLatitude() != null)     this.latitude = src.getLatitude();
        if (src.getLongitude() != null)    this.longitude = src.getLongitude();
    }

    private boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }
}
