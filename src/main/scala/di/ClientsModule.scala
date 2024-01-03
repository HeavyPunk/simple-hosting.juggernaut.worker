package di

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import lepus.client.LepusClient
import com.comcast.ip4s.*
import lepus.client.*
import cats.effect.IO
import lepus.protocol.domains.*
import com.google.inject.Guice
import common.configuration.Settings
import common.configuration.ConfigurationProvider
import net.codingwell.scalaguice.InjectorExtensions.ScalaInjector
import consumer.RabbitMqWrapper
import redis.clients.jedis.Jedis
import consumer.RedisWrapper
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

class ClientsModule extends AbstractModule with ScalaModule {
    override def configure(): Unit = {
        val injector = Guice.createInjector(SettingsModule())
        val configurationProvider = injector.instance[ConfigurationProvider[Settings]]
        val (err, config) = configurationProvider
            .getConfig
            .tryGetValue
        if (err != null)
            throw new IllegalStateException(s"Configuration provider error: ${err.toString()}")

        val connection = LepusClient[IO](
            host = Host.fromString(config.rabbitMqHost).get,
            port = Port.fromInt(config.rabbitMqPort).get,
            username = config.rabbitMqUsername,
            password = config.rabbitMqPassword,
            vhost = Path("/"),
            config = ConnectionConfig.default,
            debug = true
        )
        val rabbitMqWrapper = RabbitMqWrapper(connection)

        bind[RabbitMqWrapper].toInstance(rabbitMqWrapper)

        val jedisPool = JedisPool(JedisPoolConfig(), config.redisHost, config.redisPort)
        bind[RedisWrapper].toInstance(RedisWrapper(jedisPool))
    }
}
