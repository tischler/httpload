package com.homeaway.httpload

import scala.actors.Actor
import scala.actors._
import dispatch._

case class ReportGET(millis: Long, url: String, response: Int)
class Counter extends Actor {

	var counter : Int = 0
	def act = { 
    	while(true) { 
    		react { 
    		  case ReportGET(millis, url, response) => 
    		    counter+=1
    		    System.out.println("REPORT: " + counter + ":" + url + ":" + millis)
    		}
    	}
	}
}
