package util;

import lombok.SneakyThrows;

import java.io.InputStream;

public class Properties {
  public static int TIME_UNIT;
  public static int NR_OF_WAITERS;
  public static int NR_OF_TABLES;
  public static int NR_OF_ORDERS;

  @SneakyThrows
  public static void readProperties() {
    InputStream s = Properties.class.getResourceAsStream("/application.properties");

    java.util.Properties props = new java.util.Properties();
    props.load(s);

    TIME_UNIT = Integer.parseInt(props.getProperty("time_unit"));
    NR_OF_WAITERS = Integer.parseInt(props.getProperty("nr_of_waiters"));
    NR_OF_TABLES = Integer.parseInt(props.getProperty("nr_of_tables"));
    NR_OF_ORDERS = Integer.parseInt(props.getProperty("nr_of_orders"));
  }
}
