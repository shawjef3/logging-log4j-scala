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

import org.apache.logging.log4j.message.{EntryMessage, Message, SourceLocation}
import org.apache.logging.log4j.spi.AbstractLogger
import org.apache.logging.log4j.{Level, Marker}
import scala.language.experimental.macros
import scala.reflect.macros.blackbox

/**
  * Inspired from [[https://github.com/typesafehub/scalalogging ScalaLogging]].
  */
private object LoggerMacro {

  type LoggerContext = blackbox.Context { type PrefixType = Logger }

  def getSourceLocation(c: LoggerContext): c.Expr[SourceLocation] = {
    val term = c.internal.enclosingOwner.asTerm match {
      case t if t.owner.isMethod => t.owner
      case t => t
    }
    import c.universe._

    val className = c.Expr(Literal(Constant(term.owner.fullName)))
    val methodName = c.Expr(Literal(Constant(if (term.isMethod) term.asMethod.name.decodedName.toString else null)))
    val fileName = c.Expr(Literal(Constant(c.enclosingPosition.pos.source.file.name)))
    val line = c.Expr(Literal(Constant(c.enclosingPosition.line)))
    c.universe.reify(new SourceLocation(className.splice, methodName.splice, fileName.splice, line.splice))
  }

  def traced[A](c: LoggerContext)(level: c.Expr[Level])(f: c.Expr[A]) = {
    val (term, params) = c.internal.enclosingOwner.asTerm match {
      case t if t.owner.isMethod => (t.owner, t.owner.asMethod.paramLists)
      case t => (t, List.empty)
    }
    import c.universe._

    val delegate = Select(c.prefix.tree, TermName("delegate"))
    val entry = Select(delegate, TermName("traceEntry"))
    val exit = Select(delegate, TermName("traceExit"))
    val throwing = Select(delegate, TermName("throwing"))

    //TODO: log params
    q"""
       val location = ${getSourceLocation(c)}
       val entry = $entry(location, null: String)
       try {
         val result = $f
         $exit(location, entry, result)
       } catch {
         case scala.util.control.NonFatal(e) =>
           $throwing(location, $level, e)
           throw e
       }
     """
  }

  def fatalMarkerMsg(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[Message]) =
    logMarkerMsg(c)(c.universe.reify(Level.FATAL), marker, message)

  def fatalMarkerCseq(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[CharSequence]) =
    logMarkerCseq(c)(c.universe.reify(Level.FATAL), marker, message)

  def fatalMarkerObject(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[AnyRef]) =
    logMarkerObject(c)(c.universe.reify(Level.FATAL), marker, message)

  def fatalMarkerMsgThrowable(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[Message], cause: c.Expr[Throwable]) =
    logMarkerMsgThrowable(c)(c.universe.reify(Level.FATAL), marker, message, cause)

  def fatalMarkerCseqThrowable(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[CharSequence], cause: c.Expr[Throwable]) =
    logMarkerCseqThrowable(c)(c.universe.reify(Level.FATAL), marker, message, cause)

  def fatalMarkerObjectThrowable(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[AnyRef], cause: c.Expr[Throwable]) =
    logMarkerObjectThrowable(c)(c.universe.reify(Level.FATAL), marker, message, cause)

  def fatalMsg(c: LoggerContext)(message: c.Expr[Message]) =
    logMsg(c)(c.universe.reify(Level.FATAL), message)

  def fatalCseq(c: LoggerContext)(message: c.Expr[CharSequence]) =
    logCseq(c)(c.universe.reify(Level.FATAL), message)

  def fatalObject(c: LoggerContext)(message: c.Expr[AnyRef]) =
    logObject(c)(c.universe.reify(Level.FATAL), message)

  def fatalMsgThrowable(c: LoggerContext)(message: c.Expr[Message], cause: c.Expr[Throwable]) =
    logMsgThrowable(c)(c.universe.reify(Level.FATAL), message, cause)

  def fatalCseqThrowable(c: LoggerContext)(message: c.Expr[CharSequence], cause: c.Expr[Throwable]) =
    logCseqThrowable(c)(c.universe.reify(Level.FATAL), message, cause)

  def fatalObjectThrowable(c: LoggerContext)(message: c.Expr[AnyRef], cause: c.Expr[Throwable]) =
    logObjectThrowable(c)(c.universe.reify(Level.FATAL), message, cause)


  def errorMarkerMsg(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[Message]) =
    logMarkerMsg(c)(c.universe.reify(Level.ERROR), marker, message)

  def errorMarkerCseq(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[CharSequence]) =
    logMarkerCseq(c)(c.universe.reify(Level.ERROR), marker, message)

  def errorMarkerObject(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[AnyRef]) =
    logMarkerObject(c)(c.universe.reify(Level.ERROR), marker, message)

  def errorMarkerMsgThrowable(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[Message], cause: c.Expr[Throwable]) =
    logMarkerMsgThrowable(c)(c.universe.reify(Level.ERROR), marker, message, cause)

  def errorMarkerCseqThrowable(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[CharSequence], cause: c.Expr[Throwable]) =
    logMarkerCseqThrowable(c)(c.universe.reify(Level.ERROR), marker, message, cause)

  def errorMarkerObjectThrowable(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[AnyRef], cause: c.Expr[Throwable]) =
    logMarkerObjectThrowable(c)(c.universe.reify(Level.ERROR), marker, message, cause)

  def errorMsg(c: LoggerContext)(message: c.Expr[Message]) =
    logMsg(c)(c.universe.reify(Level.ERROR), message)

  def errorCseq(c: LoggerContext)(message: c.Expr[CharSequence]) =
    logCseq(c)(c.universe.reify(Level.ERROR), message)

  def errorObject(c: LoggerContext)(message: c.Expr[AnyRef]) =
    logObject(c)(c.universe.reify(Level.ERROR), message)

  def errorMsgThrowable(c: LoggerContext)(message: c.Expr[Message], cause: c.Expr[Throwable]) =
    logMsgThrowable(c)(c.universe.reify(Level.ERROR), message, cause)

  def errorCseqThrowable(c: LoggerContext)(message: c.Expr[CharSequence], cause: c.Expr[Throwable]) =
    logCseqThrowable(c)(c.universe.reify(Level.ERROR), message, cause)

  def errorObjectThrowable(c: LoggerContext)(message: c.Expr[AnyRef], cause: c.Expr[Throwable]) =
    logObjectThrowable(c)(c.universe.reify(Level.ERROR), message, cause)


  def warnMarkerMsg(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[Message]) =
    logMarkerMsg(c)(c.universe.reify(Level.WARN), marker, message)

  def warnMarkerCseq(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[CharSequence]) =
    logMarkerCseq(c)(c.universe.reify(Level.WARN), marker, message)

  def warnMarkerObject(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[AnyRef]) =
    logMarkerObject(c)(c.universe.reify(Level.WARN), marker, message)

  def warnMarkerMsgThrowable(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[Message], cause: c.Expr[Throwable]) =
    logMarkerMsgThrowable(c)(c.universe.reify(Level.WARN), marker, message, cause)

  def warnMarkerCseqThrowable(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[CharSequence], cause: c.Expr[Throwable]) =
    logMarkerCseqThrowable(c)(c.universe.reify(Level.WARN), marker, message, cause)

  def warnMarkerObjectThrowable(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[AnyRef], cause: c.Expr[Throwable]) =
    logMarkerObjectThrowable(c)(c.universe.reify(Level.WARN), marker, message, cause)

  def warnMsg(c: LoggerContext)(message: c.Expr[Message]) =
    logMsg(c)(c.universe.reify(Level.WARN), message)

  def warnCseq(c: LoggerContext)(message: c.Expr[CharSequence]) =
    logCseq(c)(c.universe.reify(Level.WARN), message)

  def warnObject(c: LoggerContext)(message: c.Expr[AnyRef]) =
    logObject(c)(c.universe.reify(Level.WARN), message)

  def warnMsgThrowable(c: LoggerContext)(message: c.Expr[Message], cause: c.Expr[Throwable]) =
    logMsgThrowable(c)(c.universe.reify(Level.WARN), message, cause)

  def warnCseqThrowable(c: LoggerContext)(message: c.Expr[CharSequence], cause: c.Expr[Throwable]) =
    logCseqThrowable(c)(c.universe.reify(Level.WARN), message, cause)

  def warnObjectThrowable(c: LoggerContext)(message: c.Expr[AnyRef], cause: c.Expr[Throwable]) =
    logObjectThrowable(c)(c.universe.reify(Level.WARN), message, cause)


  def infoMarkerMsg(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[Message]) =
    logMarkerMsg(c)(c.universe.reify(Level.INFO), marker, message)

  def infoMarkerCseq(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[CharSequence]) =
    logMarkerCseq(c)(c.universe.reify(Level.INFO), marker, message)

  def infoMarkerObject(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[AnyRef]) =
    logMarkerObject(c)(c.universe.reify(Level.INFO), marker, message)

  def infoMarkerMsgThrowable(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[Message], cause: c.Expr[Throwable]) =
    logMarkerMsgThrowable(c)(c.universe.reify(Level.INFO), marker, message, cause)

  def infoMarkerCseqThrowable(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[CharSequence], cause: c.Expr[Throwable]) =
    logMarkerCseqThrowable(c)(c.universe.reify(Level.INFO), marker, message, cause)

  def infoMarkerObjectThrowable(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[AnyRef], cause: c.Expr[Throwable]) =
    logMarkerObjectThrowable(c)(c.universe.reify(Level.INFO), marker, message, cause)

  def infoMsg(c: LoggerContext)(message: c.Expr[Message]) =
    logMsg(c)(c.universe.reify(Level.INFO), message)

  def infoCseq(c: LoggerContext)(message: c.Expr[CharSequence]) =
    logCseq(c)(c.universe.reify(Level.INFO), message)

  def infoObject(c: LoggerContext)(message: c.Expr[AnyRef]) =
    logObject(c)(c.universe.reify(Level.INFO), message)

  def infoMsgThrowable(c: LoggerContext)(message: c.Expr[Message], cause: c.Expr[Throwable]) =
    logMsgThrowable(c)(c.universe.reify(Level.INFO), message, cause)

  def infoCseqThrowable(c: LoggerContext)(message: c.Expr[CharSequence], cause: c.Expr[Throwable]) =
    logCseqThrowable(c)(c.universe.reify(Level.INFO), message, cause)

  def infoObjectThrowable(c: LoggerContext)(message: c.Expr[AnyRef], cause: c.Expr[Throwable]) =
    logObjectThrowable(c)(c.universe.reify(Level.INFO), message, cause)


  def debugMarkerMsg(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[Message]) =
    logMarkerMsg(c)(c.universe.reify(Level.DEBUG), marker, message)

  def debugMarkerCseq(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[CharSequence]) =
    logMarkerCseq(c)(c.universe.reify(Level.DEBUG), marker, message)

  def debugMarkerObject(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[AnyRef]) =
    logMarkerObject(c)(c.universe.reify(Level.DEBUG), marker, message)

  def debugMarkerMsgThrowable(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[Message], cause: c.Expr[Throwable]) =
    logMarkerMsgThrowable(c)(c.universe.reify(Level.DEBUG), marker, message, cause)

  def debugMarkerCseqThrowable(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[CharSequence], cause: c.Expr[Throwable]) =
    logMarkerCseqThrowable(c)(c.universe.reify(Level.DEBUG), marker, message, cause)

  def debugMarkerObjectThrowable(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[AnyRef], cause: c.Expr[Throwable]) =
    logMarkerObjectThrowable(c)(c.universe.reify(Level.DEBUG), marker, message, cause)

  def debugMsg(c: LoggerContext)(message: c.Expr[Message]) =
    logMsg(c)(c.universe.reify(Level.DEBUG), message)

  def debugCseq(c: LoggerContext)(message: c.Expr[CharSequence]) =
    logCseq(c)(c.universe.reify(Level.DEBUG), message)

  def debugObject(c: LoggerContext)(message: c.Expr[AnyRef]) =
    logObject(c)(c.universe.reify(Level.DEBUG), message)

  def debugMsgThrowable(c: LoggerContext)(message: c.Expr[Message], cause: c.Expr[Throwable]) =
    logMsgThrowable(c)(c.universe.reify(Level.DEBUG), message, cause)

  def debugCseqThrowable(c: LoggerContext)(message: c.Expr[CharSequence], cause: c.Expr[Throwable]) =
    logCseqThrowable(c)(c.universe.reify(Level.DEBUG), message, cause)

  def debugObjectThrowable(c: LoggerContext)(message: c.Expr[AnyRef], cause: c.Expr[Throwable]) =
    logObjectThrowable(c)(c.universe.reify(Level.DEBUG), message, cause)


  def traceMarkerMsg(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[Message]) =
    logMarkerMsg(c)(c.universe.reify(Level.TRACE), marker, message)

  def traceMarkerCseq(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[CharSequence]) =
    logMarkerCseq(c)(c.universe.reify(Level.TRACE), marker, message)

  def traceMarkerObject(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[AnyRef]) =
    logMarkerObject(c)(c.universe.reify(Level.TRACE), marker, message)

  def traceMarkerMsgThrowable(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[Message], cause: c.Expr[Throwable]) =
    logMarkerMsgThrowable(c)(c.universe.reify(Level.TRACE), marker, message, cause)

  def traceMarkerCseqThrowable(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[CharSequence], cause: c.Expr[Throwable]) =
    logMarkerCseqThrowable(c)(c.universe.reify(Level.TRACE), marker, message, cause)

  def traceMarkerObjectThrowable(c: LoggerContext)(marker: c.Expr[Marker], message: c.Expr[AnyRef], cause: c.Expr[Throwable]) =
    logMarkerObjectThrowable(c)(c.universe.reify(Level.TRACE), marker, message, cause)

  def traceMsg(c: LoggerContext)(message: c.Expr[Message]) =
    logMsg(c)(c.universe.reify(Level.TRACE), message)

  def traceCseq(c: LoggerContext)(message: c.Expr[CharSequence]) =
    logCseq(c)(c.universe.reify(Level.TRACE), message)

  def traceObject(c: LoggerContext)(message: c.Expr[AnyRef]) =
    logObject(c)(c.universe.reify(Level.TRACE), message)

  def traceMsgThrowable(c: LoggerContext)(message: c.Expr[Message], cause: c.Expr[Throwable]) =
    logMsgThrowable(c)(c.universe.reify(Level.TRACE), message, cause)

  def traceCseqThrowable(c: LoggerContext)(message: c.Expr[CharSequence], cause: c.Expr[Throwable]) =
    logCseqThrowable(c)(c.universe.reify(Level.TRACE), message, cause)

  def traceObjectThrowable(c: LoggerContext)(message: c.Expr[AnyRef], cause: c.Expr[Throwable]) =
    logObjectThrowable(c)(c.universe.reify(Level.TRACE), message, cause)


  def logMarkerMsg(c: LoggerContext)(level: c.Expr[Level], marker: c.Expr[Marker], message: c.Expr[Message]) =
    c.universe.reify(
      if (c.prefix.splice.delegate.isEnabled(level.splice, marker.splice)) {
        c.prefix.splice.logMessage(level.splice, marker.splice, message.splice, null)
      }
    )

  def logMarkerCseq(c: LoggerContext)(level: c.Expr[Level], marker: c.Expr[Marker], message: c.Expr[CharSequence]) =
    c.universe.reify(
      if (c.prefix.splice.delegate.isEnabled(level.splice, marker.splice)) {
        c.prefix.splice.logMessage(level.splice, marker.splice, message.splice, getSourceLocation(c).splice, null)
      }
    )

  def logMarkerObject(c: LoggerContext)(level: c.Expr[Level], marker: c.Expr[Marker], message: c.Expr[AnyRef]) =
    c.universe.reify(
      if (c.prefix.splice.delegate.isEnabled(level.splice, marker.splice)) {
        c.prefix.splice.logMessage(level.splice, marker.splice, message.splice, getSourceLocation(c).splice, null)
      }
    )

  def logMarkerMsgThrowable(c: LoggerContext)(level: c.Expr[Level], marker: c.Expr[Marker], message: c.Expr[Message], cause: c.Expr[Throwable]) =
    c.universe.reify(
      if (c.prefix.splice.delegate.isEnabled(level.splice, marker.splice)) {
        c.prefix.splice.logMessage(level.splice, marker.splice, message.splice, cause.splice)
      }
    )

  def logMarkerCseqThrowable(c: LoggerContext)(level: c.Expr[Level], marker: c.Expr[Marker], message: c.Expr[CharSequence], cause: c.Expr[Throwable]) =
    c.universe.reify(
      if (c.prefix.splice.delegate.isEnabled(level.splice, marker.splice)) {
        c.prefix.splice.logMessage(level.splice, marker.splice, message.splice, getSourceLocation(c).splice, cause.splice)
      }
    )

  def logMarkerObjectThrowable(c: LoggerContext)(level: c.Expr[Level], marker: c.Expr[Marker], message: c.Expr[AnyRef], cause: c.Expr[Throwable]) =
    c.universe.reify(
      if (c.prefix.splice.delegate.isEnabled(level.splice, marker.splice)) {
        c.prefix.splice.logMessage(level.splice, marker.splice, message.splice, getSourceLocation(c).splice, cause.splice)
      }
    )

  def logMsg(c: LoggerContext)(level: c.Expr[Level], message: c.Expr[Message]) =
    c.universe.reify(
      if (c.prefix.splice.delegate.isEnabled(level.splice)) {
        c.prefix.splice.logMessage(level.splice, null, message.splice, null)
      }
    )

  def logCseq(c: LoggerContext)(level: c.Expr[Level], message: c.Expr[CharSequence]) =
    c.universe.reify(
      if (c.prefix.splice.delegate.isEnabled(level.splice)) {
        c.prefix.splice.logMessage(level.splice, null, message.splice, getSourceLocation(c).splice, null)
      }
    )

  def logObject(c: LoggerContext)(level: c.Expr[Level], message: c.Expr[AnyRef]) =
    c.universe.reify(
      if (c.prefix.splice.delegate.isEnabled(level.splice)) {
        c.prefix.splice.logMessage(level.splice, null, message.splice, getSourceLocation(c).splice, null)
      }
    )

  def logMsgThrowable(c: LoggerContext)(level: c.Expr[Level], message: c.Expr[Message], cause: c.Expr[Throwable]) =
    c.universe.reify(
      if (c.prefix.splice.delegate.isEnabled(level.splice)) {
        c.prefix.splice.logMessage(level.splice, null, message.splice, cause.splice)
      }
    )

  def logCseqThrowable(c: LoggerContext)(level: c.Expr[Level], message: c.Expr[CharSequence], cause: c.Expr[Throwable]) =
    c.universe.reify(
      if (c.prefix.splice.delegate.isEnabled(level.splice)) {
        c.prefix.splice.logMessage(level.splice, null, message.splice, getSourceLocation(c).splice, cause.splice)
      }
    )

  def logObjectThrowable(c: LoggerContext)(level: c.Expr[Level], message: c.Expr[AnyRef], cause: c.Expr[Throwable]) =
    c.universe.reify(
      if (c.prefix.splice.delegate.isEnabled(level.splice)) {
        c.prefix.splice.logMessage(level.splice, null, message.splice, getSourceLocation(c).splice, cause.splice)
      }
    )


  def traceEntry(c: LoggerContext)(): c.Expr[EntryMessage] =
    c.universe.reify(
      c.prefix.splice.delegate.traceEntry(getSourceLocation(c).splice)
    )

  def traceEntryParams(c: LoggerContext)(params: c.Expr[AnyRef]*): c.Expr[EntryMessage] = {
    import c.universe._
    val isEnabled = Apply(
      Select(Select(c.prefix.tree, TermName("delegate")), TermName("isEnabled")),
      List(
        reify(Level.TRACE).tree,
        reify(AbstractLogger.ENTRY_MARKER).tree,
        reify(null: AnyRef).tree,
        reify(null).tree
      )
    )


    val log = Apply(
      Select(Select(c.prefix.tree, TermName("delegate")), TermName("traceEntry")),
      getSourceLocation(c).tree :: reify(null: String).tree :: (params map (_.tree)).toList
    )
    c.Expr[EntryMessage](If(isEnabled, log, reify(null).tree))
  }


  def traceEntryMessage(c: LoggerContext)(message: c.Expr[Message]): c.Expr[EntryMessage] =
    c.universe.reify(
      if (c.prefix.splice.delegate.isEnabled(Level.TRACE, AbstractLogger.ENTRY_MARKER, null: AnyRef, null)) {
        c.prefix.splice.delegate.traceEntry(getSourceLocation(c).splice, message.splice)  // TODO should not do ifEnabled check
      } else {
        null
      }
    )

  def traceExit(c: LoggerContext)(): c.Expr[Unit] =
    c.universe.reify(
      c.prefix.splice.delegate.traceExit(getSourceLocation(c).splice)
    )

  def traceExitResult[R: c.WeakTypeTag](c: LoggerContext)(result: c.Expr[R]): c.Expr[R] =
    c.universe.reify(
      c.prefix.splice.delegate.traceExit(getSourceLocation(c).splice, result.splice)
    )

  def traceExitEntryMessage(c: LoggerContext)(entryMessage: c.Expr[EntryMessage]): c.Expr[Unit] =
    c.universe.reify(
      c.prefix.splice.delegate.traceExit(getSourceLocation(c).splice, entryMessage.splice)
    )

  def traceExitEntryMessageResult[R: c.WeakTypeTag](c: LoggerContext)(entryMessage: c.Expr[EntryMessage], result: c.Expr[R]): c.Expr[R] =
    c.universe.reify(
      c.prefix.splice.delegate.traceExit(getSourceLocation(c).splice, entryMessage.splice, result.splice)
    )

  def traceExitMessageResult[R: c.WeakTypeTag](c: LoggerContext)(message: c.Expr[Message], result: c.Expr[R]): c.Expr[R] =
    c.universe.reify(
      {
        if (message.splice != null && c.prefix.splice.delegate.isEnabled(Level.TRACE, AbstractLogger.EXIT_MARKER, message.splice, null)) {
          c.prefix.splice.delegate.traceExit(getSourceLocation(c).splice, message.splice, result.splice)  // TODO should not do ifEnabled check
        }
        result.splice
      }
    )

  def throwing[T <: Throwable: c.WeakTypeTag](c: LoggerContext)(t: c.Expr[T]): c.Expr[T] =
    c.universe.reify(
      c.prefix.splice.delegate.throwing(getSourceLocation(c).splice, t.splice)
    )

  def throwingLevel[T <: Throwable: c.WeakTypeTag](c: LoggerContext)(level: c.Expr[Level], t: c.Expr[T]): c.Expr[T] =
    c.universe.reify(
      c.prefix.splice.delegate.throwing(getSourceLocation(c).splice, level.splice, t.splice)
    )

  def catching(c: LoggerContext)(t: c.Expr[Throwable]): c.Expr[Unit] =
    c.universe.reify(
      c.prefix.splice.delegate.catching(getSourceLocation(c).splice, t.splice)
    )

  def catchingLevel(c: LoggerContext)(level: c.Expr[Level], t: c.Expr[Throwable]): c.Expr[Unit] =
    c.universe.reify(
      c.prefix.splice.delegate.catching(getSourceLocation(c).splice, level.splice, t.splice)
    )

}
