package com.example.fastca

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SquareDetailViewModel @Inject constructor(
    private val repository: SquareRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val squareId: String = savedStateHandle.get<String>(AppDestinations.SQUARE_ID_ARG)
        ?: throw IllegalArgumentException("Square ID not found")

    // StateFlow для данных самого квадрата (может пригодиться для заголовка)
    val squareData: StateFlow<SquareData?> = repository.getSquareById(squareId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = null
        )

    // --- НОВОЕ: StateFlow для списка расчетов ---
    val calculations: StateFlow<List<CalculationData>> = repository.getCalculationsForSquare(squareId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList() // Начинаем с пустого списка
        )

    // --- НОВОЕ: Метод для добавления новой строки расчета ---
    fun addCalculationRow() {
        viewModelScope.launch {
            // Создаем новую строку с ID текущего квадрата
            val newCalculation = CalculationData(squareId = squareId)
            repository.addCalculation(newCalculation)
        }
    }

    // --- НОВОЕ: Метод для обновления существующей строки ---
    fun updateCalculationRow(updatedCalculation: CalculationData) {
        // Убедимся, что обновляем строку для правильного squareId (хотя id строки уникален)
        if (updatedCalculation.squareId == squareId) {
            viewModelScope.launch {
                repository.updateCalculation(updatedCalculation)
            }
        } else {
            // Обработка ошибки или логирование, если squareId не совпадает
            println("Error: Attempted to update calculation with wrong squareId")
        }
    }

    // --- НОВОЕ: Метод для удаления строки ---
    fun deleteCalculationRow(calculationToDelete: CalculationData) {
        viewModelScope.launch {
            repository.deleteCalculation(calculationToDelete)
        }
    }
}