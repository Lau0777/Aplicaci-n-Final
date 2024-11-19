import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.biblioteca.R
import com.google.firebase.auth.FirebaseAuth



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Inicio(navController: NavController) {
    var pantalla by remember { mutableStateOf("Selecciona una acción") }
    var showLogoutDialog by remember { mutableStateOf(false) } // Estado para mostrar el diálogo

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Inicio",
                        color = Color.White,
                        fontFamily = FontFamily.Serif,
                        fontSize = 22.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF1F4D96))
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF1F4D96)) {
                NavigationBarItem(
                    icon = {
                        Image(
                            painter = painterResource(id = R.drawable.catalogo),
                            contentDescription = "Ver Catálogo",
                            colorFilter = ColorFilter.tint(Color.White),
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text("Catálogo", color = Color.White) },
                    selected = pantalla == "catalogo",
                    onClick = {
                        pantalla = "catalogo"
                        navController.navigate("catalogo")
                    }
                )
                NavigationBarItem(
                    icon = {
                        Image(
                            painter = painterResource(id = R.drawable.prestados),
                            contentDescription = "Ver Préstamos",
                            colorFilter = ColorFilter.tint(Color.White),
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text("Préstamos", color = Color.White) },
                    selected = pantalla == "prestamos",
                    onClick = {
                        pantalla = "prestamos"
                        navController.navigate("prestamos")
                    }
                )
                NavigationBarItem(
                    icon = {
                        Image(
                            painter = painterResource(id = R.drawable.devolver),
                            contentDescription = "Devolver Libros",
                            colorFilter = ColorFilter.tint(Color.White),
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text("Devolver", color = Color.White) },
                    selected = pantalla == "devolverPrestamos",
                    onClick = {
                        pantalla = "devolverPrestamos"
                        navController.navigate("devolverPrestamos")
                    }
                )
                NavigationBarItem(
                    icon = {
                        Image(
                            painter = painterResource(id = R.drawable.cerrar),
                            contentDescription = "Cerrar Sesión",
                            colorFilter = ColorFilter.tint(Color.White),
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text("Salir", color = Color.White) },
                    selected = pantalla == "login",
                    onClick = {
                        showLogoutDialog = true
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.librof),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bibli),
                        contentDescription = "Logo de la biblioteca",
                        modifier = Modifier.size(200.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Biblioteca Virtual",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = Color.Blue,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .background(Color(0xFFBAC4E6), RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "¡Bienvenido a la Biblioteca Virtual! Aquí encontrarás una vasta colección de libros y revistas que enriquecerán tu conocimiento y entretenimiento. Explora, elige y disfruta de nuestra selección que abarca múltiples géneros y autores reconocidos. ¡Tu próxima gran lectura te espera!",
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontFamily = FontFamily.Serif,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(text = "Cerrar Sesión") },
            text = { Text(text = "¿Estás seguro de que deseas cerrar sesión?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        pantalla = "login"
                        navController.navigate("login")
                        showLogoutDialog = false
                    }
                ) {
                    Text("Sí", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("No")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InicioPreview() {
    Inicio(navController = rememberNavController())
}
