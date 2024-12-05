package com.example.listview.ui.clazz

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.listview.ClassItem
import com.example.listview.ListOfClassesRoute

class AddClass(private val context: Context) {
    private val viewModel = ClazzViewModel(context)

    @Composable
    fun AddClassScreen(navController: NavController) {
        var className by remember { mutableStateOf("") }
        var isError by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf("") }


        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Add Class",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            OutlinedTextField(
                value = className,
                onValueChange = {
                    className = it
                    isError = false
                    errorMessage = ""
                },
                label = { Text("Name Of Class") },
                isError = isError,
                supportingText = if(isError) {
                    { Text(errorMessage) }
                } else {
                    null
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            Button(
                onClick = {
                    when {
                        className.isEmpty() -> {
                            isError = true
                            errorMessage = "Please enter name of class"
                        }
                        viewModel.isExistClassSync(className) -> {
                            isError = true
                            errorMessage = "Class $className already exists"
                        }
                        else -> {
                            viewModel.addClass(ClassItem(id = className, name = className))
                            Toast.makeText(context, "Class added successfully", Toast.LENGTH_SHORT).show()
                            navController.navigate(ListOfClassesRoute.route) {
                                popUpTo(ListOfClassesRoute.route) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Save",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}