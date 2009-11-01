package com.homeaway.httpload

import scala.actors._ 
import scala.actors.Actor._
import dispatch._
import scala.reflect.BeanProperty

/** Message to request a GET */
case class GETRequest(url:String)

/** Actor responsible for processing HTTP requests */
class HttpActor extends Actor {
 
	@BeanProperty var counter : Counter = null
  
  def act = { 
	loop { 
		react { 
		  case GETRequest(url) => { 
			  // call me a java dev, but this is fugly... wtfs up w/ the symbol functions
			  // :/ converts string to request and >| converts the request to a handler that doesn't pipe the output anywhere
			  val duration = time("url") {  new Http()(:/(url)>|) } 
			  counter ! ReportGET(duration, url, 200)
			  System.out.println(url + " took " + duration.toString + " ms on " + Thread.currentThread.getName)
     
		  }
	   }
    }
  }
  
  /** Profiles the duration of the block and returns time in nanoseconds as a long **/
  def time[R](name: String)(block: => R): Long = {
	val startTime = System.nanoTime()
	block
	(System.nanoTime() - startTime) / 1000000
  } 
  
}
