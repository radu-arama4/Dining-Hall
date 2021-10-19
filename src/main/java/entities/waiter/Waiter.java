package entities.waiter;

import entities.order.Order;
import entities.table.Table;
import entities.table.TableStates;
import lombok.SneakyThrows;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import util.DinningHallContext;
import util.Properties;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static tomcat.Request.sendOrderToKitchen;

public class Waiter implements Runnable {
  private static final Logger logger = LogManager.getLogger(Waiter.class);
  private final BlockingQueue<Table> waitingTables = new ArrayBlockingQueue<>(50);

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

    while (DinningHallContext.getInstance().getFinishedOrdersCount() < Properties.NR_OF_ORDERS) {
      if (DinningHallContext.getInstance().hasWaitingTables()) {
        synchronized (tables) {
          for (Table table : tables) {
            if (table.getState().equals(TableStates.WAITING_TO_MAKE_AN_ORDER)) {
              foundTable = table;
              foundTable.waitOrder();
            } else {
              DinningHallContext.getSemaphore().release();
              continue;
            }
            order = table.makeOrder();
            TimeUnit.MILLISECONDS.sleep(2 * Properties.TIME_UNIT);

            waitingTables.add(foundTable);

            DinningHallContext.getInstance().increaseOrdersCount();

            Order finalOrder = order;
            Thread sendingThread = new Thread(() -> sendOrderToKitchen(finalOrder));
            sendingThread.start();
          }
        }
      }
    }
  }

  @SneakyThrows
  public synchronized void serveOrder(Order order) {
    synchronized (waitingTables) {
      for (Table table : waitingTables) {
        if (table.getCurrentOrderId() == order.getId()) {
          TimeUnit.MILLISECONDS.sleep(2 * Properties.TIME_UNIT);

          order.setServingTime(new Timestamp(System.currentTimeMillis()));

          table.freeTable();
          waitingTables.remove(table);

          DinningHallContext.getInstance().removeOrder(order);

          float deliveryTime =
              ((float) (order.getServingTime().getTime() - order.getPickUpTime().getTime())
                  / (float) 1000);

          float maxWait = ((order.getMaxWait() * Properties.TIME_UNIT) / (float) 1000);

          int rating = 0;

          if (deliveryTime < maxWait) {
            rating = 5;
          } else if (deliveryTime < maxWait * 1.1) {
            rating = 4;
          } else if (deliveryTime < maxWait * 1.2) {
            rating = 3;
          } else if (deliveryTime < maxWait * 1.3) {
            rating = 2;
          } else if (deliveryTime < maxWait * 1.4) {
            rating = 1;
          }

          logger.info(
              "Order with ID "
                  + order.getId()
                  + " delivered in "
                  + deliveryTime
                  + " with max wait "
                  + maxWait
                  + " with priority "
                  + order.getPriority()
                  + ". RATING - "
                  + rating);
          return;
        }
      }
    }
  }
}
