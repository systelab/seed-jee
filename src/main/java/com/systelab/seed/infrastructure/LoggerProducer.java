package com.systelab.seed.infrastructure;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerProducer {

   @Produces
   public Logger producer(InjectionPoint ip) {
      return LoggerFactory.getLogger(
          ip.getMember().getDeclaringClass().getName());
   }
}
