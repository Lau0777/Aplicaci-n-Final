package com.example.biblioteca.Screen

import DevolverLibrosScreen
import Inicio
import LoginScreen
import RegisterScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import catalogo


@Composable
fun navigationExample() {
    val opciones = listOf("Revistas", "Libros Fisicos", "Libros Digitales")
    val navController = rememberNavController()
    val selectedItems = remember { mutableStateListOf<Pair<String, Double>>() }


    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController)
        }
        composable("registro") {
            RegisterScreen(navController)
        }
        composable("inicio") {
            Inicio(navController)
        }
        composable("catalogo") {
            catalogo(navController)
        }
        composable("prestamos") {
            prestamosScreen(navController)
        }
        composable("devolverPrestamos") {
            DevolverLibrosScreen(navController)
        }


    }
}


