package com.example.listview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.listview.ui.clazz.AddClass
import com.example.listview.ui.clazz.ListOfClasses
import com.example.listview.ui.search.Search
import com.example.listview.ui.student.AddStudent
import com.example.listview.ui.student.ListOfStudents
import com.example.listview.ui.student.StudentPreviewScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainScreen()
        }
    }
}


@Composable
fun MainScreen() {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val currentBackStackEntry = navController.currentBackStackEntryAsState()


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(200.dp),
                drawerContainerColor = Color.Blue
            ) {
                DrawerPanel(navController, drawerState, scope)
            }
        }
    ) {
        Scaffold(
            topBar = {
                if(currentBackStackEntry.value?.destination?.route != SearchRoute.route) {
                    TopAppBar(navController, drawerState, scope)
                }
            },
            bottomBar = { MyBottomNavigation(navController) }
        ) { padding ->
            Column(
                modifier = Modifier.padding(padding)
            ) {
                NavHost(navController = navController, startDestination = ListOfClassesRoute.route) {
                    composable(ListOfClassesRoute.route) {
                        ListOfClasses(
                            context = context,
                            navController = navController
                        ).ListOfClassesScreen()
                    }
                    composable(SearchRoute.route) {
                        Search(context).SearchScreen(navController)
                    }
                    composable(AddClassRoute.route) {
                        AddClass(context = context).AddClassScreen(navController = navController) // Fixed NavController issue
                    }
                    composable(
                        ListOfStudentsRoute.route + "/{${ListOfStudentsRoute.classNameArg}}",
                        arguments = listOf(navArgument(ListOfStudentsRoute.classNameArg) {
                            type = NavType.StringType
                        })
                    ) {
                        val className = it.arguments?.getString(ListOfStudentsRoute.classNameArg).orEmpty()
                        ListOfStudents(
                            navController = navController,
                            className = className,
                            context = context
                        ).ListOfStudentScreen()
                    }
                    composable(
                        AddStudentRoute.route + "/{${AddStudentRoute.classNameArg}}/{${AddStudentRoute.studentIdArg}}",
                        arguments = listOf(
                            navArgument(AddStudentRoute.classNameArg) {
                                type = NavType.StringType
                            },
                            navArgument(AddStudentRoute.studentIdArg) {
                                type = NavType.StringType
                            }
                        )
                    ) {
                        val studentId = it.arguments?.getString(AddStudentRoute.studentIdArg).orEmpty()
                        val className = it.arguments?.getString(AddStudentRoute.classNameArg).orEmpty()
                        AddStudent(
                            context = context,
                            className = className,
                            studentId = studentId
                        ).AddStudentScreen(navController)
                    }

                    composable(
                        StudentPreviewRoute.route + "/{${StudentPreviewRoute.studentInfoArg}}",
                        arguments = listOf(
                            navArgument(StudentPreviewRoute.studentInfoArg) {
                                type = NavType.StringType
                            }
                        )
                    ) {
                        val studentInfo = it.arguments?.getString(StudentPreviewRoute.studentInfoArg).orEmpty()
                        StudentPreviewScreen(studentInfo, navController)
                    }
                }
            }
        }
    }
}

@Composable
fun TopAppBar(navController: NavController, drawerState: DrawerState, scope: CoroutineScope) {
    Row(
        modifier = Modifier.fillMaxWidth().background(Color.Blue).padding(top = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                scope.launch {
                    drawerState.open()
                }
            }
        ) {
            Image(
                painter = painterResource(R.drawable.ic_menu),
                contentDescription = "Menu Icon",
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White)
            )
        }

        Text(text = "List View", color = Color(0xFFFFFFFF), fontSize = 20.sp, fontWeight = FontWeight.Bold)

        IconButton(
            onClick = {navController.navigate(SearchRoute.route)}
        ) {
            Image(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = "Search Icon",
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White)
            )
        }
    }
}

@Composable
fun MyBottomNavigation(navController: NavController) {
    val destinationList = listOf<Destinations>(
        ListOfClassesRoute, SearchRoute
    )
    val selectedIndex = remember { mutableIntStateOf(0) }
    NavigationBar(
        modifier = Modifier.fillMaxWidth().height(108.dp),
        containerColor = Color.Blue
    ) {
        destinationList.forEachIndexed { index, destination ->
            NavigationBarItem(
                label = {},
                icon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = destination.icon),
                            contentDescription = destination.title,
                            modifier = Modifier.size(24.dp),
                            tint = if (index == selectedIndex.intValue) Color.White else Color.Gray
                        )
                        Text(
                            text = destination.title,
                            color = if (index == selectedIndex.intValue) Color.White else Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                },
                selected = index == selectedIndex.intValue,
                onClick = {
                    selectedIndex.intValue = index
                    navController.navigate(destination.route) {
                        popUpTo(ListOfClassesRoute.route)
                        launchSingleTop = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Blue,

                )
            )
        }
    }
}