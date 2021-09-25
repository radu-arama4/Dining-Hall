package tomcat;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MyFilter implements Filter {
  public void init(FilterConfig filterConfig) throws ServletException {}

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletResponse httpResponse = (HttpServletResponse) response;
    httpResponse.addHeader("myHeader", "myHeaderValue");
    chain.doFilter(request, httpResponse);
  }

  public void destroy() {}
}
