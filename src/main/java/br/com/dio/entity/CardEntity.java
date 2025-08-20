package br.com.dio.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
public class CardEntity {
    private Long id;
    private  String title;
    private  String description;

    //Mapeamento
    private BoardColumnEntity boardColumn = new BoardColumnEntity();
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<BlockEntity> blockEntities = new ArrayList<>();
}
