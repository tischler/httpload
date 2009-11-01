import sbt._

class HelloWorldProject(info: ProjectInfo) extends DefaultProject(info)
{
  val dispatch = "net.databinder" % "dispatch_2.7.5" % "0.4.2"
  
  val actorsCorePoolSize = systemOptional[Int]("actors.corePoolSize",100)
}
