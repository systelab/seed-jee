package com.systelab.seed.infrastructure.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApplicationProperties {

  //default values
  private static final long DEFAULT_RATE_LIMITING_MINUTES_DURATION = 10;
  private static final int DEFAULT_RATE_LIMITING_LIMIT_OF_REQUESTS = 100;
  //keys
  private static final String RATE_LIMITING_MINUTES_DURATION = "RATE_LIMITING_MINUTES_DURATION";
  private static final String RATE_LIMITING_LIMIT_OF_REQUESTS = "RATE_LIMITING_LIMIT_OF_REQUESTS";


  private static final Logger logger = Logger.getLogger(ApplicationProperties.class.getName());
  private static ApplicationProperties instance = null;
  private Properties props;

  private ApplicationProperties() {

    props = new Properties();
    InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties");
    try {
      props.load(input);
    } catch (IOException ex) {
      logger.log(Level.SEVERE, "error reading application.properties", ex);
    }
  }

  public static ApplicationProperties getInstance() {
    if (instance == null) {
      instance = new ApplicationProperties();
    }
    return instance;
  }

  public Long getRateLimitingMinutesDuration() {
    Long value = DEFAULT_RATE_LIMITING_MINUTES_DURATION;
    try {
      value = getLongProperty(RATE_LIMITING_MINUTES_DURATION);
    } catch (Exception e) {
    }
    return value;
  }

  public Integer getRateLimitingRequestLimit() {
    Integer value = DEFAULT_RATE_LIMITING_LIMIT_OF_REQUESTS;
    try {
      value = getIntegerProperty(RATE_LIMITING_LIMIT_OF_REQUESTS);
    } catch (Exception e) {
    }
    return value;
  }

  private String getProperty(String key) {
    return props.getProperty(key);
  }

  private Long getLongProperty(String key) {
    final String value = getProperty(key);
    return value != null ? Long.valueOf(Long.parseLong(value))
        : null;
  }

  private Integer getIntegerProperty(String key) {
    final String value = getProperty(key);
    return value != null ? Integer.valueOf(Integer.parseInt(value))
        : null;
  }
}
