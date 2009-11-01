package com.homeaway.httpload

object HttpLoad {
	def main(argv : Array[String]) : Unit = { 
	 
	  if(argv.length==0) { 
		  error("No parameters given")
		  printUsage
		  return 0 
	  }
  
	  // split the command line args between hyphenated options and non-hyphenated args
	  val (opts, args) = argv(0).split(" ").partition{ _.startsWith("-") } 
	 
	  // split the name/values or convert the boolean style arguments into name/value pairs w/ booleans
	  val optsMap = Map() ++ opts.map { x =>  
	  	val pair = x.split("-{1,2}")(1).split("=")
	  	if(pair.length==1) (pair(0), "true") else (pair(0), pair(1))
	  } 

	  // parse the options
	  val verboseOption = optsMap.getOrElse("v", "false").toBoolean
	  val urlCount = optsMap.getOrElse("number", 20)
	  var threadCount = optsMap.getOrElse("threads", 20)
   
	  val fileName = optsMap.getOrElse("file", "").toString
	  if(fileName == "") { 
		  error("No filename specified")
		  printUsage()
		  return 0
	  }

	  printSystemInfo()
   
	  // create the load runner
	  var httpLoad = new HttpLoadRunner() 
	  httpLoad.verbose = verboseOption
	  httpLoad.fileName = fileName
	  httpLoad.process()
   
	  System.out.println("*********** DONE ***********")
	}
	
	def printSystemInfo() = { 
		println("Pool size: " + System.getProperty("actors.corePoolSize"))
		println("Actor Time Freq" + System.getProperty("actors.timeFreq"))
		println("Max pool size: " + System.getProperty("actors.maxPoolSize"))
	}
	/** prints the error */
	def error(error: String) = { println("Error:" + error) }
 
	def printUsage() = { 
	  println("Usage:  httpload [options] --file=[filename.txt]")
	  println("--file=[filename.txt] : filename of list of URLs to run")
	  println("-v || --verbose : turns verbosity on")
	}
 }
