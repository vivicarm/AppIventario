package com.app.myappdeinventario.views

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import android.util.Patterns
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.unit.dp
import com.app.myappdeinventario.viewModel.AuthUiState
import com.app.myappdeinventario.viewModel.AuthViewModel
import com.app.myappdeinventario.views.ui.theme.MyAppDeInventarioTheme

class Registro : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyAppDeInventarioTheme {
                    RegistroScreen()
                }
            }
        }
    }
@Composable 
fun RegistroScreen(
    viewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel())
{

    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var name by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    //validaciones

    var nombreError by remember { mutableStateOf<String?>(null) }
    var lastnameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var numberError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Registrarse",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Campo de nombre
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                nombreError = validarNombre(it)
            },
            label = { Text("Nombre") },
            isError = nombreError != null,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        if (nombreError != null) {
            Text(nombreError ?: "", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(15.dp))

        // Campo de apellidos
        OutlinedTextField(
            value = lastname,
            onValueChange = {
                lastname = it
                lastnameError = validarApellido(it)
            },
            label = { Text("Apellidos") },
            isError = lastnameError != null,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (lastnameError != null) {
            Text(lastnameError ?: "", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(15.dp))

        // Campo de numero
        OutlinedTextField(
            value = number,
            onValueChange = {
                number = it
                numberError = validarNumero(it)
            },
            label = { Text("Numero telefónico") },
            singleLine = true,
            isError = numberError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), // indica el teclado a mostrar
            modifier = Modifier.fillMaxWidth()
        )
        if (numberError != null) {
            Text(numberError ?: "", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(15.dp))

        // Campo de email
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = validarEmail(it)
            },
            label = { Text("Correo electrónico") },
            isError = emailError != null,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        if (emailError != null) {
            Text(emailError ?: "", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(15.dp))

        // Campo de contraseña
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = validarPassword(it)
            },
            label = { Text("Contraseña") },
            singleLine = true,
            isError = passwordError != null,
            visualTransformation = if (passwordVisible)
                androidx.compose.ui.text.input.VisualTransformation.None
            else
                PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible)
                    androidx.compose.material.icons.Icons.Filled.Visibility
                else
                    androidx.compose.material.icons.Icons.Filled.VisibilityOff

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        if (passwordError != null) {
            Text(passwordError ?: "", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(25.dp))

        // Botón de registro
        Button(
            onClick = {
                if (nombreError == null && lastnameError == null &&  numberError == null &&
                    emailError== null && passwordError == null &&
                    name.isNotBlank() && lastname.isNotBlank() &&
                    number.isNotBlank() && email.isNotBlank() && password.isNotBlank()
                ) {
                    viewModel.registro(email, password)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar cuenta")
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Estado Firebase
        when (uiState) {
            is AuthUiState.Loading -> CircularProgressIndicator()
            is AuthUiState.Error -> Text(
                text = (uiState as AuthUiState.Error).message,
                color = MaterialTheme.colorScheme.error
            )

            is AuthUiState.Success -> {
                LaunchedEffect(uiState) {
                    Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                    (context as? ComponentActivity)?.finish() // cierra pantalla registro
                }
            }

            else -> {}
        }

        // Botón para iniciar sesion
        TextButton(
            onClick = {
                val intent = Intent(context, InicioSesionActivity::class.java)
                context.startActivity(intent)
            }
        ) {
            Text("¿Si tienes cuenta? Inicia Sesión")
        }
    }
}
fun validarNombre(nombre: String): String?{
    val soloLetras = Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")
    return when {
        nombre.isBlank() -> "El nombre no puede estar vacío"
        !soloLetras.matches(nombre) -> "Solo se permiten letras"
        else -> null
    }
}

fun validarApellido(apellido: String): String?{
    val soloLetras = Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")
    return when {
        apellido.isBlank() -> "El apellido no puede estar vacío"
        !soloLetras.matches(apellido) -> "Solo se permiten letras"
        else -> null
    }
}

fun validarNumero(numero: String): String? {

    return when {
        numero.isBlank() -> "El número no puede estar vacío"
        numero.length != 9 -> "Debe tener 9 dígitos"
        !numero.all { it.isDigit() } -> "Solo se permiten números"
        else -> null
    }
}

fun validarEmail(email: String): String? {
    return when {
        email.isBlank() -> "El correo no puede estar vacío"
        !Patterns.EMAIL_ADDRESS.matcher(email).matches()  -> "Correo inválido"
        else -> null
    }
}

fun validarPassword(password: String): String? {
    return when {
        password.isBlank() -> "La contraseña no puede estar vacía"
        password.length < 6 -> "Mínimo 6 caracteres"
        else -> null
    }
}