package com.example.listview

data class StudentOverview(
    val id: String,
    val fullName: String,
    val classId: String
)

data class StudentItem(
    val id: String,
    val fullName: String,
    val birthday: String,
    val gender: Int,
    val classId: String
)


data class ClassItem(
    val id: String,
    val name: String
)