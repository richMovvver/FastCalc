// FastCaViewModel.kt
package com.example.fastca

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope // Для запуска корутин
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.getValue // Для делегатов mutableStateOf
import androidx.compose.runtime.mutableStateOf // Для состояния диалога
import androidx.compose.runtime.setValue // Для делегатов mutableStateOf

@HiltViewModel
class FastCaViewModel @Inject constructor(
    private val repository: SquareRepository // Внедряем репозиторий
) : ViewModel() {

    // --- Управление списком квадратов через Flow из репозитория ---
    // Преобразуем Flow<List<SquareData>> в StateFlow<List<SquareData>>,
    // чтобы UI мог легко его собирать как состояние Compose.
    val squares: StateFlow<List<SquareData>> = repository.getAllSquares()
        .stateIn(
            scope = viewModelScope, // Область жизни корутин ViewModel
            // Flow будет активен, пока есть подписчики + 5 секунд после исчезновения последнего
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList() // Начальное значение, пока данные не загрузились из БД
        )

    // --- Управление состоянием диалога удаления (остается как было) ---
    var showDeleteDialog by mutableStateOf(false)
        private set
    var squareToDelete by mutableStateOf<SquareData?>(null)
        private set

    // --- Методы для взаимодействия с UI ---
    fun addSquare() {
        // Запускаем корутину для выполнения suspend функции репозитория
        viewModelScope.launch {
            repository.addSquare(SquareData()) // Создаем и добавляем новый квадрат
        }
    }

    fun requestDelete(square: SquareData) {
        squareToDelete = square
        showDeleteDialog = true
    }

    fun confirmDelete() {
        squareToDelete?.let { square ->
            // Запускаем корутину для выполнения suspend функции репозитория
            viewModelScope.launch {
                repository.deleteSquare(square)
            }
        }
        cancelDelete() // Скрываем диалог в любом случае
    }

    fun cancelDelete() {
        showDeleteDialog = false
        squareToDelete = null
    }
}