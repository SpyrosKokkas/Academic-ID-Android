package com.spyros.studentcard.composeAssets

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
// Alert Dialog για την εμφάνιση του QR Code της Ακαδημαϊκής Ταυτότητας
fun AlertDialog(onDismiss: () -> Unit, qrCodeBitmap: Bitmap, idNumber: Any) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { },
        modifier = Modifier.height(350.dp), // Το μέγεθος του παραθύρου
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = "QR Code")
                Text(
                    text = idNumber.toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    bitmap = qrCodeBitmap.asImageBitmap(),
                    contentDescription = idNumber.toString(),
                    modifier = Modifier // Το μέγεθος του QR
                        .width(512.dp)
                        .height(512.dp)
                        .padding(10.dp)
                )
            }
        }
    )
}

@Preview
@Composable
fun AlertDialogPreview() {
    val qrCodeBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    AlertDialog(onDismiss = {}, qrCodeBitmap = qrCodeBitmap, idNumber = "12843254323")

}