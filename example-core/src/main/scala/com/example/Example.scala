package com.example

import com.twitter.algebird._

case class Example(
  x: Int,
  y: Long)

object Foo {
  def main(args: Array[String]) = {
    println("Hello from Foo")
    
    val m = (0 until 1000).map(x => x % 17 -> x).toMap
    val r = MapAlgebra.sumByKey(m).toSeq.sorted
    r.foreach{ case (x, y) =>
      println(s"Entry: $x -> $y")
    }
  }
}
