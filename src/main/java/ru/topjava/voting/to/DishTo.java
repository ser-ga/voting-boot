package ru.topjava.voting.to;

import lombok.*;
import ru.topjava.voting.HasId;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DishTo implements HasId {

    protected Long id;

    @NotNull
    private String name;

    @NotNull
    private BigDecimal price;

    @NotNull
    private Long menuId;

}
