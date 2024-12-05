package com.example.listview.ui.search

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.listview.AddStudentRoute
import com.example.listview.StudentOverview

class Search(context: Context) {
    private val viewModel = SearchViewModel(context)


    @Composable
    fun SearchScreen(navController: NavController) {
        var searchText by remember { mutableStateOf("") }
        val students by viewModel.searchedStudents.collectAsState()
        val randomColors = listOf(
            Color.Red,
            Color.Green,
            Color.Blue,
            Color.Yellow,
            Color.Magenta
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(vertical = 16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text(text = "Search") },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        viewModel.searchStudents(searchText)
                    }
                )
            )

            Text(
                text = "Search results",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 16.dp, start = 16.dp)
            )

            LazyColumn {
                items(students) { student ->
                    StudentItemView(
                        navController = navController,
                        student = student,
                        backgroundColor = randomColors[students.indexOf(student) % randomColors.size]
                    )
                }
            }
        }
    }

    @Composable
    fun StudentLogo(fullName: String, backgroundColor: Color) {
        val sign = fullName.split(" ")
            .filter { it.isNotEmpty() }
            .joinToString(separator = "") { it.first().uppercase() }

        Box(
            modifier = Modifier.size(48.dp).clip(CircleShape).background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text =sign,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    @Composable
    fun StudentItemView(navController: NavController, student: StudentOverview, backgroundColor: Color) {

        Button(
            onClick = {navController.navigate(AddStudentRoute.route + "/${student.classId}/${student.id}")},
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(0.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StudentLogo(student.fullName, backgroundColor)

                    Text(
                        text = student.fullName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}