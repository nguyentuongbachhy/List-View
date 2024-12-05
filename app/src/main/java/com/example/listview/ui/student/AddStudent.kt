package com.example.listview.ui.student

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.listview.ListOfStudentsRoute
import com.example.listview.R
import com.example.listview.StudentItem
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class AddStudent(private val context: Context, private val className: String, private val studentId: String) {
    private val viewModel = StudentViewModel(context, className)

    @Composable
    fun AddStudentScreen(navController: NavController) {
        val classIds by viewModel.classIds.collectAsState()
        val student = viewModel.getStudentById(studentId)

        LaunchedEffect(studentId) {
            viewModel.fetchClassId()
        }

        val fullName = remember { mutableStateOf(student.fullName ?: "") }
        val birthday = remember { mutableStateOf(student.birthday ?: "") }
        val grade = remember { mutableStateOf(student.classId ?: className) }
        val gender = remember { mutableIntStateOf(student.gender ?: 1) }

        StudentInformation(
            navController = navController,
            fullName = fullName,
            birthday = birthday,
            grade = grade,
            gender = gender,
            gradeOptions = classIds,
            onSave = {
                val updatedStudent = StudentItem(
                    id = student.id.ifEmpty { UUID.randomUUID().toString() },
                    fullName = fullName.value,
                    birthday = birthday.value,
                    classId = grade.value,
                    gender = gender.intValue
                )
                if(student.id.isEmpty()) {
                    viewModel.addStudent(updatedStudent)
                } else {
                    viewModel.updateStudent(updatedStudent)
                }
                navController.popBackStack()
            },
            onDelete = {
                if(student.id.isNotEmpty()) {
                    viewModel.deleteStudent(studentId)
                }
                navController.popBackStack()
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun StudentInformation(
        navController: NavController,
        fullName: MutableState<String>,
        birthday: MutableState<String>,
        grade: MutableState<String>,
        gender: MutableIntState,
        gradeOptions: List<String>,
        onSave: () -> Unit,
        onDelete: () -> Unit
    ) {
        val expandedGrade = remember { mutableStateOf(false) }
        val calendar = remember { Calendar.getInstance() }
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val pickedDate = Calendar.getInstance()
                pickedDate.set(year, month, dayOfMonth)
                pickedDate.set(year, month, dayOfMonth)
                val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                birthday.value = dateFormatter.format(pickedDate.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)

        )
        val gson = Gson()

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = "Student Information",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            item {
                OutlinedTextField(
                    label = { Text(text = "Full Name") },
                    value = fullName.value,
                    onValueChange = { fullName.value = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = birthday.value,
                    label = { Text(text = "Birthday") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    onValueChange = { birthday.value = it },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select Date",
                            modifier = Modifier
                                .clickable {
                                    datePickerDialog.show()
                                }
                                .padding(bottom = 16.dp)
                        )
                    },
                    readOnly = true
                )


            }

            item {
                ExposedDropdownMenuBox(
                    expanded = expandedGrade.value,
                    onExpandedChange = { expandedGrade.value = !expandedGrade.value }
                ) {
                    OutlinedTextField(
                        label = { Text(text = "Class") },
                        value = grade.value,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        trailingIcon = {
                            Icon(
                                painter = if (expandedGrade.value) painterResource(R.drawable.ic_arrow_up)
                                else painterResource(R.drawable.ic_arrow_down),
                                contentDescription = "Select Class",
                                modifier = Modifier.clickable { expandedGrade.value = expandedGrade.value }
                            )
                        },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedGrade.value,
                        onDismissRequest = { expandedGrade.value = false }
                    ) {
                        gradeOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(text = option) },
                                onClick = {
                                    grade.value = option
                                    expandedGrade.value = false
                                }
                            )
                        }
                    }
                }

            }

            item {
                OutlinedTextField(label = { Text(text = "Gender") }, value = if (gender.intValue == 1) "Male" else if (gender.intValue == 0) "Female" else "Other", onValueChange = {}, readOnly = true)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(start = 0.dp, end = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { gender.intValue = 1 }
                    ) {
                        RadioButton(
                            selected = gender.intValue == 1,
                            onClick = { gender.intValue = 1 }
                        )
                        Text(text = "Male")
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { gender.intValue = 0 }
                    ) {
                        RadioButton(
                            selected = gender.intValue == 0,
                            onClick = { gender.intValue = 0 }
                        )
                        Text(text = "Female")
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { gender.intValue = 2 }
                    ) {
                        RadioButton(
                            selected = gender.intValue == 2,
                            onClick = { gender.intValue = 2 }
                        )
                        Text(text = "Other")
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        if (studentId != "null") {
                            onDelete()
                        }
                        navController.navigate(ListOfStudentsRoute.route + "/${className}")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(Color.Red),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Delete",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                Button(
                    onClick = {onSave()},
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(Color.Blue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Save",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

        item {
            Button(
                onClick = {
                    val studentData = mapOf(
                        "fullName" to fullName.value,
                        "birthday" to birthday.value,
                        "grade" to grade.value,
                        "gender" to when (gender.intValue) {
                            1 -> "Male"
                            0 -> "Female"
                            else -> "Other"
                        }
                    )

                    if (studentData.values.any { it.isBlank() }) {
                        Toast.makeText(context, "All fields must be filled!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val jsonData = gson.toJson(studentData)
                    val githubUrl = "https://nguyentuongbachhy.github.io/preview_student/?data=" +
                            URLEncoder.encode(jsonData, StandardCharsets.UTF_8.toString())

                    Log.d("PreviewURL", githubUrl)

                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl))
                    context.startActivity(intent)

                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(Color.Green),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Preview",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        }
    }
}