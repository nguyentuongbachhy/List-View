    package com.example.listview.ui.clazz

    import android.content.Context
    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.example.listview.ClassItem
    import com.example.listview.database.DatabaseHelper
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.asStateFlow
    import kotlinx.coroutines.flow.collectLatest
    import kotlinx.coroutines.launch

    class ClazzViewModel(context: Context): ViewModel(){
        private val databaseHelper = DatabaseHelper(context)
        private val _classes = MutableStateFlow<List<ClassItem>>(emptyList())
        val classes = _classes.asStateFlow()

        init {
            fetchClasses()
        }

        private fun fetchClasses() {
            viewModelScope.launch {
                databaseHelper.getClasses().collectLatest { classList ->
                    _classes.value = classList
                }
            }
        }

        fun isExistClassSync(className: String): Boolean {
            return databaseHelper.isExistClassSync(className)
        }

        fun addClass(clazz: ClassItem) {
            viewModelScope.launch {
                databaseHelper.addClass(clazz)
                fetchClasses()
            }
        }

        fun deleteClass(classId: String) {
            viewModelScope.launch {
                databaseHelper.deleteClassById(classId)
                fetchClasses()
            }
        }

    }