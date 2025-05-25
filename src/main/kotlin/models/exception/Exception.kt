package models.exception

import java.util.UUID

sealed class Exception {
//    abstract val id: UUID
    abstract val message: String
}