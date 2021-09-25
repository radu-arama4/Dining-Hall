package entities.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;


@AllArgsConstructor
@Getter
@Setter
public class Order {
  private int id;
  private List<Integer> items;
  private int priority;
  private float maxWait;
  private boolean ready;
  private float preparingTime;
  private Timestamp pickUpTime;
  private Timestamp servingTime;

  public Order(int id, List<Integer> items, int priority) {
    this.id = id;
    this.items = items;
    this.priority = priority;
    ready = false;
  }
}
