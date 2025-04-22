// SquareData.kt
package com.example.fastca

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "squares")
data class SquareData(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

)