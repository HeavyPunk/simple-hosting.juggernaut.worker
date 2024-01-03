package common

final case class TaskModel[TTaskContext](
    val taskId: String,
    val taskKind: String,
    val taskContext: TTaskContext,
)
