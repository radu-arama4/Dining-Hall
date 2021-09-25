package entities.table;

import entities.order.Food;
import entities.order.Order;
import lombok.extern.slf4j.Slf4j;
import util.DinningHallContext;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@Slf4j
public class Table {
  private TableStates state;

  private Order currentOrder;

  public Table() {
    state = TableStates.FREE;
  }

  public Order makeOrder() {
    int orderId = new Random().nextInt(100000);

    List<Integer> items = generateRandomOrderItems(4, 10);

    Order generatedOrder =
        new Order(orderId, generateRandomOrderItems(4, 10), new Random().nextInt(5));

    List<Food> foods = DinningHallContext.getInstance().getFoods();

    float maxPrepTime = 0;

    for (Food food : foods) {
      if (items.contains(food.getId())) {
        if (food.getPreparationTime() > maxPrepTime) {
          maxPrepTime = food.getPreparationTime();
        }
      }
    }

    generatedOrder.setMaxWait((float) (maxPrepTime * 1.3));
    generatedOrder.setPickUpTime(new Timestamp(System.currentTimeMillis()));

    currentOrder = generatedOrder;

    log.info("Table is waiting for order " + currentOrder.getId());

    return generatedOrder;
  }

  private List<Integer> generateRandomOrderItems(int nrOfItems, int maxId) {
    List<Integer> items = new LinkedList<>();
    for (int i = 0; i < nrOfItems; i++) {
      items.add(new Random().nextInt(maxId));
    }
    return items;
  }

  public Order getCurrentOrder() {
    return currentOrder;
  }

  public void freeTable() {
    currentOrder = null;
    state = TableStates.FREE;
  }

  public void waitServing() {
    state = TableStates.WAITING_TO_MAKE_AN_ORDER;
  }

  public void waitOrder() {
    state = TableStates.WAITING_ORDER;
  }

  public TableStates getState() {
    return state;
  }
}
