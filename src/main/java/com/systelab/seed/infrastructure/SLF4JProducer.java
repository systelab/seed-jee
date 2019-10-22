package com.systelab.seed.infrastructure;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class SLF4JProducer {

   @Produces
   public Logger producer(InjectionPoint ip){
      return LoggerFactory.getLogger(ip.getMember().getDeclaringClass().getName());
   }
}
