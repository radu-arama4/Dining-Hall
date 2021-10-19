package entities.table;

import entities.order.Food;
import entities.order.Order;
import util.DinningHallContext;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Table {
  private TableStates state;

  private int currentOrderId;

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

    currentOrderId = orderId;

    return generatedOrder;
  }

  private List<Integer> generateRandomOrderItems(int nrOfItems, int maxId) {
    List<Integer> items = new LinkedList<>();
    for (int i = 0; i < nrOfItems; i++) {
      items.add(new Random().nextInt(maxId));
    }
    return items;
  }

  public int getCurrentOrderId() {
    return currentOrderId;
  }

  public void freeTable() {
    currentOrderId = 0;
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
