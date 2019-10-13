package com.github.jelmer.luminary

import org.slf4s.{ Logger, Logging => SLF4SLogging }

trait Logging extends SLF4SLogging {
  @inline
  protected[this] lazy val logger: Logger = log
}

