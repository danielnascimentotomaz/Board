package br.com.dio.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardEntity {
    private Long id;
    private  String title;
    private  String description;

    //Mapeamento
    private BoardColumnEntity boardColumn = new BoardColumnEntity();
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<BlockEntity> blockEntities = new ArrayList<>();

    public CardEntity(long cardId, String title, String description) {
        this.setId(cardId);
        this.setTitle(title);
        this.setDescription(description);
    }
}
