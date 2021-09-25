package util;

import org.apache.catalina.LifecycleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tomcat.TomcatManager;

public class ApplicationManager {
  private static Logger logger = LoggerFactory.getLogger(ApplicationManager.class);
  private static TomcatManager tomcatManager = new TomcatManager();

  public static void startApplication() {
    Thread serverThread = new Thread(tomcatManager);
    serverThread.start();
    logger.info("App started!");
  }

  public static void closeApplication() {
    try {
      TomcatManager.stopServer();
    } catch (LifecycleException e) {
      logger.error(e.getMessage());
    }
    logger.info("App stopped!");
  }
}
