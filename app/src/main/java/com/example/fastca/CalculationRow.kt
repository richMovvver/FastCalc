package com.example.fastca

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete // Иконка удаления
import androidx.compose.material3.* // Используем Material 3 компоненты
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged // Импорт для отслеживания фокуса
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Composable для отображения и редактирования одной строки расчета.
 *
 * @param calculation Данные текущей строки расчета.
 * @param onUpdate Лямбда, вызываемая для обновления данных строки в ViewModel/Repository.
 * @param onDelete Лямбда, вызываемая для удаления этой строки.
 * @param modifier Модификатор для настройки внешнего вида строки.
 */
@Composable
fun CalculationRow(
    calculation: CalculationData,
    onUpdate: (CalculationData) -> Unit,
    onDelete: (CalculationData) -> Unit,
    modifier: Modifier = Modifier
) {
    // Локальное состояние для полей ввода.
    // Используем remember с ключом calculation.id, чтобы состояние сбрасывалось
    // при изменении ID (например, при удалении/добавлении строк в LazyColumn).
    var nameState by remember(calculation.id) { mutableStateOf(calculation.name) }
    var lengthState by remember(calculation.id) { mutableStateOf(calculation.lengthMm.toStringPreservingTrailingZero()) }
    var widthState by remember(calculation.id) { mutableStateOf(calculation.widthMm.toStringPreservingTrailingZero()) }
    var countState by remember(calculation.id) { mutableStateOf(calculation.count.toString()) }

    // Эффект для синхронизации локального состояния с внешними данными,
    // если объект calculation изменился (например, после обновления из другого источника).
    LaunchedEffect(calculation) {
        // Проверяем, чтобы не перезаписать ввод пользователя без необходимости
        if (nameState != calculation.name) nameState = calculation.name
        if (lengthState != calculation.lengthMm.toStringPreservingTrailingZero()) lengthState = calculation.lengthMm.toStringPreservingTrailingZero()
        if (widthState != calculation.widthMm.toStringPreservingTrailingZero()) widthState = calculation.widthMm.toStringPreservingTrailingZero()
        if (countState != calculation.count.toString()) countState = calculation.count.toString()
    }

    Row(
        modifier = modifier
            .fillMaxWidth() // Занимает всю ширину
            .padding(vertical = 4.dp) // Вертикальный отступ между строками
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)) // Слегка видимая граница строки
            .padding(horizontal = 8.dp, vertical = 8.dp), // Внутренние отступы в строке
        verticalAlignment = Alignment.CenterVertically, // Выравнивание элементов по вертикали
        horizontalArrangement = Arrangement.spacedBy(4.dp) // Небольшое пространство между элементами
    ) {
        // --- Поле Имя ---
        EditableTextField(
            value = nameState,
            onValueChange = { nameState = it }, // Обновляем локальное состояние при вводе
            onLostFocus = { // Действие при потере фокуса
                val trimmedName = nameState.trim() // Убираем пробелы по краям
                // Обновляем в БД, только если значение действительно изменилось
                if (trimmedName != calculation.name) {
                    onUpdate(calculation.copy(name = trimmedName))
                }
                // Обновляем state, если были пробелы
                if (nameState != trimmedName) nameState = trimmedName
            },
            modifier = Modifier.weight(1.5f), // Уменьшенный вес для имени
            label = "Имя"
        )

        // --- Поле Длина ---
        EditableNumberField(
            value = lengthState,
            onValueChange = { lengthState = it }, // Обновляем локальное состояние
            onLostFocus = { // Действие при потере фокуса
                val parsedValue = lengthState.toDoubleOrNull() // Пытаемся преобразовать в Double
                if (parsedValue != null) { // Если успешно преобразовано
                    // Обновляем в БД, если значение отличается от сохраненного
                    if (parsedValue != calculation.lengthMm) {
                        onUpdate(calculation.copy(lengthMm = parsedValue))
                    }
                    // Обновляем локальное состояние для корректного форматирования (e.g., "5." -> "5")
                    lengthState = parsedValue.toStringPreservingTrailingZero()
                } else { // Если введено невалидное значение
                    // Сбрасываем локальное состояние к последнему сохраненному значению
                    lengthState = calculation.lengthMm.toStringPreservingTrailingZero()
                }
            },
            modifier = Modifier.weight(1.5f), // Увеличенный вес
            label = "Длина"
        )

        // --- Поле Ширина --- (Логика аналогична Длине)
        EditableNumberField(
            value = widthState,
            onValueChange = { widthState = it },
            onLostFocus = {
                val parsedValue = widthState.toDoubleOrNull()
                if (parsedValue != null) {
                    if (parsedValue != calculation.widthMm) {
                        onUpdate(calculation.copy(widthMm = parsedValue))
                    }
                    widthState = parsedValue.toStringPreservingTrailingZero()
                } else {
                    widthState = calculation.widthMm.toStringPreservingTrailingZero()
                }
            },
            modifier = Modifier.weight(1.5f), // Увеличенный вес
            label = "Ширина"
        )

        // --- Поле Количество --- (Логика аналогична, но с Int и проверкой > 0)
        EditableNumberField(
            value = countState,
            onValueChange = { countState = it },
            onLostFocus = {
                val parsedValue = countState.toIntOrNull() // Пытаемся преобразовать в Int
                // Проверяем, что значение валидно (не null) и больше нуля
                if (parsedValue != null && parsedValue > 0) {
                    // Обновляем в БД, если значение отличается
                    if (parsedValue != calculation.count) {
                        onUpdate(calculation.copy(count = parsedValue))
                    }
                    // Обновляем локальное состояние (просто toString для Int)
                    countState = parsedValue.toString()
                } else { // Если введено невалидное значение (не число, 0 или меньше)
                    // Сбрасываем к последнему сохраненному значению
                    countState = calculation.count.toString()
                }
            },
            modifier = Modifier.weight(1.5f), // Увеличенный вес
            label = "Кол-во",
            keyboardType = KeyboardType.Number // Используем числовую клавиатуру без десятичных знаков
        )

        // --- Отображение Площади (или Объема) ---
        Text(
            text = calculation.getFormattedVolume(), // Используем форматированную строку из модели
            modifier = Modifier
                .weight(1.0f) // Увеличенный вес
                .padding(start = 4.dp), // Небольшой отступ слева
            textAlign = TextAlign.End, // Выравнивание текста по правому краю
            style = MaterialTheme.typography.bodyMedium // Стиль текста из темы
        )

        // --- Кнопка удаления строки ---
        IconButton(
            onClick = { onDelete(calculation) }, // Вызываем лямбду удаления при нажатии
            modifier = Modifier.size(36.dp) // Явно задаем небольшой размер кнопке
        ) {
            Icon(
                imageVector = Icons.Default.Delete, // Стандартная иконка корзины
                contentDescription = "Удалить строку", // Описание для доступности
                tint = MaterialTheme.colorScheme.error // Используем цвет ошибки из темы для акцента
            )
        }
    }
}

/**
 * Вспомогательный Composable для создания редактируемого текстового поля
 * с использованием OutlinedTextField и обработкой потери фокуса.
 */
@Composable
fun EditableTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onLostFocus: () -> Unit,
    modifier: Modifier = Modifier,
    label: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value, // Текущее значение поля
        onValueChange = onValueChange, // Лямбда для обновления значения при вводе
        modifier = modifier
            // .height(IntrinsicSize.Min) // Убрали - используем стандартную высоту
            .onFocusChanged { focusState -> // Отслеживаем изменение состояния фокуса
                if (!focusState.isFocused) { // Если фокус был потерян
                    onLostFocus() // Вызываем переданную лямбду
                }
            },
        label = { Text(label, fontSize = 10.sp) }, // Маленький текст метки (Label)
        singleLine = true, // Поле ввода будет однострочным
        keyboardOptions = keyboardOptions, // Настройки клавиатуры (тип и т.д.)
        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp), // Размер шрифта внутри поля
        // Используем стандартные contentPadding для OutlinedTextField
        // contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp) // Убрали
    )
}

/**
 * Вспомогательный Composable для создания редактируемого числового поля.
 * Обертка над EditableTextField с фильтрацией ввода и настройкой клавиатуры.
 */
@Composable
fun EditableNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    onLostFocus: () -> Unit,
    modifier: Modifier = Modifier,
    label: String,
    // Тип клавиатуры по умолчанию - для десятичных чисел
    keyboardType: KeyboardType = KeyboardType.Decimal
) {
    EditableTextField( // Используем базовый EditableTextField
        value = value,
        onValueChange = { newValue ->
            // Фильтруем ввод: разрешаем только цифры, точку и запятую
            val filtered = newValue.filter { it.isDigit() || it == '.' || it == ',' }
            // Заменяем запятую на точку для унификации перед сохранением/парсингом
            onValueChange(filtered.replace(',', '.'))
        },
        onLostFocus = onLostFocus,
        modifier = modifier,
        label = label,
        // Передаем объект KeyboardOptions с указанным типом клавиатуры
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}

/**
 * Расширение для Double, чтобы преобразовать его в строку,
 * удаляя ".0" в конце, если число целое.
 * Например: 5.0 -> "5", 5.5 -> "5.5"
 */
fun Double.toStringPreservingTrailingZero(): String {
    val str = this.toString()
    return if (str.endsWith(".0")) str.substring(0, str.length - 2) else str
}