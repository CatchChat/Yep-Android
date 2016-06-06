package catchla.yep.model

data class TaskResponse<D> constructor(val data: D? = null, val exception: Throwable? = null)