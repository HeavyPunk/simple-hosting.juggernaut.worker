package consumer

import worker.Worker
import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import com.google.inject.Inject
import parser.TaskParser
import worker.WorkerFactory
import scala.reflect.ClassTag
import common.ResultMonad
import common.configuration.Settings
import cats.effect.IO
import common.TaskStatus
import common.TaskModel

class Consumer[TTaskContext : ClassTag, TWorker <: Worker[TTaskContext, _]] @Inject() (
    rabbitClient: RabbitMqWrapper,
    redis: RedisWrapper,
    taskParser: TaskParser,
    settings: Settings
) {
    val threadPool = Executors.newFixedThreadPool(settings.threadsCount + 1) // NOTE: было бы круто всё-таки выделять потоки per-request
    val executionContext = ExecutionContext.fromExecutor(threadPool)
    given ExecutionContext = executionContext
    var executionTask: Future[Unit] = null
    var cancellation = false

    var queue: QueueInfo = null
    var workerFactory: WorkerFactory[TWorker] = null

    def configureFor(queue: QueueInfo, workerFactory: WorkerFactory[TWorker]) = {
        this.queue = queue
        this.workerFactory = workerFactory
    }

    def startScanTask(): Unit = {
        if !isReadyForWork() then throw new IllegalStateException("ScanTask is not ready for work")
        executionTask = Future {
            while(!cancellation) {
                val stream = rabbitClient.getStream(queue)
                val futures = stream.map(queued => Future {
                    val worker = workerFactory.createWorker()
                    taskParser.parse[TaskModel[TTaskContext]](queued.message.payload)
                        .flatMap(context => {
                            redis.setTaskStatus(context.taskId, TaskStatus.InProgress)
                            worker.work(context.taskContext)
                            redis.setTaskStatus(context.taskId, TaskStatus.Completed)
                            ResultMonad(())
                        })
                })
                Thread.sleep(10 * 1000)
            }
        }
    }

    def stopScanTask(force: Boolean = false): Unit = {
        cancellation = true
        Await.ready(executionTask, Duration(10, "sec"))
    }

    private def isReadyForWork() = queue != null && workerFactory != null
}
