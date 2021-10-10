package entities.waiter;

import entities.order.Order;
import entities.table.Table;
import entities.table.TableStates;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import util.DinningHallContext;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static tomcat.Request.sentOrderToKitchen;

@Slf4j
public class Waiter implements Runnable {
  private List<Table> waitingTables = new LinkedList<>();

  @SneakyThrows
  @Override
  public void run() {
    List<Table> tables = DinningHallContext.getInstance().getTables();
    Order order = null;
    Table foundTable = null;

    while (DinningHallContext.getInstance().getFinishedOrdersCount() < 10) {
      while (foundTable == null) {
        for (Table table : tables) {
          synchronized (table){
            if (table.getState().equals(TableStates.WAITING_TO_MAKE_AN_ORDER)) {
              order = table.makeOrder();
              foundTable = table;
            }
          }
        }
      }

      TimeUnit.SECONDS.sleep(2);

      if (foundTable.getState().equals(TableStates.WAITING_TO_MAKE_AN_ORDER)) {
        waitingTables.add(foundTable);
        foundTable.waitOrder();

        foundTable = null;

        DinningHallContext.getInstance().increaseOrdersCount();

        sentOrderToKitchen(order);
      }
    }
  }

  public synchronized void serveOrder(Order order) {
    for (Table table : waitingTables) {
      if (table.getCurrentOrder().getId() == order.getId()) {
        try {
          TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        table.freeTable();
        waitingTables.remove(table);

        System.out.println("Order with ID " + order.getId() + " is being served!");

        return;
      }
    }
  }

  public List<Table> getWaitingTables() {
    return waitingTables;
  }
}
