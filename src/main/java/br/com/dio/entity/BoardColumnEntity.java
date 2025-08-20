package br.com.dio.entity;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


@Data
public class BoardColumnEntity {
    private Long id;
    private String name;
    private Integer order;
    private BoardColumnKindEnum kind;
    // Mapeamentos das entidades
    private BoardEntity board = new BoardEntity();
    @ToString.Exclude
    private List<CardEntity> cardEntityList = new ArrayList<>();

}
