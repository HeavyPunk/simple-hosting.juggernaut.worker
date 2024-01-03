package worker.downloadfile

import com.fasterxml.jackson.annotation.JsonProperty
import worker.Worker
import worker.WorkerFactory
import common.Monad
import common.ResultMonad

class DownloadFileWorkerFactory extends WorkerFactory[DownloadFileWorker] {
    override def createWorker(): DownloadFileWorker = DownloadFileWorker()
}

class DownloadFileWorker extends Worker[DownloadFileWorkerContext, Exception] {
    override def work(context: DownloadFileWorkerContext): Monad[Exception, Unit] = {
        println(context.s3Path)
        println(context.destinationPath)
        ResultMonad(())
    }
}

case class DownloadFileWorkerContext(
    @JsonProperty("s3-path") val s3Path: String,
    @JsonProperty("destination-path") val destinationPath: String,
)
