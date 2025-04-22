// DatabaseModule.kt
package com.example.fastca

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase // Нужен для миграции
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// --- ОПРЕДЕЛЯЕМ МИГРАЦИЮ ---
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Добавляем новую колонку colorArgb типа INTEGER.
        // Она не может быть NULL.
        // Устанавливаем значение по умолчанию для существующих строк
        // (например, белый цвет).
        //db.execSQL("ALTER TABLE squares ADD COLUMN colorArgb INTEGER NOT NULL DEFAULT ${Color.White.toArgb()}")
        db.execSQL("ALTER TABLE squares ADD COLUMN colorArgb INTEGER")
    }
}
// --- НОВАЯ МИГРАЦИЯ с версии 2 на 3 ---
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // 1. Создаем временную таблицу с новой схемой (без colorArgb)
        db.execSQL("""
            CREATE TABLE squares_new (
                id TEXT NOT NULL PRIMARY KEY
            )
        """.trimIndent())

        // 2. Копируем данные из старой таблицы в новую (только столбец id)
        db.execSQL("""
            INSERT INTO squares_new (id)
            SELECT id FROM squares
        """.trimIndent())

        // 3. Удаляем старую таблицу
        db.execSQL("DROP TABLE squares")

        // 4. Переименовываем новую таблицу в оригинальное имя
        db.execSQL("ALTER TABLE squares_new RENAME TO squares")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `calculations` (
                `id` TEXT NOT NULL,
                `squareId` TEXT NOT NULL,
                `name` TEXT NOT NULL,
                `lengthMm` REAL NOT NULL,
                `widthMm` REAL NOT NULL,
                `count` INTEGER NOT NULL,
                PRIMARY KEY(`id`),
                FOREIGN KEY(`squareId`) REFERENCES `squares`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
            )
        """.trimIndent())
        // Создаем индекс для внешнего ключа
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_calculations_squareId` ON `calculations` (`squareId`)")
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Добавляем новую колонку createdAt
        db.execSQL("ALTER TABLE calculations ADD COLUMN createdAt INTEGER NOT NULL DEFAULT 0")
    }
}
// --------------------------

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "fastca_database"
        )
            // --- ДОБАВЛЯЕМ МИГРАЦИЮ В БИЛДЕР ---
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
            // -----------------------------------
            .build()
    }

    @Provides
    @Singleton
    fun provideSquareDao(database: AppDatabase): SquareDao {
        return database.squareDao()
    }

    // --- ДОБАВЛЕНО: Предоставляем CalculationDao ---
    @Provides
    @Singleton
    fun provideCalculationDao(database: AppDatabase): CalculationDao {
        return database.calculationDao()
    }
}