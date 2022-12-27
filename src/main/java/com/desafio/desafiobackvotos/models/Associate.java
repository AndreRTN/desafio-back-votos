package com.desafio.desafiobackvotos.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Associate {

    @Id
    private String cpf;
    private LocalDateTime voted_at;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "associate_ruling",
            joinColumns = @JoinColumn(name = "associate_id"),
            inverseJoinColumns = @JoinColumn(name = "ruling_id")
    )
    @JsonIgnore
    private List<Ruling> rulings = new ArrayList<>();

    public void addRuling(Ruling ruling) {
        rulings.add(ruling);
    }
}
