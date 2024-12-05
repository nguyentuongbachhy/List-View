package com.example.listview.ui.search

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listview.StudentOverview
import com.example.listview.database.DatabaseHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(context: Context): ViewModel() {
    private val databaseHelper = DatabaseHelper(context)
    private val _searchedStudents = MutableStateFlow<List<StudentOverview>>(emptyList())
    val searchedStudents = _searchedStudents.asStateFlow()

    fun searchStudents(keyword: String) {
        viewModelScope.launch {
            databaseHelper.searchStudents(keyword).collect { students ->
                _searchedStudents.value = students
            }
        }
    }
}