package entities.waiter;

import com.google.gson.Gson;
import entities.order.Order;
import entities.table.Table;
import entities.table.TableStates;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import util.DinningHallContext;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
          if (table.getState().equals(TableStates.WAITING_TO_MAKE_AN_ORDER)) {
            order = table.makeOrder();
            foundTable = table;
          }
        }
      }

      TimeUnit.SECONDS.sleep(4);

      if (foundTable.getState().equals(TableStates.WAITING_TO_MAKE_AN_ORDER)) {
        waitingTables.add(foundTable);
        foundTable.waitOrder();

        foundTable = null;

        DinningHallContext.getInstance().increaseOrdersCount();

        sentOrderToKitchen(order);
      }
    }
  }

  public void serveOrder(Order order) {
    System.out.println("WAAAI");
    for (Table table : waitingTables) {
      if (table.getCurrentOrder().getId() == order.getId()) {
        try {
          TimeUnit.SECONDS.sleep(5);
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

  private void sentOrderToKitchen(Order order) {
    HttpURLConnection con = null;
    try {
      URL url = new URL("http://localhost:8081/home");
      con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("POST");
    } catch (IOException e) {
      log.error(e.getMessage());
    }
    con.setRequestProperty("Content-Type", "application/json; utf-8");
    con.setRequestProperty("Accept", "application/json");

    con.setDoOutput(true);

    Gson gson = new Gson();

    String json = gson.toJson(order);

    try (OutputStream os = con.getOutputStream()) {
      byte[] input = json.getBytes("utf-8");
      os.write(input, 0, input.length);
    } catch (IOException e) {
      log.error(e.getMessage());
    }

    try (BufferedReader br =
        new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
      StringBuilder response = new StringBuilder();
      String responseLine = null;
      while ((responseLine = br.readLine()) != null) {
        response.append(responseLine.trim());
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    System.out.println("Order with ID " + order.getId() + " sent to Kitchen!");
  }

  public List<Table> getWaitingTables() {
    return waitingTables;
  }
}
