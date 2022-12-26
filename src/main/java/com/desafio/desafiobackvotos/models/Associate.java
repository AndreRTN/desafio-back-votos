package com.desafio.desafiobackvotos.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Associate {

    @Id
    private String cpf;
    private LocalDate voted_at;

    @ManyToMany
    @JoinTable(
            name = "associate_ruling",
            joinColumns = @JoinColumn(name = "associate_id"),
            inverseJoinColumns = @JoinColumn(name = "ruling_id")
    )
    private List<Ruling> rulings;
}
