package models.exception

data class ExceptionCompilation(
    override val message: String,
    val blockId: Long,
) : Exception()