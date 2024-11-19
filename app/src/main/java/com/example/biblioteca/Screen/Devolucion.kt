import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.biblioteca.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevolverLibrosScreen(navController: NavController) {
    var pantalla by remember { mutableStateOf("Selecciona una acción") }
    val firestore = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val prestamos = remember { mutableStateListOf<Map<String, Any>>() }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var selectedPrestamoId by remember { mutableStateOf("") }
    var selectedLibroId by remember { mutableStateOf("") }
    var selectedCantidadPrestada by remember { mutableStateOf(0) }

    // Obtener los préstamos del usuario actual
    LaunchedEffect(Unit) {
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
                        prestamoData["id"] = document.id // Usamos el ID del documento como "id"
                        prestamos.add(prestamoData)
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
                        Text(text = "Devolver Libros", color = Color.White)
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
            }
        }

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Libros en Préstamo",
                fontSize = 24.sp,
                color = Color(0xFF1F4D96),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn {
                items(prestamos.size) { index ->
                    val prestamo = prestamos[index]
                    val prestamoId = prestamo["id"].toString()
                    val libroId = prestamo["libroId"].toString()
                    val cantidadPrestada = (prestamo["cantidad"] as? Long)?.toInt() ?: 0


                    var tituloLibro by remember { mutableStateOf("") }
                    LaunchedEffect(libroId) {
                        firestore.collection("Libro").document(libroId).get()
                            .addOnSuccessListener { document ->
                                tituloLibro = document.getString("Nombre") ?: "Libro no encontrado"
                            }
                            .addOnFailureListener {
                                tituloLibro = "Error al cargar nombre"
                            }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFBAC4E6))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "Título: $tituloLibro", color = Color.Black)
                            Text(text = "Cantidad Prestada: $cantidadPrestada", color = Color.Black)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    showDialog = true
                                    selectedPrestamoId = prestamoId
                                    selectedLibroId = libroId
                                    selectedCantidadPrestada = cantidadPrestada
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F4D96))
                            ) {
                                Text(text = "Devolver", color = Color.White)
                            }
                        }
                    }
                }
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = it, color = if (it.contains("Error")) Color.Red else Color.Green)
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Confirmar devolución") },
                text = { Text("¿Estás seguro de que deseas devolver este libro?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                            scope.launch {
                                devolverLibro(selectedPrestamoId, selectedLibroId, selectedCantidadPrestada)
                            }
                        }
                    ) {
                        Text("Sí")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("No")
                    }
                }
            )
        }
    }
}

// Función para devolver el libro y actualizar la cantidad disponible en Firestore
fun devolverLibro(prestamoId: String, libroId: String, cantidadPrestada: Int) {
    val firestore = FirebaseFirestore.getInstance()

    // Eliminar el préstamo
    firestore.collection("Prestamos").document(prestamoId)
        .delete()
        .addOnSuccessListener {
            println("Préstamo devuelto con éxito.")

            // Actualizar la cantidad disponible en el documento del libro
            val libroRef = firestore.collection("Libro").document(libroId)
            libroRef.get()
                .addOnSuccessListener { libroDoc ->
                    val cantidadDisponibleActual = libroDoc.getLong("Cantidad")?.toInt() ?: 0
                    val nuevaCantidadDisponible = cantidadDisponibleActual + cantidadPrestada

                    // Actualizar la cantidad disponible
                    libroRef.update("Cantidad", nuevaCantidadDisponible)
                        .addOnSuccessListener {
                            println("Cantidad disponible del libro actualizada correctamente.")
                        }
                        .addOnFailureListener { e ->
                            println("Error al actualizar la cantidad disponible: ${e.message}")
                        }
                }
                .addOnFailureListener { e ->
                    println("Error al obtener el documento del libro: ${e.message}")
                }
        }
        .addOnFailureListener { e ->
            println("Error al devolver el préstamo: ${e.message}")
        }
}
