// SquareRepository.kt
package com.example.fastca

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SquareRepository @Inject constructor(
    private val squareDao: SquareDao, // Внедряем SquareDao
    private val calculationDao: CalculationDao // <-- ДОБАВЬТЕ ЭТУ СТРОКУ ДЛЯ ВНЕДРЕНИЯ CalculationDao
) {

    // --- Методы для SquareData ---
    fun getAllSquares(): Flow<List<SquareData>> = squareDao.getAllSquares()
    suspend fun addSquare(square: SquareData) { squareDao.insertSquare(square) }
    suspend fun deleteSquare(square: SquareData) { squareDao.deleteSquare(square) }
    fun getSquareById(squareId: String): Flow<SquareData?> = squareDao.getSquareById(squareId)

    // --- Методы для CalculationData ---
    // Теперь calculationDao будет доступен, так как он член класса
    fun getCalculationsForSquare(squareId: String): Flow<List<CalculationData>> {
        return calculationDao.getCalculationsForSquare(squareId)
    }

    suspend fun addCalculation(calculation: CalculationData) {
        calculationDao.insertCalculation(calculation)
    }

    suspend fun updateCalculation(calculation: CalculationData) {
        calculationDao.updateCalculation(calculation)
    }

    suspend fun deleteCalculation(calculation: CalculationData) {
        calculationDao.deleteCalculation(calculation)
    }
}