package consumer

import common.Monad
import common.TaskStatus
import com.google.inject.Inject
import redis.clients.jedis.Jedis
import common.ResultMonad
import redis.clients.jedis.JedisPool

class RedisWrapper @Inject() (jedisPool: JedisPool) {
    def setTaskStatus(taskId: String, status: TaskStatus): Monad[Exception, Boolean] = {
        ResultMonad(true)
    }
}
