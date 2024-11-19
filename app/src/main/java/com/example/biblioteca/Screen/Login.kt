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
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) } // Estado para la visibilidad de la contraseña
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val buttonColor = Color(0xFF1F4D96) // Color personalizado para botones

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Imagen de fondo adaptada a la pantalla y visible completamente
        Image(
            painter = painterResource(id = R.drawable.borde),
            contentDescription = "Imagen de fondo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Contenido principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Imagen encima de los campos de texto
            Image(
                painter = painterResource(id = R.drawable.libros), // Reemplaza con tu recurso de imagen
                contentDescription = "Logo de la aplicación",
                modifier = Modifier
                    .size(200.dp) // Ajusta el tamaño de la imagen según prefieras
                    .padding(bottom = 16.dp)
                    .border(4.dp, Color(0xFF1F4D96), RoundedCornerShape(8.dp))
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo") },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, Color(0xFF1F4D96), shape = RoundedCornerShape(8.dp))
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) {
                        painterResource(id = R.drawable.close) // Reemplaza con el ícono "ocultar contraseña"
                    } else {
                        painterResource(id = R.drawable.open) // Reemplaza con el ícono "ver contraseña"
                    }
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = image,
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
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
            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    // Validación de campos vacíos
                    if (email.isBlank() || password.isBlank()) {
                        errorMessage = "Por favor, complete todos los campos"
                    } else {
                        // Si los campos están llenos, intenta iniciar sesión
                        scope.launch {
                            FirebaseAuth.getInstance()
                                .signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        // Autenticación exitosa, navegar a la pantalla principal
                                        navController.navigate("inicio") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    } else {
                                        // Mostrar mensaje de error
                                        errorMessage = "Usuario o contraseña incorrectos"
                                    }
                                }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(buttonColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.sesion), // Reemplaza con tu icono
                    contentDescription = "Icono de inicio de sesión",
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Iniciar Sesión", color = Color.White, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    navController.navigate("registro")
                },
                colors = ButtonDefaults.buttonColors(buttonColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.cuenta), // Reemplaza con tu icono
                    contentDescription = "Icono de crear cuenta",
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Crear Cuenta", color = Color.White, fontSize = 16.sp)
            }

            // Mensaje de error
            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(navController = rememberNavController())
}
