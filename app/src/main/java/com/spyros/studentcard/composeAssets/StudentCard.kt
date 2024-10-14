package com.spyros.studentcard.composeAssets

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.preferencesDataStore
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.zxing.qrcode.QRCodeWriter
import com.spyros.studentcard.R
import kotlinx.coroutines.flow.MutableStateFlow

val Context.dataStore by preferencesDataStore(name = "settings")


@OptIn(ExperimentalCoilApi::class)
@Composable
fun StudentCard(
    name: String,
    surname: String,
    address: String,
    imageId: String,
    regDate: String,
    idNumber: String,
    ticket: String,
    uni: String,
    uniImage: Int,
    regNumber: String,
    attribute: String,
    context: Context,
    returnBtn: (Boolean, Context) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val qrCodeBitmap = remember { mutableStateOf(generateQRCode(idNumber)) }

/*
Δημιουργεία των MutableStateFlows για την διαχείριση των καταστάσεων των μεταβλητών
που κρατάνε τα στοιχεια του πάσου. Οι τιμές των flows συλλέγονται ως State χρησιμοποιώντας την
συνάρτηση collectAsState, επιτρέποντας την αυτόματη ενημέρωση του UI κάθε φορά που αλλάζουν
οι καταστάσεις.
*/

    val uniFlow = MutableStateFlow(uni)
    val uniState by uniFlow.collectAsState()

    val nameFlow = MutableStateFlow(name)
    val nameState by nameFlow.collectAsState()

    val surnameFlow = MutableStateFlow(surname)
    val surnameState by surnameFlow.collectAsState()

    val regDateFlow = MutableStateFlow(regDate)
    val regDateState by regDateFlow.collectAsState()

    val ticketFlow = MutableStateFlow(ticket)
    val ticketState by ticketFlow.collectAsState()

    val imageFlow = MutableStateFlow(imageId)
    val imageState by imageFlow.collectAsState()

    val idNumberFlow = MutableStateFlow(idNumber)
    val idNumberState by idNumberFlow.collectAsState()

    val addressFlow = MutableStateFlow(address)
    val addressState by addressFlow.collectAsState()

    val regNumberFlow = MutableStateFlow(regNumber)
    val regNumberState by regNumberFlow.collectAsState()

    var returnClicked by remember { mutableStateOf(true) }


    // Εμφάνιση του QR code μέσω του alertDialog Composable όταν Ο χρήστης πατάει το Floating Action Button
    if (showDialog) {
        AlertDialog(onDismiss = { showDialog = false }, qrCodeBitmap = qrCodeBitmap.value , idNumber = idNumberState)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                val brush = Brush.verticalGradient(
                    colors = listOf(Color.White, Color(0xFF112673)),
                    startY = 0f,
                    endY = size.height
                )
                drawRect(brush = brush)
            }
    ) {
        Button(
            onClick = {
                returnClicked = true
                returnBtn(returnClicked, context)
            },
            colors = androidx.compose.material.ButtonDefaults.buttonColors(
                backgroundColor = Color.Transparent

            ),
            elevation = null,
            modifier = Modifier
                .border(0.dp, Color.Transparent)

        ) {
            Image(
                painter = painterResource(id = R.drawable.back_arrow),
                contentDescription = null,
                Modifier.size(30.dp)
            )
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "Academic ID \nΑκαδημαική Ταυτότητα",
                textAlign = TextAlign.Center,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(top = 10.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Προβολή όλων των στοιχείων του πάσου που τράβηκε απο τα Flow state

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = uniImage),
                    contentDescription = null,
                    modifier = Modifier.size(60.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = uniState,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Image(
                painter = rememberImagePainter(imageState),
                contentDescription = null,
                modifier = Modifier.size(150.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = attribute,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )
            Spacer(modifier = Modifier.height(25.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Ονοματεπώνυμο",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(start = 30.dp)
                    )
                    Text(
                        text = "$nameState\n$surnameState",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(start = 30.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Ημερομηνία\nΕγγραφής",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(end = 30.dp)
                    )
                    Text(
                        text = regDateState,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(end = 30.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Δελτίο Εισιτηρίου",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier
                            .padding(start = 30.dp)
                    )
                    Text(
                        text = ticketState,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(start = 30.dp)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Αριθμός \nΜητρώου",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(end = 35.dp)
                    )
                    Text(
                        text = regNumberState,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(end = 35.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Αριθμός \nΤαυτότητας",
                    fontSize = 17.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Normal,
                )
                Text(
                    text = idNumberState,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Διεύθυνση",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal,
                )
                Text(
                    text = addressState,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
        }


        //Εμφάνιση του QR του αριθμού Ταυτότητας

        FloatingActionButton(
            onClick = { showDialog = true },
            backgroundColor = Color.LightGray,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .size(45.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.more_info),
                contentDescription = "More Info"
            )
        }
    }
}

//Δημιουργία του QR code χρησιμοποιώντας την βιβλιοθήκη ZXing
fun generateQRCode(idNumberState: String): Bitmap {
    val data = idNumberState
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(data, com.google.zxing.BarcodeFormat.QR_CODE, 1024, 1024)
    val width = bitMatrix.width
    val height = bitMatrix.height
    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
    for (x in 0 until width) {
        for (y in 0 until height) {
            bmp.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
        }
    }
    return bmp
}


@Preview(

    name = "Realme GT Neo 2",
    widthDp = 360,
    heightDp = 800
)
@Composable
fun PreviewStudentCard() {
    StudentCard(
        name = "John",
        surname = "Doe",
        address = "123 Main St",
        imageId = "https://firebasestorage.googleapis.com/v0/b/studentcard-5714c.appspot.com/o/Student_Ids%2FIMG20230921155115.jpg?alt=media&token=501fafc6-0fe0-4d00-b0be-80d4757a6740", // Example URL
        regDate = "2023-09-01",
        idNumber = "123456789",
        ticket = "987654321",
        uni = "Πανεπιστήμιο Πειραιώς",
        uniImage = R.drawable.uni_png_cap,
        regNumber = "Π19073",
        attribute = "Φοιτητης",
        context = LocalContext.current,
        returnBtn = { returnClicked, context ->}
    )
}
