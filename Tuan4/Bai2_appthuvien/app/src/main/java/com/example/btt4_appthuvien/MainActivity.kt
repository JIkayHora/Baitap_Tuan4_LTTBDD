package com.example.btt4_appthuvien

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.btt4_appthuvien.ui.theme.Btt4_appthuvienTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Btt4_appthuvienTheme {
                Surface( modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background){
                    LibraryApp()
                }
            }
        }
    }
}

//Trừu tượng
abstract class LibraryItem(val id: String, val title: String) {
    abstract fun getDisplayInfo(): String
}

interface ILibraryManager {
    fun getAllBooks(): List<Book>
    fun getAllStudents(): List<Student>
    fun addNewBook(title: String)
    fun addNewStudent(name: String)
}

//Kế thừa và đa hình
open class Book(
    id: String,
    title: String,
    val author: String = "UTH Press"
) : LibraryItem(id, title) {
    override fun getDisplayInfo(): String = title
}

class ReferenceBook(
    id: String,
    title: String,
    private val subject: String
) : Book(id, title) {
    override fun getDisplayInfo(): String = "$title ($subject)"
}

open class Person(val id: String, val name: String) {
    open fun getRoleDescription(): String = "Thành viên thư viện"
}
//Đóng gói
class Student(id: String, name: String) : Person(id, name) {
    private val _borrowedBooks = mutableStateListOf<Book>()
    val borrowedBooks: List<Book> get() = _borrowedBooks
    fun borrowBook(book: Book) {
        if (!_borrowedBooks.contains(book)) _borrowedBooks.add(book)
    }
    fun returnBook(book: Book) {
        _borrowedBooks.remove(book)
    }
    fun isBorrowing(book: Book): Boolean = _borrowedBooks.contains(book)
    override fun getRoleDescription(): String {
        return "Sinh viên (Đang mượn: ${_borrowedBooks.size} cuốn)"
    }
}
class LibraryManager : ILibraryManager {
    private val _books = mutableStateListOf<Book>()
    private val _students = mutableStateListOf<Student>()
    init {
        val book1 = Book("B01", "Sách 01")
        val book2 = Book("B02", "Sách 02")
        val book3 = ReferenceBook("B03", "Sách 03", "Lập trình OOP")
        val book4 = Book("B04", "Sách 04")
        _books.addAll(listOf(book1, book2, book3, book4))
        val svA = Student("SV01", "Bá Vãi Lúa").apply {
            borrowBook(book1)
            borrowBook(book2)
        }
        val svB = Student("SV02", "Bắp Bá Khá").apply {
            borrowBook(book1)
        }
        val svC = Student("SV03", "Dương Thành Đạt")
        _students.addAll(listOf(svA, svB, svC))
    }
    override fun getAllBooks(): List<Book> = _books
    override fun getAllStudents(): List<Student> = _students
    override fun addNewBook(title: String) {
        if (title.isNotBlank()) {
            _books.add(Book("B0${_books.size + 1}", title))
        }
    }
    override fun addNewStudent(name: String) {
        if (name.isNotBlank()) {
            _students.add(Student("SV0${_students.size + 1}", name))
        }
    }
}

@Composable
fun LibraryApp() {
    val libraryManager = remember { LibraryManager() }
    val students = libraryManager.getAllStudents()
    val allBooks = libraryManager.getAllBooks()

    var currentStudent by remember { mutableStateOf(students.first()) }
    var studentNameInput by remember { mutableStateOf(currentStudent.name) }
    var showAddBookDialog by remember { mutableStateOf(false) }

    val navControl = rememberNavController()
    val navigateToTab: (String) -> Unit = { route ->
        navControl.navigate(route) {
            popUpTo("management") { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
    NavHost(navControl, startDestination = "management") {
        composable("management") {
            ManagementScreen(
                currentStudent = currentStudent,
                studentNameInput = studentNameInput,
                onNameInputChange = { studentNameInput = it },
                onChangeStudentClick = {
                    val found = students.find {
                        it.name.equals(studentNameInput.trim(), ignoreCase = true) && it != currentStudent
                    }
                    currentStudent = found ?: students[(students.indexOf(currentStudent) + 1) % students.size]
                    studentNameInput = currentStudent.name
                },
                onAddBookClick = { showAddBookDialog = true },
                onNavigate = navigateToTab
            )
        }
        composable("books") {
            BookListScreen(
                books = allBooks,
                onAddBook = { title -> libraryManager.addNewBook(title) },
                onNavigate = navigateToTab
            )
        }
        composable("students") {
            StudentListScreen(
                students = students,
                currentStudent = currentStudent,
                onSelectStudent = { selected ->
                    currentStudent = selected
                    studentNameInput = selected.name
                    navigateToTab("management")
                },
                onAddStudent = { name -> libraryManager.addNewStudent(name) },
                onNavigate = navigateToTab
            )
        }
    }

    if (showAddBookDialog) {
        AlertDialog(
            onDismissRequest = { showAddBookDialog = false },
            title = { Text("Chọn sách để mượn",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp) },
            text = {
                val unborrowedBooks = allBooks.filter { !currentStudent.isBorrowing(it) }
                if (unborrowedBooks.isEmpty()) {
                    Text("Sinh viên này đã mượn hết sách trong thư viện!")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(unborrowedBooks) { book ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        currentStudent.borrowBook(book)
                                        showAddBookDialog = false
                                    },
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                            ) {
                                Text(
                                    text = "+ ${book.getDisplayInfo()}",
                                    modifier = Modifier.padding(14.dp),
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAddBookDialog = false }) {
                    Text("Đóng")
                }
            }
        )
    }
}
@Composable
fun LibraryLayout(
    currentTab: Int,
    onNavigate: (String) -> Unit,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Hệ thống\nQuản lý Thư viện",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            content()
        }

        HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Color.White),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomTabItem(
                title = "Quản lý",
                icon = Icons.Default.Home,
                isSelected = currentTab == 0,
                onClick = { if (currentTab != 0) onNavigate("management") }
            )
            BottomTabItem(
                title = "DS Sách",
                icon = Icons.Default.List,
                isSelected = currentTab == 1,
                onClick = { if (currentTab != 1) onNavigate("books") }
            )
            BottomTabItem(
                title = "Sinh viên",
                icon = Icons.Default.Person,
                isSelected = currentTab == 2,
                onClick = { if (currentTab != 2) onNavigate("students") }
            )
        }
    }
}

@Composable
fun ManagementScreen(
    currentStudent: Student,
    studentNameInput: String,
    onNameInputChange: (String) -> Unit,
    onChangeStudentClick: () -> Unit,
    onAddBookClick: () -> Unit,
    onNavigate: (String) -> Unit
) {
    LibraryLayout(currentTab = 0, onNavigate = onNavigate) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp,
                vertical = 8.dp)
        ) {
            Text("Sinh viên",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = studentNameInput,
                    onValueChange = onNameInputChange,
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = onChangeStudentClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1554B4)),
                    modifier = Modifier.height(52.dp)
                ) {
                    Text("Thay đổi",
                        fontWeight = FontWeight.Bold,
                        color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("Danh sách sách",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black)
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE0E0E0))
                    .padding(16.dp)
            ) {
                if (currentStudent.borrowedBooks.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Bạn chưa mượn quyển sách nào\nNhấn 'Thêm' để bắt đầu hành trình đọc sách!",
                            textAlign = TextAlign.Center,
                            color = Color.DarkGray,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(currentStudent.borrowedBooks) { book ->
                            Surface(
                                shape = RoundedCornerShape(24.dp),
                                color = Color.White,
                                shadowElevation = 2.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp,
                                        vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = true,
                                        onCheckedChange = { isChecked ->
                                            if (!isChecked) currentStudent.returnBook(book)
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = Color(0xFFA31D36),
                                            checkmarkColor = Color.White
                                        )
                                    )
                                    Text(
                                        text = book.getDisplayInfo(),
                                        fontSize = 15.sp,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
            Box(modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center) {
                Button(
                    onClick = onAddBookClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1554B4)),
                    modifier = Modifier
                        .width(180.dp)
                        .height(48.dp)
                ) {
                    Text("Thêm",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White)
                }
            }
        }
    }
}

@Composable
fun BookListScreen(
    books: List<Book>,
    onAddBook: (String) -> Unit,
    onNavigate: (String) -> Unit
) {
    var newBookTitle by remember { mutableStateOf("") }
    LibraryLayout(currentTab = 1, onNavigate = onNavigate) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp,
            vertical = 8.dp)) {
            Text("Tạo sách mới vào kho",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newBookTitle,
                    onValueChange = { newBookTitle = it },
                    placeholder = { Text("Nhập tên sách ") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Button(
                    onClick = {
                        onAddBook(newBookTitle)
                        newBookTitle = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1554B4)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(52.dp)
                ) {
                    Text("Tạo", color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("Tất cả sách trong thư viện (${books.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp)
            Spacer(modifier = Modifier.height(10.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(books) { book ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(book.getDisplayInfo(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp)
                            Text("Mã sách: ${book.id} | Tác giả: ${book.author}",
                                fontSize = 12.sp,
                                color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun StudentListScreen(
    students: List<Student>,
    currentStudent: Student,
    onSelectStudent: (Student) -> Unit,
    onAddStudent: (String) -> Unit,
    onNavigate: (String) -> Unit
) {
    var newStudentName by remember { mutableStateOf("") }
    LibraryLayout(currentTab = 2, onNavigate = onNavigate) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp,
            vertical = 8.dp)) {
            Text("Thêm sinh viên mới",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newStudentName,
                    onValueChange = { newStudentName = it },
                    placeholder = { Text("Nhập tên sinh viên") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Button(
                    onClick = {
                        onAddStudent(newStudentName)
                        newStudentName = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1554B4)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(52.dp)
                ) {
                    Text("Thêm", color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("Danh sách sinh viên ",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp)
            Spacer(modifier = Modifier.height(10.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(students) { student ->
                    val isSelected = student == currentStudent
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectStudent(student) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFFDCEBFB)
                            else Color(0xFFF5F5F5)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(student.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp)
                            Text(student.getRoleDescription(),
                                fontSize = 13.sp,
                                color = Color.DarkGray)
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun BottomTabItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = if (isSelected) Color(0xFF1554B4) else Color.Gray
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = icon,
            contentDescription = title,
            tint = color)
        Text(text = title,
            fontSize = 12.sp,
            color = color,
            fontWeight = FontWeight.Medium)
    }
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Btt4_appthuvienTheme {
        LibraryApp()
    }
}