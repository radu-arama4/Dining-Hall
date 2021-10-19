package util;

import entities.order.Order;
import entities.waiter.Waiter;

import java.util.List;

public class OrderReceiveService implements Runnable {
  @Override
  public void run() {
    while (true) {
      if (DinningHallContext.getInstance().hasReadyOrders()) {
        Order readyOrder = DinningHallContext.getInstance().getOrder();

        List<Waiter> waiters = DinningHallContext.getInstance().getWaiters();
        for (Waiter waiter : waiters) {

          synchronized (waiter) {
            waiter.serveOrder(readyOrder);
          }
        }
      }
    }
  }
}
