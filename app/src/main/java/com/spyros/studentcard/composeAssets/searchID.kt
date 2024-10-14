package com.spyros.studentcard.composeAssets

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.spyros.studentcard.LoginActivity
import com.spyros.studentcard.R

@Composable

/*
Σελίδα στην οποία ο φοιτητής θα πραγματοποιήσει την αναζήτηση του φοιτητικού πάσου.
Συγκεκριμένα θα είσάγει στα αντίστοιχα πεδία το Αριθμό Μητρώου και το Επώνυμο του
και θα επιλέξει εάν θέλει το πάσο του να αποθηκευτεί στην μνήμη cache με τη χρήση του
preferences DataStore.
 */
fun SearchId(
    context: Context,
    searchById: (String, String) -> Unit,
    saveId: (Boolean, String, String) -> Unit,
    onQrCodeClicked: (Boolean) -> Unit,
    onFinishActivity: () -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomAppBar(
                backgroundColor = Color(0xFF112693),
                content = {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(end=10.dp),

                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { logout(context)
                                onFinishActivity()},
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xff829aef))
                        ) {

                            Image(painter = painterResource(id = R.drawable.logout2),
                                contentDescription = null,
                                Modifier.size(25.dp))
                        }

                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .drawBehind {
                    val brush = Brush.verticalGradient(
                        colors = listOf(Color.White, Color(0xFF112693)),
                        startY = 0f,
                        endY = size.height
                    )
                    drawRect(brush = brush)
                }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Eύρεση Ακαδημαϊκής Ταυτότητας",
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 20.dp)
                )
                Spacer(modifier = Modifier.padding(10.dp))

                Column(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.uni_png_cap),
                        contentDescription = null,
                        modifier = Modifier
                            .size(150.dp)
                            .align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.padding(10.dp))
                    Text(
                        text = "Στοιχεία Φοιτητή",
                        fontSize = 20.sp,
                        textAlign = TextAlign.Left,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 20.dp, bottom = 20.dp, end = 5.dp)
                    )

                    var regNumber by rememberSaveable { mutableStateOf("") }
                    var lastName by rememberSaveable { mutableStateOf("") }
                    var checked by remember { mutableStateOf(true) }
                    //var clicked by remember { mutableStateOf(false) }

                    TextField(
                        value = regNumber,
                        onValueChange = { regNumber = it },
                        label = { Text("Aριθμός Μητρώου") },
                        textStyle = TextStyle(fontSize = 18.sp),
                        colors = TextFieldDefaults.textFieldColors(
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.padding(bottom = 20.dp, start = 10.dp)
                    )

                    TextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = { Text("Επώνυμο") },
                        textStyle = TextStyle(fontSize = 18.sp),
                        colors = TextFieldDefaults.textFieldColors(
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Send
                        ),
                        modifier = Modifier.padding(start = 10.dp)
                    )

                    Spacer(modifier = Modifier.padding(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Αποθήκευση Πάσου",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { checked = it }
                        )
                        Button(
                            onClick = {
                                searchById(regNumber, lastName)
                                saveId(checked, regNumber, lastName)
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xff829aef))
                        ) {
                            Text(
                                text = "Αναζήτηση",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.padding(10.dp))

                    Image(
                        painter = painterResource(id = R.drawable.qr_logo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(180.dp)
                            .align(Alignment.CenterHorizontally)
                            .clickable {
                                onQrCodeClicked(true)
                            }
                    )
                }
            }
        }
    }
}

//Συνάρτηση για τη διαχείριση της αποσύνδεσης και μεταφορά του χρήστη στην αρχική σελίδα.
fun logout(context: Context) {
    val auth = FirebaseAuth.getInstance()
    auth.signOut()
    val intent = Intent(context, LoginActivity::class.java)
    context.startActivity(intent)

}

@Preview(
    name = "Realme GT Neo 2",
    widthDp = 360,
    heightDp = 800,
    //showSystemUi = true
)
@Composable
fun PreviewSearchId() {
    SearchId(searchById = { _, _ -> }, saveId = { _, _, _ -> }, onQrCodeClicked = {}, context = LocalContext.current, onFinishActivity = {})
}
