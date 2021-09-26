package tomcat;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import java.io.File;

public class TomcatManager implements Runnable {
  private static Tomcat tomcat;

  public static void startServer() throws LifecycleException {
    tomcat = new Tomcat();

    tomcat.setPort(8080);
    tomcat.setHostname("localhost");
    String appBase = ".";
    tomcat.getHost().setAppBase(appBase);

    File docBase = new File(System.getProperty("java.io.tmpdir"));
    Context context = tomcat.addContext("", docBase.getAbsolutePath());

    Class servletClass = Servlet.class;
    Tomcat.addServlet(context, servletClass.getSimpleName(), servletClass.getName());
    context.addServletMappingDecoded("/home/*", servletClass.getSimpleName());

    Class filterClass = MyFilter.class;
    FilterDef myFilterDef = new FilterDef();
    myFilterDef.setFilterClass(filterClass.getName());
    myFilterDef.setFilterName(filterClass.getSimpleName());
    context.addFilterDef(myFilterDef);

    FilterMap myFilterMap = new FilterMap();
    myFilterMap.setFilterName(filterClass.getSimpleName());
    myFilterMap.addURLPattern("/my-servlet/*");
    context.addFilterMap(myFilterMap);

    tomcat.start();
    tomcat.getServer().await();
  }

  public static void stopServer() throws LifecycleException {
    tomcat.stop();
  }

  @Override
  public void run() {
    try {
      startServer();
    } catch (LifecycleException e) {
      e.printStackTrace();
    }
  }
}
