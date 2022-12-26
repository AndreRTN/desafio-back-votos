package com.desafio.desafiobackvotos.models;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
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
    private LocalDate created_at;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "rulings")
    private List<Associate> associates;
}
