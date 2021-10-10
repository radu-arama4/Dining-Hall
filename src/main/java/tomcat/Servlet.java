package tomcat;

import com.google.gson.Gson;
import entities.order.Order;
import util.DinningHallContext;
import util.JsonUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Servlet extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setStatus(HttpServletResponse.SC_OK);
    resp.getWriter().write("welcome");
    resp.getWriter().flush();
    resp.getWriter().close();
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String payloadRequest = JsonUtil.getBody(req);

    Gson gson = new Gson();

    Order receivedOrder = gson.fromJson(payloadRequest, Order.class);

    System.out.println("Received ready order with ID:" + receivedOrder.getId());

    DinningHallContext.getInstance().addOrder(receivedOrder);

    resp.getWriter().write("Received order with ID: " + receivedOrder.getId());
    resp.getWriter().flush();
    resp.getWriter().close();
  }
}
