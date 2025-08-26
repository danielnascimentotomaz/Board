package br.com.dio.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import static br.com.dio.entity.BoardColumnKindEnum.INITIAL;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardColumnEntity {
    private Long id;
    private String name;
    private Integer order;
    private BoardColumnKindEnum kind;
    // Mapeamentos das entidades
    private BoardEntity board = new BoardEntity();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<CardEntity> cardEntityList = new ArrayList<>();


    public BoardColumnEntity(long id, String name, BoardColumnKindEnum boardColumnKindEnum) {
        this.setId(id);
        this.setName(name);
        this.setKind(boardColumnKindEnum);
    }


}
