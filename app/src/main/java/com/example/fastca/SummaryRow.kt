// Добавьте этот Composable в файл SquareDetailScreen.kt или создайте новый файл

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface // Для фона с elevation
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fastca.CalculationData

/**
 * Composable для отображения строки с итогами (общее количество и площадь).
 * @param calculations Список строк расчета для вычисления итогов.
 */
@Composable
fun SummaryRow(
    calculations: List<CalculationData>,
    modifier: Modifier = Modifier
) {
    // Вычисляем итоги только если список не пуст
    val totalCount = if (calculations.isNotEmpty()) calculations.sumOf { it.count } else 0
    val totalVolume = if (calculations.isNotEmpty()) calculations.sumOf { it.volumeM3 } else 0.0

    // Используем Surface для фона и небольшого поднятия (elevation)
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp), // Отступы вокруг строки
        shape = MaterialTheme.shapes.medium, // Скругленные углы
        tonalElevation = 4.dp // Небольшая тень/поднятие
    ) {
        Column { // Используем Column для добавления разделителя сверху
            Divider() // Разделитель перед строкой итогов
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp), // Внутренние отступы
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween // Распределяем элементы по краям
            ) {
                // Текст "Общее количество"
                Text(
                    text = "Общ кол-во:",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold // Жирный шрифт для акцента
                )
                // Значение общего количества
                Text(
                    text = totalCount.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )

                // Текст "Общая площадь"
                Text(
                    text = "Общ площ (м²):",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp) // Отступ слева
                )
                // Значение общей площади (форматированное)
                Text(
                    text = String.format("%.2f", totalVolume).replace(',', '.'),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }
        }
    }
}