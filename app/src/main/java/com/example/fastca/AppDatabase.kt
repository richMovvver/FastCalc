// AppDatabase.kt
package com.example.fastca

import androidx.room.Database
import androidx.room.RoomDatabase

// Аннотация Database определяет сущности (entities), версию БД и нужно ли экспортировать схему.
// exportSchema = false - упрощение для разработки, для продакшена лучше true и настроить миграции.
@Database(entities = [SquareData::class, CalculationData::class], version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Абстрактный метод, который Room реализует для предоставления DAO.
    abstract fun squareDao(): SquareDao
    abstract fun calculationDao(): CalculationDao

    // Компаньон объект не обязателен при использовании Hilt,
    // так как Hilt будет управлять созданием экземпляра БД.
}