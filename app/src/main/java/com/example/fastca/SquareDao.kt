// SquareDao.kt
package com.example.fastca

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow // Важно: используем Flow для реактивности

@Dao
interface SquareDao {

    // Запрос для получения всех квадратов. Возвращает Flow,
    // который будет автоматически обновлять UI при изменениях в таблице.
    @Query("SELECT * FROM squares ORDER BY id ASC") // Добавим сортировку для предсказуемости
    fun getAllSquares(): Flow<List<SquareData>>

    // Метод для вставки нового квадрата.
    // onConflict = OnConflictStrategy.REPLACE - если квадрат с таким id уже есть, он будет заменен.
    // suspend - т.к. операции с БД должны выполняться в корутинах (вне основного потока).
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSquare(square: SquareData)

    // Метод для удаления квадрата.
    // suspend - по той же причине.
    @Delete
    suspend fun deleteSquare(square: SquareData)

    // Альтернативный метод удаления по ID (если удобнее)
    // @Query("DELETE FROM squares WHERE id = :squareId")
    // suspend fun deleteSquareById(squareId: String)

    @Query("SELECT * FROM squares WHERE id = :squareId")
    fun getSquareById(squareId: String): Flow<SquareData?> // Возвращаем Flow<SquareData?> (может не найти)
}