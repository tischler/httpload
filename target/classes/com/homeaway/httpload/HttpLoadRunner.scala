package com.homeaway.httpload

import java.io._
import scala.io._
import scala.reflect.BeanProperty

class HttpLoadRunner {

	/** should verbosity be on? */
	@BeanProperty var verbose:Boolean = false

	/** file name to parse */
	@BeanProperty var fileName:String = null
 
	/** Profiles the duration of the block and returns time in nanoseconds as a long **/
	def time[R](name: String)(block: => R): Long = {
			val startTime = System.nanoTime()
			block
			val diff = (System.nanoTime() - startTime)
			diff / 1000000
	}

	def process() = { 
	  
	  val counter = new Counter
	  counter.start
   
	  val httpActor = new HttpActor
	  httpActor.counter = counter
	  httpActor.start
   
   
	  verbose_msg("Starting httpload")
	  verbose_msg("loading file" + fileName)

	  var file:File = null
	  var fileSource:Source = null

	  try { 
		  file = new File(fileName)
		  fileSource = Source.fromFile(file) 
	  } catch { 
	  	case fnfe: FileNotFoundException => { 
	  		println("File not found")
	  	}
	  }

	  var startTime = System.nanoTime()
	  // send off each line of the file for processing after removing the whitespace
	  fileSource.getLines.map{ _.trim }.foreach { (url) => httpActor ! GETRequest(url) } 
	  var totalTime = (System.nanoTime()-startTime)/100000
	  
	  System.out.println("********  Read through file in " + totalTime)
   
	}
 
	/* If the -v flag is set, print the message */
	def verbose_msg(msg: String) = { if(verbose) { println(msg) } }
  
}

