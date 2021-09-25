package entities.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Food {
  private int id;
  private String name;
  private float preparationTime;
  private int complexity;
  private String cookingApparatus;
}
