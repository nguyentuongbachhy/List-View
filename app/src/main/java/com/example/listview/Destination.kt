package com.example.listview

interface Destinations {
    val route: String
    val icon: Int
    val title: String
}

object ListOfClassesRoute : Destinations {
    override val route = "listOfClasses"
    override val icon = R.drawable.ic_home
    override val title = "Home"
}

object SearchRoute: Destinations {
    override val route = "search"
    override val icon = R.drawable.ic_search
    override val title = "Search"
}

object AddClassRoute: Destinations {
    override val route = "addClass"
    override val icon = 0
    override val title = "Add Class"
}

object ListOfStudentsRoute: Destinations {
    const val classNameArg = "className"
    override val route = "listOfStudent"
    override val icon = 0
    override val title = "List of Student"
}

object AddStudentRoute: Destinations {
    const val classNameArg = "className"
    const val studentIdArg = "id"
    override val route = "addStudent"
    override val icon = 0
    override val title = "Add Student"
}

object StudentPreviewRoute: Destinations {
    const val studentInfoArg = "studentInfo"
    override val route: String = "studentPreview"
    override val icon: Int = 0
    override val title: String = "studentPreview"
}