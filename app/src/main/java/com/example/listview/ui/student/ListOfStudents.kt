package com.example.listview.ui.student

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.listview.AddStudentRoute
import com.example.listview.StudentOverview
import com.example.listview.R


class ListOfStudents(
    private val navController: NavController,
    private val className: String,
    context: Context
) {
    private val viewModel = StudentViewModel(context, className)

    @Composable
    fun ListOfStudentScreen() {
        val students by viewModel.students.collectAsState()
        val randomColors = listOf(
            Color.Red,
            Color.Green,
            Color.Blue,
            Color.Yellow,
            Color.Magenta
        )

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn {
                item {
                    Text(
                        text = "Students in $className",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    ) }

                items(students) {student ->
                    StudentItemView(navController, student, randomColors[students.indexOf(student) % randomColors.size])
                }
            }

            FloatingActionButton(
                onClick = {navController.navigate(AddStudentRoute.route + "/${className}/${null}")},
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),

                containerColor = Color.Blue
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = "Add Student",
                    tint = Color.White
                )
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
        var showDialog by remember { mutableStateOf(false) }

        Button(
            onClick = {navController.navigate(AddStudentRoute.route + "/${className}/${student.id}")},
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

                    Text(text = student.fullName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }

                IconButton(
                    onClick = { showDialog = true }
                ) {
                    Icon(
                        Icons.Filled.MoreVert,
                        contentDescription = "Menu",
                        tint = Color.Black
                    )
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = {showDialog = false},
                title = {Text(text = "Delete Student ${student.fullName}")},
                text = {Text(text = "Are you sure you want to delete this student?")},
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteStudent(student.id)
                            showDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text(text = "Delete")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {showDialog = false}
                    ) {
                        Text(text = "Cancel")
                    }
                }
            )
        }
    }
}


