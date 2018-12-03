package com.novoda.redditvideos.model

import androidx.room.*

@Database(entities = [VideoEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun videoDao(): VideoDao
}

@Dao
interface VideoDao {

    @Query("SELECT * FROM video_entry")
    fun getAll(): List<VideoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(entries: List<VideoEntity>)

}

@Entity(tableName = "video_entry")
data class VideoEntity(
    @PrimaryKey val id: String,
    val title: String,
    val thumbnail: String
)

interface DatabaseAware {

    val database: AppDatabase

    val videos: VideoDao get() = database.videoDao()

}
