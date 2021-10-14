package util;

import entities.order.Food;
import entities.order.Order;
import entities.table.Table;
import entities.table.TableStates;
import entities.waiter.Waiter;
import lombok.SneakyThrows;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

public class DinningHallContext {
  private static DinningHallContext instance;

  private static int finishedOrdersCount;

  private List<Table> tables;
  private List<Waiter> waiters;
  private List<Food> foods;

  private int readyOrdersCount = 0;

  private final BlockingQueue<Order> readyOrders = new ArrayBlockingQueue<>(20);

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

  public BlockingQueue<Order> getReadyOrders() {
    return readyOrders;
  }

  public boolean hasWaitingTables() {
    if (!tables.isEmpty()){
      for (Table table : tables) {
        if (table.getState().equals(TableStates.WAITING_TO_MAKE_AN_ORDER)) {
          return true;
        }
      }
    }
    return false;
  }

  public synchronized boolean hasReadyOrders(){
    return !readyOrders.isEmpty();
  }

  @SneakyThrows
  public synchronized Order getOrder(){
    return readyOrders.take();
  }

  public synchronized void removeOrder(Order order){
    readyOrders.remove(order);
  }

  @SneakyThrows
  public synchronized void addOrder(Order order) {
    readyOrders.add(order);

    //    for (Waiter waiter : waiters) {
    //      waiter.serveOrder(readyOrders.take());
    //    }
  }
}
