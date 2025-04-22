// SquareDetailScreen.kt
package com.example.fastca

import SummaryRow
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

/**
 * Экран для отображения деталей выбранного квадрата.
 *
 * @param navController Контроллер навигации для обработки действия "Назад".
 * @param viewModel ViewModel для этого экрана, предоставляемая Hilt.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SquareDetailScreen(
    navController: NavController, viewModel: SquareDetailViewModel = hiltViewModel()
) {
    // Собираем данные самого квадрата (для заголовка) и список расчетов
    val squareData by viewModel.squareData.collectAsStateWithLifecycle()
    val calculations by viewModel.calculations.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(squareData?.id?.substring(0, 6) ?: "Детали") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        // УДАЛЕНО: floatingActionButton = { ... }
    ) { paddingValues ->

        // --- НОВЫЙ Column как основной контейнер ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Применяем отступы от Scaffold ко всему Column
        ) {
            LazyColumn(
                modifier = Modifier
                    // ЗАНИМАЕТ ВСЕ МЕСТО, КРОМЕ НИЖНИХ ЭЛЕМЕНТОВ
                    .weight(1f)
                    .padding(horizontal = 8.dp), // Горизонтальные отступы для списка
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // ... (items для CalculationRow и сообщение "Нет данных" остаются здесь)
                items(calculations, key = { it.id }) { calculation ->
                    CalculationRow(
                        calculation = calculation,
                        onUpdate = { updatedCalc -> viewModel.updateCalculationRow(updatedCalc) },
                        onDelete = { calcToDelete -> viewModel.deleteCalculationRow(calcToDelete) })
                    Divider() // Разделитель между строками
                }

                if (calculations.isEmpty()) {
                    item {
                        Text(
                            "Нет данных для расчета. Нажмите '+' чтобы добавить.",
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } // --- Конец LazyColumn ---

            // --- Место для строки итогов (добавим на следующем шаге) ---
            SummaryRow(calculations = calculations) // <-- Вызов новой Composable

            // --- НОВАЯ КНОПКА ДОБАВЛЕНИЯ (как на главном экране) ---
            Button(
                onClick = { viewModel.addCalculationRow() },
                // Стилизуем под кнопку с главного экрана
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary, // Или surfaceVariant, если хотите серую
                    contentColor = MaterialTheme.colorScheme.onPrimary // Или onSurfaceVariant
                ), modifier = Modifier.fillMaxWidth() // Во всю ширину
                    .padding(horizontal = 16.dp, vertical = 8.dp) // Отступы как у кнопки "Создать"
            ) {
                Text(
                    "+ Добавить строку", // Текст кнопки
                    style = MaterialTheme.typography.labelLarge // Стиль текста
                )
            }
        } // --- Конец основного Column ---
    }
}