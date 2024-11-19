package com.example.biblioteca.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.biblioteca.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun prestamosScreen(navController: NavController) {
    var pantalla by remember { mutableStateOf("Selecciona una acción") }
    val firestore = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val prestamos = remember { mutableStateListOf<Map<String, Any>>() }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showCards by remember { mutableStateOf(true) } // Estado para visibilidad de los Card

    // Obtener préstamos de Firestore en tiempo real
    LaunchedEffect(userId) {
        if (userId != null) {
            firestore.collection("Prestamos")
                .whereEqualTo("userId", userId)
                .addSnapshotListener { result, error ->
                    if (error != null) {
                        errorMessage = "Error al cargar los préstamos: ${error.message}"
                        return@addSnapshotListener
                    }
                    if (result != null) {
                        prestamos.clear()
                        for (document in result) {
                            val prestamoData = document.data.toMutableMap()
                            prestamoData["id"] = document.id
                            prestamos.add(prestamoData)
                        }
                    }
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Mis Prestamos", color = Color.White, fontFamily = FontFamily.Serif)
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF1F4D96)),
            )

        },
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF1F4D96)) {
                NavigationBarItem(
                    icon = {
                        Image(
                            painter = painterResource(id = R.drawable.home),
                            contentDescription = "Volver",
                            colorFilter = ColorFilter.tint(Color.White),
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text("Inicio", color = Color.White) },
                    selected = false,
                    onClick = {
                        navController.navigate("inicio")
                    }
                )
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

            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Préstamos Activos",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = Color(0xFF1F4D96),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { showCards = !showCards }, // Cambiar visibilidad de los Card
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F4D96))
                ) {
                    Text(text = if (showCards) "Ocultar Préstamos" else "Mostrar Préstamos")
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (showCards) { // Mostrar u ocultar los Card según el estado
                    if (prestamos.isEmpty()) {
                        Text(
                            text = "No tienes préstamos activos.",
                            color = Color.Gray,
                            fontFamily = FontFamily.Serif,
                            fontSize = 16.sp
                        )
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                            items(prestamos) { prestamo ->
                                val libroId = prestamo["libroId"] as? String ?: ""
                                val cantidad = (prestamo["cantidad"] as? Long)?.toInt() ?: 0

                                // Obtener información del libro
                                var titulo by remember { mutableStateOf("") }
                                LaunchedEffect(libroId) {
                                    firestore.collection("Libro").document(libroId).get()
                                        .addOnSuccessListener { document ->
                                            titulo = document.getString("Nombre") ?: "Título no disponible"
                                        }
                                        .addOnFailureListener {
                                            titulo = "Error al cargar título"
                                        }
                                }

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    shape = MaterialTheme.shapes.medium,
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFBAC4E6)), // Color específico
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = "Título: $titulo",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = Color.Black
                                        )
                                        Text(
                                            text = "Cantidad prestada: $cantidad",
                                            color = Color.Gray,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                errorMessage?.let {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = it, color = Color.Red)
                }
            }
        }
    }
}


