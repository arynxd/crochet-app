package com.aryn.crochet_app.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "project")
data class Project(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "hook")
    val hookSize: Float,

    @ColumnInfo(name = "is_current")
    val current: Boolean,

    @ColumnInfo(name = "current_part")
    val currentPart: Long
)
