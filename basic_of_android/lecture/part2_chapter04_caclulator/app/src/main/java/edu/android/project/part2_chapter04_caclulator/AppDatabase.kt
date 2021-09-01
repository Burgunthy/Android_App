package edu.android.project.part2_chapter04_caclulator

import androidx.room.Database
import androidx.room.RoomDatabase
import edu.android.project.part2_chapter04_caclulator.dao.HistoryDao
import edu.android.project.part2_chapter04_caclulator.model.History

@Database(entities = [History::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    // Dao 가져오게 도와주기
    abstract fun historyDAO(): HistoryDao
}