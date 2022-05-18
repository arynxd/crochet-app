package com.aryn.crochet_app.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(
    tableName = "part", foreignKeys = [
        ForeignKey(entity = Project::class, onDelete= CASCADE, parentColumns = ["id"], childColumns = ["projectId"]),
    ]
)
data class Part(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "row_count")
    val rowCount: Int,

    @ColumnInfo(name = "projectId")
    val project: Long
)
