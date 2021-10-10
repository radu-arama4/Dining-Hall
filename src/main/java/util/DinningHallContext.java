package util;

import entities.order.Food;
import entities.order.Order;
import entities.table.Table;
import entities.waiter.Waiter;
import lombok.SneakyThrows;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class DinningHallContext {
  private static DinningHallContext instance;

  private static int finishedOrdersCount;

  private List<Table> tables;
  private List<Waiter> waiters;
  private List<Food> foods;

  private int readyOrdersCount = 0;

  private volatile List<Order> readyOrders = new LinkedList<>();

  private static Semaphore semaphore = new Semaphore(1);

  private DinningHallContext() {}

  public static DinningHallContext getInstance() {
    if (instance == null) {
      instance = new DinningHallContext();
      finishedOrdersCount = 0;
    }
    return instance;
  }

  public void setTables(List<Table> tables) {
    this.tables = tables;
  }

  public void setWaiters(List<Waiter> waiters) {
    this.waiters = waiters;
  }

  public List<Table> getTables() {
    return tables;
  }

  public List<Waiter> getWaiters() {
    return waiters;
  }

  public List<Food> getFoods() {
    return foods;
  }

  public void setFoods(List<Food> foods) {
    this.foods = foods;
  }

  public int getFinishedOrdersCount() {
    return finishedOrdersCount;
  }

  public void increaseOrdersCount() {
    finishedOrdersCount++;
  }

  public List<Order> getReadyOrders() {
    return readyOrders;
  }

  @SneakyThrows
  public synchronized void addOrder(Order order) {
    readyOrders.add(order);
    readyOrdersCount++;
    System.out.println(readyOrdersCount);

    semaphore.acquire();

    Thread thread =
        new Thread(
            () -> {
              for (Waiter waiter : waiters) {
                waiter.serveOrder(order);
              }
            });

    semaphore.release();

    thread.start();
  }
}
