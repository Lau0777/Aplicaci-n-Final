import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.biblioteca.R
import kotlinx.coroutines.delay



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun catalogo(navController: NavController) {
    val firestore = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val libros = remember { mutableStateListOf<Map<String, Any>>() }
    val selectedBooks = remember { mutableStateMapOf<String, Int>() }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var selectedLibro by remember { mutableStateOf<Map<String, Any>?>(null) }

    // Estado para manejar la imagen seleccionada
    var selectedImage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        firestore.collection("Libro")
            .addSnapshotListener { result, error ->
                if (error != null) {
                    errorMessage = "Error al cargar libros: ${error.message}"
                    Log.e("FirestoreError", error.message ?: "Error desconocido")
                    return@addSnapshotListener
                }
                if (result != null) {
                    libros.clear()
                    for (document in result) {
                        val libroData = document.data.toMutableMap()
                        libroData["id"] = document.id
                        libros.add(libroData)
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
                        Text(text = "Libros", color = Color.White, fontFamily = FontFamily.Serif)
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
                            painter = painterResource(id = R.drawable.prestados),
                            contentDescription = "Ver Préstamos",
                            colorFilter = ColorFilter.tint(Color.White),
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text("Préstamos", color = Color.White) },
                    selected = false,
                    onClick = {
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
                    selected = false,
                    onClick = {
                        navController.navigate("devolverPrestamos")
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.librof),
                contentDescription = "Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0x80FFFFFF))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "LIBROS DISPONIBLES",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = Color(0xFF1F4D96),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(libros) { libro ->
                        val libroId = libro["id"].toString()
                        val titulo = libro["Nombre"] as? String ?: ""
                        val autor = libro["Autor"] as? String ?: ""
                        val genero = libro["Genero"] as? String ?: ""
                        val cantidad = (libro["Cantidad"] as? Long)?.toInt() ?: 0
                        val link = libro["Link"] as? String ?: ""

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            AsyncImage(
                                model = link,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(if (selectedImage == link) 200.dp else 64.dp)
                                    .padding(end = 8.dp)
                                    .clickable {
                                        selectedImage = if (selectedImage == link) null else link
                                    }
                            )
                            Checkbox(
                                checked = selectedBooks.containsKey(libroId),
                                onCheckedChange = { isChecked ->
                                    if (isChecked) {
                                        selectedBooks[libroId] = 1
                                    } else {
                                        selectedBooks.remove(libroId)
                                    }
                                },
                                colors = CheckboxDefaults.colors(Color(0xFF1F4D96))
                            )
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = titulo,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif,
                                    modifier = Modifier.clickable {
                                        selectedLibro = libro
                                        showDialog = true
                                    }
                                )
                                /*Text(
                                    text = "Autor: $autor - Género: $genero - Disponible: $cantidad",
                                    color = Color.Black,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1
                                )*/
                                if (selectedBooks.containsKey(libroId)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Button(onClick = {
                                            selectedBooks[libroId] = (selectedBooks[libroId] ?: 1) + 1
                                        }) { Text("+") }
                                        Text(text = "${selectedBooks[libroId]}", modifier = Modifier.padding(8.dp))
                                        Button(onClick = {
                                            selectedBooks[libroId]?.let {
                                                if (it > 1) selectedBooks[libroId] = it - 1
                                            }
                                        }) { Text("-") }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = {
                                scope.launch {
                                    selectedBooks.forEach { (bookId, quantity) ->
                                        val libroRef = firestore.collection("Libro").document(bookId)
                                        firestore.runTransaction { transaction ->
                                            val snapshot = transaction.get(libroRef)
                                            val currentCantidad = (snapshot.getLong("Cantidad") ?: 0).toInt()
                                            if (currentCantidad >= quantity) {
                                                transaction.update(libroRef, "Cantidad", currentCantidad - quantity)
                                                firestore.collection("Prestamos").add(
                                                    mapOf(
                                                        "userId" to userId,
                                                        "libroId" to bookId,
                                                        "cantidad" to quantity
                                                    )
                                                )
                                                errorMessage = "Préstamo guardado correctamente"
                                            } else {
                                                errorMessage = "No hay suficientes ejemplares disponibles"
                                            }
                                        }.addOnFailureListener { error ->
                                            errorMessage = "Error al procesar préstamo: ${error.message}"
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F4D96))
                        ) {
                            Text(text = "Añadir al préstamo", color = Color.White)
                        }

                        errorMessage?.let {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = it, color = if (it.contains("Error")) Color.Red else Color.Blue)
                        }
                    }
                }
            }
        }
    }

    if (showDialog && selectedLibro != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = selectedLibro?.get("Nombre") as? String ?: "Sin título") },
            text = {
                Column {
                    Text("Autor: ${selectedLibro?.get("Autor") as? String ?: "Desconocido"}")
                    Text("Género: ${selectedLibro?.get("Genero") as? String ?: "Desconocido"}")
                    Text("Cantidad disponible: ${(selectedLibro?.get("Cantidad") as? Long)?.toInt() ?: 0}")
                }
            },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}


