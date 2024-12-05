package com.example.listview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DrawerPanel(navController: NavController, drawerState: DrawerState, scope: CoroutineScope) {
    Box(
        modifier = Modifier.fillMaxHeight().fillMaxWidth().background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color(0xFFFFFFFF)),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                color = Color.Blue,
                shadowElevation = 4.dp,

            ) {
                Text(
                    text = "List View",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp ,top = 12.dp, bottom = 11.dp),
                    color = Color.White
                )
            }

            LazyColumn {
                item {
                    ClickableRow(
                        iconResId = R.drawable.ic_home,
                        text = "Home",
                        onClick = {navController.navigate(ListOfClassesRoute.route)}
                    )
                }

                item {
                    ClickableRow(
                        iconResId = R.drawable.ic_search,
                        text = "Search",
                        onClick = {navController.navigate(SearchRoute.route)}
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = {
                scope.launch {
                    drawerState.close()
                }
            }) {
                Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Close Icon")
            }
        }
    }
}

@Composable
fun ClickableRow(
    iconResId: Int,
    text: String,
    onClick: () -> Unit
) {

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        shape = RoundedCornerShape(0.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = "$text Icon",
                modifier = Modifier.padding(start = 16.dp)
            )
            Text(
                text = text,
                color = Color(0xFF000000),
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}