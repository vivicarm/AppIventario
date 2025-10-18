package com.app.myappdeinventario.views

import android.R.attr.text
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.app.myappdeinventario.R
import com.app.myappdeinventario.model.Categoria
import com.app.myappdeinventario.model.Usuario
import com.app.myappdeinventario.viewModel.AuthUiState
import com.app.myappdeinventario.viewModel.AuthViewModel
import com.app.myappdeinventario.views.ui.theme.MyAppDeInventarioTheme
import com.app.myappdeinventario.views.ui.theme.blanco
import com.app.myappdeinventario.views.ui.theme.semiBlanco
import com.app.myappdeinventario.views.ui.theme.verdeAzuladoMedio
import com.app.myappdeinventario.views.ui.theme.verdeOscuroProfundo
import com.app.myappdeinventario.views.ui.theme.verdePetróleoOscuro

class IniciarSesionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyAppDeInventarioTheme {
                 LoginScrem()
                }
            }
        }
    }

@Composable
fun LoginScrem(viewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {

    var usuario by remember { mutableStateOf(Usuario()) } // llama al model usuario

    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Imagen superior
        Image(
            painter = painterResource(id = R.drawable.imagewelcome),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f),
            contentScale = ContentScale.Crop
        )

        // Card blanco con bordes redondeados
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.60f)
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            colors = CardDefaults.cardColors(
                containerColor = blanco
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            // Contenido del card
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Iniciar Sesión",
                    style = MaterialTheme.typography.headlineMedium,
                    color = verdeOscuroProfundo

                )

                Spacer(modifier = Modifier.height(30.dp))

                // Campo correo
                OutlinedTextField(
                    value = usuario.email,
                    onValueChange = {usuario = usuario.copy(email = it) },
                    label = { Text("Correo electrónico") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = verdePetróleoOscuro,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = verdeAzuladoMedio,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = verdeAzuladoMedio,
                        unfocusedLabelColor = Color.Gray,
                        cursorColor = verdeAzuladoMedio
                    )
                )

                Spacer(modifier = Modifier.height(15.dp))

                // Campo contraseña
                OutlinedTextField(
                    value = usuario.password,
                    onValueChange = { usuario = usuario.copy(password= it)},
                    label = { Text("Contraseña") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible)
                            Icons.Filled.Visibility
                        else
                            Icons.Filled.VisibilityOff

                        val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = description)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = verdePetróleoOscuro,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = verdeAzuladoMedio,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = verdeAzuladoMedio,
                        unfocusedLabelColor = Color.Gray,
                        cursorColor = verdeAzuladoMedio
                    )
                )

                Spacer(modifier = Modifier.height(25.dp))

                //boton iniciar sesion
                Button(
                    onClick = {
                        if (emailError== null && passwordError == null &&
                            usuario.email.isNotBlank() && usuario.password.isNotBlank())
                        { viewModel.login(usuario.email, usuario.password) } },
                ) {
                    Text("Iniciar Sesión")
                }

                when (uiState) {
                    is AuthUiState.Loading -> {
                        Text("Iniciando sesión...", color = Color.Gray)
                    }
                    is AuthUiState.Success -> {
                        Text((uiState as AuthUiState.Success).message, color = Color.Green)

                        LaunchedEffect(Unit) {
                            val intent = Intent(context, MainActivity::class.java)
                            context.startActivity(intent)
                        }
                    }
                    is AuthUiState.Error -> {
                        Text((uiState as AuthUiState.Error).message, color = Color.Red)
                    }
                    else -> Unit
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Botón para registrarse
                TextButton(
                    onClick = {
                        val intent = Intent(context, RegistroActivity::class.java)
                        context.startActivity(intent)
                    }
                ) {
                    Text("¿No tienes cuenta? Registrarse")
                }
            }
        }
    }
}

