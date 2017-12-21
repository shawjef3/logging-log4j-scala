/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */
package org.apache.logging.log4j.scala

import org.apache.logging.log4j.message.Message
import org.apache.logging.log4j.{Level, Marker}
import org.apache.logging.log4j.spi.AbstractLogger
import scala.collection.mutable

/**
  * There is a test logger at [[org.apache.logging.log4j.TestLogger]],
  * but this one allows much finer assertions. You can check each argument
  * passed to [[TestLogger#logMessage]], rather than a special String
  * format.
  */
class TestLogger extends AbstractLogger {
  
  val entries = mutable.Buffer.empty[(String, Level, Marker, Message, Throwable)]

  override def logMessage(fqcn: String, level: Level, marker: Marker, msg: Message, throwable: Throwable): Unit = {
    entries += ((fqcn, level, marker, msg, throwable))
  }

  override def isEnabled(
    level: Level,
    marker: Marker,
    message: scala.Any,
    t: Throwable
  ) = true

  override def isEnabled(
    level: Level,
    marker: Marker,
    message: CharSequence,
    t: Throwable
  ): Boolean = true

  override def isEnabled(
    level: Level,
    marker: Marker,
    message: Message,
    t: Throwable
  ): Boolean = true

  override def isEnabled(
    level: Level,
    marker: Marker,
    message: String,
    p0: scala.Any,
    p1: scala.Any,
    p2: scala.Any,
    p3: scala.Any,
    p4: scala.Any,
    p5: scala.Any,
    p6: scala.Any,
    p7: scala.Any
  ): Boolean = true

  override def isEnabled(
    level: Level,
    marker: Marker,
    message: String,
    p0: scala.Any,
    p1: scala.Any,
    p2: scala.Any,
    p3: scala.Any,
    p4: scala.Any,
    p5: scala.Any,
    p6: scala.Any,
    p7: scala.Any,
    p8: scala.Any
  ): Boolean = true

  override def isEnabled(
    level: Level,
    marker: Marker,
    message: String,
    p0: scala.Any,
    p1: scala.Any
  ): Boolean = true

  override def isEnabled(
    level: Level,
    marker: Marker,
    message: String,
    p0: scala.Any
  ): Boolean = true

  override def isEnabled(
    level: Level,
    marker: Marker,
    message: String,
    params: AnyRef*
  ): Boolean = true

  override def isEnabled(
    level: Level,
    marker: Marker,
    message: String
  ): Boolean = true

  override def isEnabled(
    level: Level,
    marker: Marker,
    message: String,
    t: Throwable
  ): Boolean = true

  override def isEnabled(
    level: Level,
    marker: Marker,
    message: String,
    p0: scala.Any,
    p1: scala.Any,
    p2: scala.Any,
    p3: scala.Any,
    p4: scala.Any,
    p5: scala.Any,
    p6: scala.Any
  ): Boolean = true

  override def isEnabled(
    level: Level,
    marker: Marker,
    message: String,
    p0: scala.Any,
    p1: scala.Any,
    p2: scala.Any,
    p3: scala.Any,
    p4: scala.Any,
    p5: scala.Any
  ): Boolean = true

  override def isEnabled(
    level: Level,
    marker: Marker,
    message: String,
    p0: scala.Any,
    p1: scala.Any,
    p2: scala.Any,
    p3: scala.Any,
    p4: scala.Any
  ): Boolean = true

  override def isEnabled(
    level: Level,
    marker: Marker,
    message: String,
    p0: scala.Any,
    p1: scala.Any,
    p2: scala.Any,
    p3: scala.Any
  ): Boolean = true

  override def isEnabled(
    level: Level,
    marker: Marker,
    message: String,
    p0: scala.Any,
    p1: scala.Any,
    p2: scala.Any
  ): Boolean = true

  override def isEnabled(
    level: Level,
    marker: Marker,
    message: String,
    p0: scala.Any,
    p1: scala.Any,
    p2: scala.Any,
    p3: scala.Any,
    p4: scala.Any,
    p5: scala.Any,
    p6: scala.Any,
    p7: scala.Any,
    p8: scala.Any,
    p9: scala.Any
  ): Boolean = true

  override def getLevel: Level = Level.ALL
}
