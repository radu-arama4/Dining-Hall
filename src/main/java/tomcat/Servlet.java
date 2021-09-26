package tomcat;

import com.google.gson.Gson;
import entities.order.Order;
import entities.table.Table;
import entities.waiter.Waiter;
import util.DinningHallContext;
import util.JsonUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

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

    List<Waiter> waiters = DinningHallContext.getInstance().getWaiters();

    System.out.println("Received ready order with ID:" + receivedOrder.getId());

    for (Waiter waiter:waiters){
      List<Table> waitingTables = waiter.getWaitingTables();

      if(waitingTables.stream().anyMatch(o-> o.getCurrentOrder().getId() == receivedOrder.getId())){
        waiter.serveOrder(receivedOrder);
      }
    }

    resp.getWriter().write("Received order with ID: " + receivedOrder.getId());
    resp.getWriter().flush();
    resp.getWriter().close();
  }
}
