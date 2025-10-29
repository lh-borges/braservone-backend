package com.braservone.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;

@Entity
public class Hospedagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    private Departamento departamento;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @NotNull
    private LocalDate dataEntrada;

    @NotNull
    private LocalDate dataSaida;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "hospedagem_usuarios",
        joinColumns = @JoinColumn(name = "hospedagem_id"),
        // IMPORTANTE: User tem PK = username (String)
        inverseJoinColumns = @JoinColumn(name = "user_username", referencedColumnName = "username")
    )
    private Set<User> hospedes = new LinkedHashSet<>();

    @Column(precision = 14, scale = 2)
    private BigDecimal valor;

    private Long centroCustos;

    public void adicionarHospede(User u) {
        if (hospedes.contains(u)) {
            throw new IllegalArgumentException("Usuário já está hospedado!");
        }
        hospedes.add(u);
    }

    public void removerHospede(User u) {
        if (!hospedes.contains(u)) {
            throw new IllegalArgumentException("Usuário não está hospedado!");
        }
        hospedes.remove(u);
    }

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Hotel getHotel() { return hotel; }
    public void setHotel(Hotel hotel) { this.hotel = hotel; }
    public LocalDate getDataEntrada() { return dataEntrada; }
    public void setDataEntrada(LocalDate dataEntrada) { this.dataEntrada = dataEntrada; }
    public LocalDate getDataSaida() { return dataSaida; }
    public void setDataSaida(LocalDate dataSaida) { this.dataSaida = dataSaida; }
    public Set<User> getHospedes() { return hospedes; }
    public void setHospedes(Set<User> hospedes) { this.hospedes = hospedes; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public Long getCentroCustos() { return centroCustos; }
    public void setCentroCustos(Long centroCustos) { this.centroCustos = centroCustos; }
}
