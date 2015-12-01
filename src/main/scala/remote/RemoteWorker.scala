package remote

import java.net.InetAddress

import akka.actor._
import org.apache.commons.codec.digest.Crypt

object RemoteWorker extends App {
  val system = ActorSystem("Workers")
  val masterHost = "smaster"
  val masterSubscriber = s"akka.tcp://AkkaLoader@$masterHost:5500/user/Subscriber"
  val keepAliveDelay = 2000 //ms

  val remoteActor = system.actorOf(Props[RemoteWorker], name = "RemoteWorker")

  while (true) {
    subscribeToMaster()
    Thread.sleep(keepAliveDelay)
  }

  def subscribeToMaster() = {
    val masterActor = system.actorSelection(masterSubscriber)
    val host = InetAddress.getLocalHost.getHostAddress

    println("Registered Remote Worker host = " + host)

    masterActor ! host
  }

}

class RemoteWorker extends Actor {
  def receive = {
    case msg: String =>
      execute(msg)
      sender ! "ACK"
  }

  private def execute(hash: String): Unit = {
    if (hash.nonEmpty) {
      val start = System.currentTimeMillis()
      val crypto = Crypt.crypt(hash)
      println(s"Encrypted (${System.currentTimeMillis - start}) Ms. ")
    }
  }
}
