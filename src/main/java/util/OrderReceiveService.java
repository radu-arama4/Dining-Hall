package util;

import entities.order.Order;
import entities.waiter.Waiter;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderReceiveService implements Runnable {
  @Override
  public void run() {
    ExecutorService executorService = Executors.newFixedThreadPool(10);

    while (true) {
      executorService.execute(
          () -> {
            if (DinningHallContext.getInstance().hasReadyOrders()) {
              Order readyOrder = DinningHallContext.getInstance().getOrder();

              List<Waiter> waiters = DinningHallContext.getInstance().getWaiters();
              for (Waiter waiter : waiters) {

                synchronized (waiter) {
                  waiter.serveOrder(readyOrder);
                }
              }
            }
          });
    }
  }
}
