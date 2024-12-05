package com.example.listview.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.listview.ClassItem
import com.example.listview.StudentItem
import com.example.listview.StudentOverview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_NAME = "students_management.db"
        const val DATABASE_VERSION = 1

        const val TABLE_CLASS = "class"
        const val TABLE_STUDENT = "student"

        const val CREATE_CLASS_TABLE = """
            CREATE TABLE $TABLE_CLASS (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL
            )
        """

        const val CREATE_STUDENT_TABLE = """
            CREATE TABLE $TABLE_STUDENT(
                id TEXT PRIMARY KEY,
                fullName TEXT NOT NULL,
                birthday DATE NOT NULL,
                gender INTEGER DEFAULT 1,
                classId TEXT NOT NULL,
                FOREIGN KEY (classId) REFERENCES $TABLE_CLASS(id)
            )
        """
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_CLASS_TABLE)
        db.execSQL(CREATE_STUDENT_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_STUDENT")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CLASS")
        onCreate(db)
    }

    fun getClasses(): Flow<List<ClassItem>> = flow {
        val db = readableDatabase
        try {
            val query = "SELECT id, name FROM $TABLE_CLASS"
            db.rawQuery(query, null).use { cursor ->
                val classes = mutableListOf<ClassItem>()
                while (cursor.moveToNext()) {
                    val id = cursor.getString(cursor.getColumnIndexOrThrow("id"))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    classes.add(ClassItem(id, name))
                }

                emit(classes)
            }
        } finally {
            db.close()
        }
    }

    fun addClass(classItem: ClassItem) {
        val db = writableDatabase
        try {
            val query = "INSERT INTO $TABLE_CLASS (id, name) VALUES (?, ?)"
            db.execSQL(query, arrayOf(classItem.id, classItem.name))
        } finally {
            db.close()
        }
    }

    fun isExistClassSync(classId: String): Boolean {
        val db = readableDatabase
        try {
            val query = "SELECT id FROM $TABLE_CLASS WHERE id = ?"
            db.rawQuery(query, arrayOf(classId)).use { cursor ->
                return cursor.moveToFirst()
            }
        } finally {
            db.close()
        }
    }

    fun deleteClassById(classId: String) {
        val db = writableDatabase
        try {
            val query = "DELETE FROM $TABLE_CLASS WHERE id = ?"
            db.execSQL(query, arrayOf(classId))
        } finally {
            db.close()
        }
    }

    fun getClassIds(): Flow<List<String>> = flow {
        val db = readableDatabase
        try {
            val query = "SELECT id FROM $TABLE_CLASS"
            db.rawQuery(query, null).use { cursor ->
                val classIds = mutableListOf<String>()
                while (cursor.moveToNext()) {
                    val id = cursor.getString(cursor.getColumnIndexOrThrow("id"))
                    classIds.add(id)
                }

                emit(classIds)
            }
        } finally {
            db.close()
        }
    }

    fun getStudentsByClass(className: String): Flow<List<StudentOverview>> = flow {
        val db = readableDatabase
        try {
            val query = """
            SELECT id, fullName, classId
            FROM $TABLE_STUDENT
            WHERE classId = ?
        """.trimIndent()
            db.rawQuery(query, arrayOf(className)).use { cursor ->
                val students = mutableListOf<StudentOverview>()
                while (cursor.moveToNext()) {
                    students.add(
                        StudentOverview(
                            id = cursor.getString(cursor.getColumnIndexOrThrow("id")),
                            fullName = cursor.getString(cursor.getColumnIndexOrThrow("fullName")),
                            classId = cursor.getString(cursor.getColumnIndexOrThrow("classId"))
                        )
                    )
                }

                emit(students)
            }
        } finally {
            db.close()
        }
    }

    fun getStudentById(studentId: String) :StudentItem {
        val db = readableDatabase
        try {
            val query = "SELECT id, fullName, birthday, gender, classId FROM $TABLE_STUDENT WHERE id = ?"
            db.rawQuery(query, arrayOf(studentId)).use { cursor ->
                if (cursor.moveToFirst()) {
                    val student = StudentItem(
                        id = cursor.getString(cursor.getColumnIndexOrThrow("id")),
                        fullName = cursor.getString(cursor.getColumnIndexOrThrow("fullName")),
                        birthday = cursor.getString(cursor.getColumnIndexOrThrow("birthday")),
                        gender = cursor.getInt(cursor.getColumnIndexOrThrow("gender")),
                        classId = cursor.getString(cursor.getColumnIndexOrThrow("classId"))
                    )
                    return student
                }
            }
        }
        finally {
            db.close()
        }

        return StudentItem("", "", "01/01/2000", 1, "")
    }

    fun addStudent(student: StudentItem) {
        val db = writableDatabase
        try {
            val query = "INSERT INTO $TABLE_STUDENT (id, fullName, birthday, gender, classId) VALUES (?, ?, ?, ?, ?)"
            db.execSQL(query, arrayOf(student.id, student.fullName, student.birthday, student.gender, student.classId))
        } finally {
            db.close()
        }
    }

    fun updateStudent(student: StudentItem) {
        val db = writableDatabase
        try {
            val query = "UPDATE $TABLE_STUDENT SET fullName = ?, birthday = ?, gender = ?, classId = ? WHERE id = ?"
            db.execSQL(query, arrayOf(student.fullName, student.birthday, student.gender, student.classId, student.id))
        } finally {
            db.close()
        }
    }

    fun deleteStudentById(studentId: String) {
        val db = writableDatabase
        try {
            val query = "DELETE FROM $TABLE_STUDENT WHERE id = ?"
            db.execSQL(query, arrayOf(studentId))
        } finally {
            db.close()
        }
    }

    fun searchStudents(keyword: String): Flow<List<StudentOverview>> = flow {
        val db = readableDatabase
        try {
            val query = """
                SELECT id, fullName, classId
                FROM $TABLE_STUDENT
                WHERE fullName LIKE ?
            """.trimIndent()

            db.rawQuery(query, arrayOf("%$keyword%")).use { cursor ->
                val students = mutableListOf<StudentOverview>()
                while (cursor.moveToNext()) {
                    students.add(
                        StudentOverview(
                            id = cursor.getString(cursor.getColumnIndexOrThrow("id")),
                            fullName = cursor.getString(cursor.getColumnIndexOrThrow("fullName")),
                            classId = cursor.getString(cursor.getColumnIndexOrThrow("classId"))
                        )
                    )
                }

                emit(students)
            }
        } finally {
            db.close()
        }
    }
}