import java.io._
import java.lang.Thread
import scala.io._
import scala.actors.Actor._
import scala.actors._
import dispatch._
import Http._

val (opts, args) = argv.partition{ _.startsWith("-") } 

val optsMap = Map() ++ opts.map { x => 
  val pair = x.split("-{1,2}")(1).split("=")
  if(pair.length ==1) (pair(0), "true") else (pair(0), pair(1))
} 

val verbose = optsMap.getOrElse("v", "false").toBoolean
val number = optsMap.getOrElse("n", "0").toInt
val fileName = optsMap.getOrElse("file", "").toString

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
    exit
  }
}

val caller = self

/* Message for requesting GETs of a url */
case class GETRequest(url:String) 
case class Complete(sender:Actor)

val requestActor = actor { 

  while(true) { 
    receive { 
      case GETRequest(url) => { 
        println("recieved GET: " + url)
        val request = :/(url) 
        val http = new Http
        http(request >|)
        println("trying " + url)
      }
      case Complete(sender) => { 
        sender ! Complete(self)
      }
    }
  }
}

fileSource.getLines.map{ _.trim }.foreach { (url) => requestActor ! GETRequest(url) }
requestActor ! Complete(self)

Thread.sleep(2000)

/** Profiles the duration of the block and returns time in nanoseconds as a long **/
def time[R](name: String)(block: => R): Long = { 
  val startTime = System.nanoTime()
  block
  val diff = (System.nanoTime() - startTime) 
  diff / 1000000
}

/* If the -v flag is set, print the message */
def verbose_msg(msg: String) = { if(verbose) { println(msg) } }



