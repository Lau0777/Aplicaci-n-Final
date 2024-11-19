import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.biblioteca.R
import com.example.biblioteca.Screen.prestamosScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore



@Composable
fun RegisterScreen(navController: NavController) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contraseña by remember { mutableStateOf("") }
    var confirmarContraseña by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var contraseñaVisible by remember { mutableStateOf(false) } // Estado para la visibilidad de la contraseña
    var confirmarContraseñaVisible by remember { mutableStateOf(false) } // Estado para la visibilidad de la confirmación de contraseña
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val buttonColor = Color(0xFF1F4D96) // Color personalizado para los botones

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Imagen de fondo que se adapta a la pantalla
        Image(
            painter = painterResource(id = R.drawable.borde), // Reemplaza con tu imagen de fondo
            contentDescription = "Imagen de fondo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, Color(0xFF1F4D96), shape = RoundedCornerShape(8.dp))
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo") },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, Color(0xFF1F4D96), shape = RoundedCornerShape(8.dp))
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Campo de Contraseña con visibilidad toggle
            OutlinedTextField(
                value = contraseña,
                onValueChange = { contraseña = it },
                label = { Text("Contraseña") },
                visualTransformation = if (contraseñaVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (contraseñaVisible) {
                        painterResource(id = R.drawable.close) // Reemplaza con el ícono "ocultar contraseña"
                    } else {
                        painterResource(id = R.drawable.open) // Reemplaza con el ícono "ver contraseña"
                    }
                    IconButton(onClick = { contraseñaVisible = !contraseñaVisible }) {
                        Icon(
                            painter = image,
                            contentDescription = if (contraseñaVisible) "Ocultar contraseña" else "Mostrar contraseña",
                            modifier = Modifier.size(20.dp),
                            tint = Color(0xFF1F4D96)
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, Color(0xFF1F4D96), shape = RoundedCornerShape(8.dp))
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Campo para confirmar la contraseña con visibilidad toggle
            OutlinedTextField(
                value = confirmarContraseña,
                onValueChange = { confirmarContraseña = it },
                label = { Text("Confirmar Contraseña") },
                visualTransformation = if (confirmarContraseñaVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (confirmarContraseñaVisible) {
                        painterResource(id = R.drawable.close) // Reemplaza con el ícono "ocultar contraseña"
                    } else {
                        painterResource(id = R.drawable.open) // Reemplaza con el ícono "ver contraseña"
                    }
                    IconButton(onClick = { confirmarContraseñaVisible = !confirmarContraseñaVisible }) {
                        Icon(
                            painter = image,
                            contentDescription = if (confirmarContraseñaVisible) "Ocultar contraseña" else "Mostrar contraseña",
                            modifier = Modifier.size(20.dp),
                            tint = Color(0xFF1F4D96)
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, Color(0xFF1F4D96), shape = RoundedCornerShape(8.dp))
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono") },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, Color(0xFF1F4D96), shape = RoundedCornerShape(8.dp))
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (nombre.isBlank() || correo.isBlank() || contraseña.isBlank() || confirmarContraseña.isBlank() || telefono.isBlank()) {
                        errorMessage = "Por favor completa todos los campos."
                    } else if (contraseña != confirmarContraseña) {
                        errorMessage = "Las contraseñas no coinciden."
                    } else {
                        auth.createUserWithEmailAndPassword(correo, contraseña)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = hashMapOf(
                                        "nombre" to nombre,
                                        "correo" to correo,
                                        "telefono" to telefono
                                    )
                                    val userId = auth.currentUser?.uid
                                    if (userId != null) {
                                        db.collection("Usuario").document(userId)
                                            .set(user)
                                            .addOnSuccessListener {
                                                showSuccessDialog = true
                                            }
                                            .addOnFailureListener { e ->
                                                errorMessage = "Error al guardar los datos: ${e.message}"
                                            }
                                    } else {
                                        errorMessage = "Error al obtener el ID de usuario."
                                    }
                                } else {
                                    errorMessage = task.exception?.message
                                }
                            }
                    }
                },
                colors = ButtonDefaults.buttonColors(buttonColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.registro), // Reemplaza con tu ícono
                    contentDescription = "Icono de registro",
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Registrar", color = Color.White, fontSize = 16.sp)
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = it, color = Color.Red)
            }
        }

        // Ventana emergente de confirmación de éxito
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { /* No cerrar con clic fuera del diálogo */ },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showSuccessDialog = false
                            navController.navigate("login")
                        }
                    ) {
                        Text("Aceptar")
                    }
                },
                title = { Text("Registro Exitoso") },
                text = { Text("Usuario registrado correctamente.") }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(navController = rememberNavController())
}
