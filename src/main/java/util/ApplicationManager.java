package util;

import org.apache.catalina.LifecycleException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import tomcat.TomcatManager;

public class ApplicationManager {
  private static final Logger logger = LogManager.getLogger(ApplicationManager.class);
  private static TomcatManager tomcatManager = new TomcatManager();

  public static void startApplication() {
    Thread serverThread = new Thread(tomcatManager);
    Properties.readProperties();
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
