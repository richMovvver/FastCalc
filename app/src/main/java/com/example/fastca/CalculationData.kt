package com.example.fastca

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "calculations",
    // Связываем с таблицей 'squares' по полю squareId
    foreignKeys = [ForeignKey(
        entity = SquareData::class,
        parentColumns = ["id"], // Поле в родительской таблице (SquareData)
        childColumns = ["squareId"], // Поле в этой таблице
        onDelete = ForeignKey.CASCADE // При удалении SquareData удалить все связанные расчеты
    )],
    // Индекс для ускорения запросов по squareId
    indices = [Index(value = ["squareId"])]
)
data class CalculationData(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val squareId: String, // Ссылка на родительский SquareData
    val name: String = "",
    val lengthMm: Double = 0.0, // Длина в мм
    val widthMm: Double = 0.0,  // Ширина в мм
    val count: Int = 1,          // Количество
    val createdAt: Long = System.currentTimeMillis()
) {
    // Вычисляемое свойство для объема в кубических метрах (мм * мм * 1 / 1_000_000_000)
    // Умножаем на count
    val volumeM3: Double
        get() = if (lengthMm > 0 && widthMm > 0 && count > 0) {
            (lengthMm * widthMm * count) / 1_000_000.0 // Делим на 1 млн для м² и еще раз на 1000? Нет, просто 1 млн.
            // Перепроверка: 1000мм * 1000мм = 1_000_000 мм² = 1 м².
            // Значит, мм * мм / 1_000_000 = м².
            // Объем = Площадь * Толщина. У нас нет толщины.
            // Возможно, имелся в виду расчет площади?
            // Если объем, нужна третья размерность (толщина/высота).
            // Пока будем считать ПЛОЩАДЬ в м²: (lengthMm * widthMm * count) / 1_000_000.0
            // Если нужен объем, добавьте поле thicknessMm и делите на 1_000_000_000.0
            // Давайте пока сделаем площадь в м²
            (lengthMm * widthMm * count) / 1_000_000.0
        } else {
            0.0
        }

    // Форматирование объема для отображения
    fun getFormattedVolume(): String {
        val vol = volumeM3
        return if (vol == 0.0) {
            "0.0" // <-- Показываем "0.0" вместо "0.000000"
        } else {
            String.format("%.2f", vol).replace(',', '.')
        }
    }
}