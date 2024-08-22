package com.lorena.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private String name;

    @JsonIgnore// Ignora este campo al serializar el objeto a JSON, para evitar referencias circulares o datos innecesarios.
    @ManyToOne //Un restaurante tiene multiples categorias
    private Restaurant restaurant;

}
