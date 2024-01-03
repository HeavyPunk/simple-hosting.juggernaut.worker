// import common.configuration.YamlFileConfigurationProvider
// import common.configuration.Settings

// @main def main: Unit = {
//     val configPath = "settings.yml"
//     val config = YamlFileConfigurationProvider(configPath)
//         .getConfig
// }
package example

import cats.effect.IO
import cats.effect.IOApp
import lepus.client.*
import lepus.protocol.domains.*

import scala.concurrent.duration.*
import com.comcast.ip4s.Host
import com.comcast.ip4s.Port

object HelloWorld extends IOApp.Simple {

  private val exchange = ExchangeName.default

  def app(con: Connection[IO]) = con.channel.use(ch =>
    for {
      _ <- IO.println(con.capabilities.toFieldTable)
      _ <- ch.exchange.declare(ExchangeName("events"), ExchangeType.Topic)
      q <- ch.queue.declare(autoDelete = true)
      q <- IO.fromOption(q)(new Exception())
      print = ch.messaging
        .consume[String](q.queue, mode = ConsumeMode.NackOnError)
        .printlns
      publish = fs2.Stream
        .awakeEvery[IO](1.second)
        .map(_.toMillis)
        .evalTap(l => IO.println(s"publishing $l"))
        .map(l => Message(l.toString()))
        .evalMap(ch.messaging.publish(exchange, q.queue, _))
      _ <- IO.println(q)

      _ <- print.merge(publish).interruptAfter(10.seconds).compile.drain
    } yield ()
  )

  private val connect =
    // connect to default port
    LepusClient[IO](
        host = Host.fromString("localhost").get,
        port = Port.fromInt(5672).get,
        username = "rmuser",
        password = "rmpassword",
        vhost = Path("/"),
        config = ConnectionConfig.default,
        debug = true
    )
    // or connect to the TLS port
    // for more advanced ssl see SSLExample.scala under .jvm directory
    //
    // con <- LepusClient[IO](debug = true,port=port"5671", ssl = SSL.Trusted)

  override def run: IO[Unit] = connect.use(app)

}

