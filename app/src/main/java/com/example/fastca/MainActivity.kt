package com.example.fastca

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextAlign
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fastca.ui.theme.FastCaTheme
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FastCaApp()
        }
    }
}

@Composable
fun FastCaApp() {
    FastCaTheme {
        // Создаем NavController
        val navController = rememberNavController()

        // NavHost определяет граф навигации
        NavHost(
            navController = navController,
            // Начальный экран
            startDestination = AppDestinations.SQUARE_GRID_ROUTE
        ) {
            // Экран сетки квадратов
            composable(route = AppDestinations.SQUARE_GRID_ROUTE) {
                // Передаем navController в FastCaScreen
                FastCaScreen(navController = navController)
            }

            // Экран детализации квадрата
            composable(
                route = AppDestinations.squareDetailRouteWithArg,
                arguments = listOf(navArgument(AppDestinations.SQUARE_ID_ARG) {
                    type = NavType.StringType // Указываем тип аргумента
                })
            ) { backStackEntry ->
                // Извлекаем аргумент (ID квадрата)
                val squareId = backStackEntry.arguments?.getString(AppDestinations.SQUARE_ID_ARG)
                // !! Важно: обработать случай, если ID не пришел (хотя не должен при правильной навигации)
                if (squareId != null) {
                    SquareDetailScreen(
                        navController = navController
                    )
                } else {
                    // Можно показать ошибку или вернуться назад
                    navController.popBackStack()
                }
            }
        }
    }
}

@Composable
fun FastCaScreen(
    navController: NavController, // <-- Добавить NavController как параметр
    viewModel: FastCaViewModel = hiltViewModel()
) {
    val squares by viewModel.squares.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = "FastCa",
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = FontFamily.Cursive,
                fontSize = 32.sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(24.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
        ) {
            items(squares, key = { it.id }) { square ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(8.dp)
                        // ИСПРАВЛЕНО: Используем цвет поверхности из темы
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline) // Уменьшил толщину для M3 стиля
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = {
                                    viewModel.requestDelete(square)
                                },
                                // --- ДОБАВЛЕНО: Обработка нажатия для навигации ---
                                onTap = {
                                    // Переходим на экран детали, передавая ID квадрата
                                    navController.navigate("squareDetail/${square.id}")
                                }
                                // -------------------------------------------------
                            )
                        }
                ) {
                    Text(
                        text = square.id.substring(0, 6),
                        style = MaterialTheme.typography.bodyLarge, // Изменил стиль для примера
                        // ИСПРАВЛЕНО: Используем цвет текста для поверхности
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // ... (Кнопка "Создать" и AlertDialog остаются без изменений по логике)
        Button(
            onClick = { viewModel.addSquare() },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                "Создать",
                style = MaterialTheme.typography.labelLarge
            )
        }

        if (viewModel.showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.cancelDelete() },
                title = { Text("Удалить квадрат?") },
                text = { Text("Вы точно хотите удалить этот квадрат?") },
                confirmButton = {
                    Button(onClick = { viewModel.confirmDelete() }) {
                        Text("Да")
                    }
                },
                dismissButton = {
                    Button(onClick = { viewModel.cancelDelete() }) {
                        Text("Нет")
                    }
                }
            )
        }
    }
}