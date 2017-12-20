package org.apache.logging.log4j.scala

object Test extends App {
  import org.apache.logging.log4j.Level
  class F extends Logging {
    def f = logger.traced(Level.TRACE) {
      logger.info("hi")
      3
    }

  }
  (new F).f
}
