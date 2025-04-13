package fi.orkas.rvtest

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.RoomDatabase
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Database(entities = [Category::class, CategoryMedia::class, Media::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun mediaDao(): MediaDao
}

@Dao
interface MediaDao {
    @Transaction
    @Query("SELECT * FROM categories")
    fun getCategoryMedia(): Flow<List<CategoryWithMedia>>
}

data class CategoryWithMedia(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "cid",
        entityColumn = "mid",
        associateBy = Junction(CategoryMedia::class, "cid", "mid")
    )
    val media: List<Media>
)

@Entity(tableName = "categories")
data class Category(@PrimaryKey val cid: Int, val title: String)

@Entity(
    tableName = "categorymedia",
    primaryKeys = ["cid", "mid"],
    indices = [Index("cid"), Index("mid")],
    foreignKeys = [
        ForeignKey(Category::class, ["cid"], ["cid"]),
        ForeignKey(Media::class, ["mid"], ["mid"])
    ]
)
data class CategoryMedia(val cid: Int, val mid: Int)

@Entity(tableName = "media")
data class Media(
    @PrimaryKey val mid: Int,
    val duration: String,
    val title: String,
    val description: String,
    val cast: String,
    val director: String,
    val year: Int,
    val genre: String,
    val poster: String,
    val synopsis: String
)
