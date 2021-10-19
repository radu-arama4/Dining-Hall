package tomcat;

import com.google.gson.Gson;
import entities.order.Order;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;


public class Request {
  private static final Logger log = LogManager.getLogger(Request.class);

  public static void sendOrderToKitchen(Order order) {
    HttpURLConnection con = null;
    // host.docker.internal
    try {
      URL url = new URL("http://host.docker.internal:8081/home");
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
      String responseLine;
      while ((responseLine = br.readLine()) != null) {
        response.append(responseLine.trim());
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    log.info("Order with ID " + order.getId() + " sent to Kitchen!");
  }
}
