package com.aryn.crochet_app.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.aryn.crochet_app.db.entity.Part
import com.aryn.crochet_app.db.entity.Project

@Dao
interface DAO {
    /******Inserts*****/
    @Insert
    fun insertNewProject(project: Project): Long

    @Insert
    fun insertNewPart(part: Part): Long


    /******DELETES*****/
    //deletes the contents of a whole table
    @Query("DELETE FROM project")
    fun clearProjectTable()

    //deletes the contents of a whole table
    @Query("DELETE FROM part")
    fun clearPartsTable()

    @Query("DELETE FROM project WHERE project.id = :projectID")
    fun deleteProjectById(projectID: Long)

    //deletes the contents of a whole table
    @Query("DELETE FROM part WHERE part.id = :partID")
    fun deletePartById(partID: Long)


    /******Fetch Projects*****/
    @Query("SELECT * from project ORDER BY project.id ASC")
    fun getAllProjects(): List<Project>

    @Query("SELECT project.id from project ORDER BY project.id ASC")
    fun getAllProjectIDs(): List<Long>

    @Query("SELECT project.name from project ORDER BY project.name ASC")
    fun getAllProjectNames(): List<String>

    @Query("SELECT project.hook from project ORDER BY project.hook ASC")
    fun getAllProjectHookSizes(): List<Float>

    @Query("SELECT project.name from project WHERE project.id = :projectID")
    fun getProjectNameByID(projectID: Long): String

    @Query("SELECT project.hook from project WHERE project.id = :projectID")
    fun getProjectHookByID(projectID: Long): Float

    @Query("SELECT * FROM project WHERE project.is_current LIMIT 1")
    fun getCurrentProject(): Project?

    /******Fetch Parts*****/
    @Query("SELECT * from part WHERE part.projectId = :projectID")
    fun getAllPartsByProjectID(projectID: Long): List<Part>

    @Query("SELECT part.name from part WHERE part.id = :partID")
    fun getPartNameByID(partID: Long): String?

    @Query("SELECT part.row_count from part WHERE part.id = :partID")
    fun getPartRowCountByID(partID: Long): Int?


    /******Updates*****/
    @Query("UPDATE part SET row_count = :newRowCount WHERE part.id = :partID")
    fun updatePartRowCountByID(partID: Long, newRowCount: Int)

    @Query("UPDATE part SET name = :newName WHERE part.id = :partID")
    fun updatePartNameByID(partID: Long, newName: String)

    @Query("UPDATE project SET is_current = :isCurrent WHERE project.id = :projectId")
    fun updateCurrentProject(projectId: Long, isCurrent: Boolean)

    @Query("UPDATE project SET is_current = false")
    fun clearAllCurrents()

    @Query("UPDATE project SET current_part = :partId WHERE project.id = :projectId")
    fun updateCurrentPart(projectId: Long, partId: Long)
}