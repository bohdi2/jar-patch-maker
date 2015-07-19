package org.bohdi.tools.patcher

import java.util.HashSet
import java.util.Set

object SetUtil {
  def intersection[T](setA: Set[T], setB: Set[T]): Set[T] = {
    val tmp: Set[T] = new HashSet[T](setA)
    tmp.retainAll(setB)
    tmp
  }

  def difference[T](setA: Set[T], setB: Set[T]): Set[T] = {
    val tmp: Set[T] = new HashSet[T](setA)
    tmp.removeAll(setB)
    return tmp
  }
}
