package hr.ferit.tomislavcelic.gamecompanion.ui.screens.createeditevent

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import hr.ferit.tomislavcelic.gamecompanion.R
import hr.ferit.tomislavcelic.gamecompanion.data.model.GameEvent
import hr.ferit.tomislavcelic.gamecompanion.ui.datetime.DateTimeField
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOrEditEventScaffold(
    nav : NavHostController,
    originalEvent : GameEvent? = null,
    presetGameKey : String? = null,
    isChallenge:  Boolean
) {
    val viewModel: CreateEditEventViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                CreateEditEventViewModel(originalEvent, presetGameKey, isChallenge) as T
        }
    )

    val allGames by viewModel.allGames.collectAsState()
    val saveOK by viewModel.saveEnabled.collectAsState()
    val challenge by viewModel.isChallenge.collectAsState()

    val context = LocalContext.current

    val cropLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val resultUri = UCrop.getOutput(result.data!!)
            resultUri?.let { croppedUri ->
                val image = InputImage.fromFilePath(context, croppedUri)
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        viewModel.applyOcrResult(visionText.text)
                    }
                    .addOnFailureListener { e ->
                        // TODO: Snackbar
                    }
            }
        }
    }

    val options = UCrop.Options().apply {
        setFreeStyleCropEnabled(true)
        setToolbarColor(ContextCompat.getColor(context, R.color.purple_500))
        setStatusBarColor(ContextCompat.getColor(context, R.color.purple_700))
        setActiveControlsWidgetColor(ContextCompat.getColor(context, R.color.teal_200))
    }
    options.setFreeStyleCropEnabled(true)

    val cacheFile = remember { File(context.cacheDir, "camera.jpg") }
    val cameraUri = remember {
        FileProvider.getUriForFile(
            context, "${context.packageName}.fileprovider", cacheFile
        )
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode != Activity.RESULT_OK) return@rememberLauncherForActivityResult
        val source = res.data?.data ?: cameraUri

        val dest = Uri.fromFile(File(context.cacheDir, "cropped.jpg"))
        val uCrop = UCrop.of(source, dest)
            .withOptions(options)
            .getIntent(context)

        cropLauncher.launch(uCrop)
    }

    fun openSystemPicker() {
        val cameraInt = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, cameraUri)
            addFlags(
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
        val galleryInt = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        val chooser = Intent.createChooser(galleryInt, "Select image").apply {
            putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraInt))
        }
        photoPickerLauncher.launch(chooser)
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openSystemPicker()
        } else {
            // Handle permission denied - could show a snackbar or toast
            Toast.makeText(context, "Camera permission is required for autofill feature", Toast.LENGTH_SHORT).show()
        }
    }

    fun requestCameraPermissionOrOpenPicker() {
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openSystemPicker()
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    Scaffold(
        topBar = {
            val title = if (challenge) "Create challenge" else "Create event"
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = { requestCameraPermissionOrOpenPicker() }
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "OCR")
                        Spacer(Modifier.width(8.dp))
                        Text("Autofill")
                    }

                    TextButton(
                        enabled = saveOK,
                        onClick = {
                            viewModel.save(
                                onDone = { nav.popBackStack() },
                                onError = { /* TODO: snackbar */ }
                            )
                        }
                    ) { Text("Save") }
                }
            )
        }
    ) { padding ->

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = viewModel.title.collectAsState().value,
                onValueChange = { viewModel.title.value = it },
                label = { Text("Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            GamePicker(
                allGames = allGames,
                selectedKey = viewModel.selectedKey.collectAsState().value,
                onPick = { viewModel.selectedKey.value = it }
            )

            DateTimeField(
                label = "Starts*",
                timestamp = viewModel.starts.collectAsState().value,
                onPick = { ts -> viewModel.starts.value = ts }
            )
            DateTimeField(
                label = "Expires*",
                timestamp = viewModel.expires.collectAsState().value,
                onPick = { ts -> viewModel.expires.value = ts }
            )

            OutlinedTextField(
                value = viewModel.additionalInfo.collectAsState().value,
                onValueChange = { viewModel.additionalInfo.value = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            if (challenge) {
                OutlinedTextField(
                    value = viewModel.goal.collectAsState().value.toString(),
                    onValueChange = { viewModel.goal.value = it.toIntOrNull() ?: 0 },
                    label = { Text("Goal (e.g. 10 wins)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = viewModel.challengeInfo.collectAsState().value,
                    onValueChange = { viewModel.challengeInfo.value = it },
                    label = { Text("Challenge notes") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }

            val modeSwitchButtonText = if (!challenge) "Add a challenge instead" else "Add an event instead"
            TextButton(onClick = viewModel::toggleChallenge)
            { Text(modeSwitchButtonText) }

            Text(
                "* Expires is required.  Starts may be blank (starts immediately).",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
