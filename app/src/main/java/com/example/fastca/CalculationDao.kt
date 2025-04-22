package com.example.fastca

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculationDao {

    @Query("SELECT * FROM calculations WHERE squareId = :squareId ORDER BY createdAt ASC")
    fun getCalculationsForSquare(squareId: String): Flow<List<CalculationData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalculation(calculation: CalculationData)

    @Update
    suspend fun updateCalculation(calculation: CalculationData)

    @Delete
    suspend fun deleteCalculation(calculation: CalculationData)

    // Можно добавить метод для удаления всех расчетов для квадрата, если нужно
    // @Query("DELETE FROM calculations WHERE squareId = :squareId")
    // suspend fun deleteCalculationsForSquare(squareId: String)
}