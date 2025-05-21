import models.exception.Exception

data class ConsoleOutput(
    val output: String,
    val exception: Exception? = null,
)