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
public class Ruling {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String name;
    private Boolean isOpen = false;
    private Integer minutesToEnd;
    private Long total = 0L;
    private Long negativeVote = 0L;
    private Long positiveVote = 0L;
    private LocalDateTime created_at;
    private LocalDateTime expired_date;
    private Boolean expirated = false;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "rulings", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Associate> associates = new ArrayList<>();

    public void addVote(Boolean vote) {
        if(vote) setPositiveVote(positiveVote + 1);
        else setNegativeVote(negativeVote + 1);
        setTotal(negativeVote + positiveVote);
    }
}
