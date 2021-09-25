package util;

import entities.order.Food;
import entities.table.Table;
import entities.waiter.Waiter;

import java.util.List;

public class DinningHallContext {
  private static DinningHallContext instance;

  private static int finishedOrdersCount;

  private List<Table> tables;
  private List<Waiter> waiters;
  private List<Food> foods;

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

  public void increaseOrdersCount(){
    finishedOrdersCount++;
  }
}
