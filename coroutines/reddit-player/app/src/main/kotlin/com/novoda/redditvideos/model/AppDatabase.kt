package com.novoda.redditvideos.model

import androidx.paging.DataSource
import androidx.room.*

@Database(entities = [VideoEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun videoDao(): VideoDao
}

@Dao
interface VideoDao {

    @Query("SELECT * FROM video_entry")
    fun findVideos(): DataSource.Factory<Int, VideoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(entries: List<VideoEntity>)

    @Query("DELETE FROM video_entry")
    fun clear()

}

@Entity(tableName = "video_entry")
data class VideoEntity(
    @PrimaryKey val id: String,
    val title: String,
    val thumbnail: String
)
