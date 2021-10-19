import entities.order.Food;
import entities.table.Table;
import entities.waiter.Waiter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import util.ApplicationManager;
import util.DinningHallContext;
import util.OrderReceiveService;
import util.Properties;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class Main {
  private static final Logger logger = LogManager.getLogger(Main.class);

  public static void main(String[] args) {

    ApplicationManager.startApplication();

    generateTables(Properties.NR_OF_TABLES);
    generateWaiters(Properties.NR_OF_WAITERS);

    DinningHallContext dinningHallContext = DinningHallContext.getInstance();

    List<Waiter> waiters = generateWaiters(5);

    dinningHallContext.setTables(generateTables(10));
    dinningHallContext.setWaiters(waiters);
    dinningHallContext.setFoods(generateFood());

    List<Thread> threads = new LinkedList<>();

    for (Waiter waiter : waiters) {
      Thread thread = new Thread(waiter);
      threads.add(thread);
      thread.start();
    }

    Thread thread = new Thread(new OrderReceiveService());
    thread.start();

    occupyTables();

//    while (true){
//      if (DinningHallContext.getInstance().getFinishedOrdersCount()==10){
//        for (Thread thread1 : threads) {
//          thread1.interrupt();
//        }
//      }
//    }

    //    ApplicationManager.closeApplication();
  }

  private static List<Waiter> generateWaiters(int nrOfWaiters) {
    Semaphore semaphore = new Semaphore(1);
    List<Waiter> waiters = new LinkedList<>();
    for (int i = 0; i < nrOfWaiters; i++) {
      Waiter newWaiter = new Waiter(semaphore);
      waiters.add(newWaiter);
    }
    return waiters;
  }

  public static List<Table> generateTables(int nrOfTables) {
    List<Table> tables = new LinkedList<>();
    for (int i = 0; i < nrOfTables; i++) {
      Table newTable = new Table();
      newTable.freeTable();
      tables.add(newTable);
    }
    return tables;
  }

  public static List<Food> generateFood() {
    Food pizza = new Food(1, "pizza", 20, 2, "oven");
    Food salad = new Food(2, "salad", 10, 1, null);
    Food zeama = new Food(3, "zeama", 7, 1, "stove");
    Food scallopSashimiWithMeyerLemonConfit =
        new Food(4, "Scallop Sashimi with Meyer Lemon Confit", 32, 3, null);
    Food islandDuckWithMulberryMustard =
        new Food(5, "Island Duck with Mulberry Mustard", 35, 3, "oven");
    Food waffles = new Food(6, "Waffles", 10, 1, "stove");
    Food aubergine = new Food(7, "Aubergine", 20, 2, null);
    Food lasagna = new Food(8, "Lasagna", 30, 2, "oven");
    Food burger = new Food(9, "Burger", 15, 1, "oven");
    Food gyros = new Food(10, "Gyros", 15, 1, null);

    return new LinkedList<>(
        Arrays.asList(
            pizza,
            salad,
            zeama,
            scallopSashimiWithMeyerLemonConfit,
            islandDuckWithMulberryMustard,
            waffles,
            aubergine,
            lasagna,
            burger,
            gyros));
  }

  public static void occupyTables() {
    ExecutorService executorService = Executors.newFixedThreadPool(10);

    while (DinningHallContext.getInstance().getFinishedOrdersCount() < Properties.NR_OF_ORDERS) {
      executorService.execute(
          () -> {
            int randomNr = new Random().nextInt(100);

            int tableToBeServed;

            if (randomNr % 3 == 0) {
              tableToBeServed = new Random().nextInt(10);
              Table table = DinningHallContext.getInstance().getTables().get(tableToBeServed);
              table.waitServing();
            }
          });
    }
  }
}
