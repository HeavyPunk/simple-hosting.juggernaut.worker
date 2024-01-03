package consumer

import cats.effect.kernel.Resource
import cats.effect.IO
import lepus.client.Connection

import cats.effect.IO
import com.comcast.ip4s.* // 1

import lepus.client.* // 2
import lepus.protocol.domains.*  // 3

class RabbitMqWrapper(connection: Resource[IO, Connection[IO]]) {
    def getStream(queueInfo: QueueInfo): fs2.Stream[IO, DeliveredMessage[String]] = {
        val channel = for {
            con <- connection
            ch <- con.channel
        } yield ch

        val consumer = fs2.Stream
            .resource(channel)
            .flatMap(_.messaging.consume[String](
                QueueName.from(queueInfo.name).getOrElse(QueueName.autoGen),
                mode = ConsumeMode.NackOnError
            ))
        consumer
    }
}
