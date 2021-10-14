package entities.waiter;

import entities.order.Order;
import entities.table.Table;
import entities.table.TableStates;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import util.DinningHallContext;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static tomcat.Request.sendOrderToKitchen;

@Slf4j
public class Waiter implements Runnable {
  private BlockingQueue<Table> waitingTables = new ArrayBlockingQueue<>(50);

  private final Semaphore semaphore;

  public Waiter(Semaphore semaphore) {
    this.semaphore = semaphore;
  }

  @SneakyThrows
  @Override
  public void run() {
    List<Table> tables = DinningHallContext.getInstance().getTables();
    Order order;
    Table foundTable;

    while (true) {
      if (DinningHallContext.getInstance().hasWaitingTables()) {
        for (Table table : tables) {
          synchronized (table) {
            if (table.getState().equals(TableStates.WAITING_TO_MAKE_AN_ORDER)) {
              order = table.makeOrder();
              foundTable = table;
            }else {
              continue;
            }
          }

          TimeUnit.SECONDS.sleep(2);

          if (foundTable.getState().equals(TableStates.WAITING_TO_MAKE_AN_ORDER)) {
            waitingTables.add(foundTable);
            foundTable.waitOrder();

            System.out.println("Waiting tables: ");
            waitingTables.forEach(
                table1 -> {
                  System.out.print(table1.getCurrentOrderId() + " ");
                });

            System.out.println();

            DinningHallContext.getInstance().increaseOrdersCount();

            sendOrderToKitchen(order);
          }
        }
      }
      if (DinningHallContext.getInstance().hasReadyOrders()) {
        Order readyOrder = DinningHallContext.getInstance().getOrder();
        serveOrder(readyOrder);
      }
    }
  }

  @SneakyThrows
  public synchronized void serveOrder(Order order) {
    for (Table table : waitingTables) {
      if (table.getCurrentOrderId() == order.getId()) {
        try {
          TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        table.freeTable();
        waitingTables.remove(table);

        DinningHallContext.getInstance().removeOrder(order);

        System.out.println("Order with ID " + order.getId() + " is being served!");

        return;
      }
    }
  }

  public BlockingQueue<Table> getWaitingTables() {
    return waitingTables;
  }
}
