package gourmetgo.client.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

@Composable
fun ProfileImage(
    imageUrl: String?,
    isChef: Boolean,
    onImageCaptured: (Uri) -> Unit,
    onImageUploadError: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showCameraDialog by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var loadedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoadingImage by remember { mutableStateOf(false) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showCameraDialog = true
        } else {
            onImageUploadError("Permiso de cámara requerido")
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && imageUri != null) {
            onImageCaptured(imageUri!!)
        }
    }

    fun createImageUri(): Uri {
        val image = File(context.filesDir, "camera_photo_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            image
        )
    }

    LaunchedEffect(imageUrl) {
        if (isValidImageUrl(imageUrl) && imageUrl != null) {
            isLoadingImage = true
            try {
                loadedBitmap = withContext(Dispatchers.IO) {
                    val connection = URL(imageUrl).openConnection()
                    connection.connectTimeout = 10000
                    connection.readTimeout = 10000
                    val inputStream = connection.getInputStream()
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream.close()
                    bitmap
                }
            } catch (e: Exception) {
                loadedBitmap = null
            } finally {
                isLoadingImage = false
            }
        } else {
            loadedBitmap = null
            isLoadingImage = false
        }
    }

    Box(
        modifier = modifier
            .size(120.dp)
            .clip(CircleShape)
            .clickable {
                val permission = Manifest.permission.CAMERA
                when (PackageManager.PERMISSION_GRANTED) {
                    ContextCompat.checkSelfPermission(context, permission) -> {
                        showCameraDialog = true
                    }
                    else -> {
                        cameraPermissionLauncher.launch(permission)
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoadingImage -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            loadedBitmap != null -> {
                Image(
                    bitmap = loadedBitmap!!.asImageBitmap(),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            if (isChef) Icons.Default.Restaurant else Icons.Default.Person,
                            contentDescription = if (isChef) "Chef" else "Usuario",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (isChef) "Chef" else "Usuario",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(32.dp)
                .background(
                    MaterialTheme.colorScheme.primary,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.CameraAlt,
                contentDescription = "Cambiar foto",
                modifier = Modifier.size(18.dp),
                tint = Color.White
            )
        }
    }

    if (showCameraDialog) {
        AlertDialog(
            onDismissRequest = { showCameraDialog = false },
            title = { Text("Cambiar foto de perfil") },
            text = { Text("¿Deseas tomar una nueva foto?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCameraDialog = false
                        imageUri = createImageUri()
                        cameraLauncher.launch(imageUri!!)
                    }
                ) {
                    Text("Tomar foto")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showCameraDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

private fun isValidImageUrl(url: String?): Boolean {
    return !url.isNullOrBlank() &&
            (url.startsWith("https://res.cloudinary.com/") ||
                    url.startsWith("https://images.unsplash.com/"))
}