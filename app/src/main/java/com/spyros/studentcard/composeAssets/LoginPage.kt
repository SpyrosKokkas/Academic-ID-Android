package com.spyros.studentcard.composeAssets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spyros.studentcard.R

@Composable
/*
Composable για την οθόνη του Login Page. Συγκεκριμένα περιέχει δύο πεδία στα οποία
θα εισάγει ο χρήστης τα στοιχεία εισόδου του και δύο κουμπιά τα οποία πραγματοποιούν
την είσοδο ή την εγγραφή του χρήστη.
 */
fun LoginPage(
    onLogin: (String, String) -> Unit,
    onRegister: (String, String) -> Unit
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .drawBehind {
            val brush = Brush.verticalGradient(
                colors = listOf(Color.White, Color(0xFF112693)),
                startY = 0f,
                endY = size.height
            )
            drawRect(brush = brush)
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Ακαδημαική Ταυτότητα",
                modifier = Modifier
                    .padding(15.dp)
                    .padding(top = 20.dp),
                fontSize = 30.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(30.dp))

            Image(
                painter = painterResource(id = R.drawable.uni_png_cap),
                contentDescription = null,
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(120.dp))

            var username by rememberSaveable { mutableStateOf("") }
            var password by rememberSaveable { mutableStateOf("") }

            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Όνομα Χρήστη") },
                textStyle = TextStyle(fontSize = 18.sp),

                colors = TextFieldDefaults.textFieldColors(
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                )
            )
            Spacer(modifier = Modifier.height(25.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Κωδικός Πρόσβασης") },
                textStyle = TextStyle(fontSize = 18.sp),
                colors = TextFieldDefaults.textFieldColors(
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black
                ),
                visualTransformation = PasswordVisualTransformation(), // Hide password input
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { onLogin(username, password) },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xff829aef))
            ) {
                Text(text = "Είσοδος",
                        fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,)
            }
            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { onRegister(username, password) },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xff829aef))
            ) {
                Text(text = "Εγγραφή",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp)
            }
        }
    }
}

@Preview
@Composable
fun PreviewLoginPage() {
    LoginPage(
        onLogin = { _, _ -> },
        onRegister = { _, _ -> }
    )
}