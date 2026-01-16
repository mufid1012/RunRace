package com.example.runrace_finalproject.ui.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.runrace_finalproject.ui.components.LoadingIndicator
import com.example.runrace_finalproject.utils.Constants
import com.example.runrace_finalproject.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditEventScreen(
    eventId: Int? = null,
    onNavigateBack: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val formState by viewModel.formState.collectAsState()
    val uploadState by viewModel.uploadState.collectAsState()
    val isEditing = eventId != null
    
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(Constants.STATUS_UPCOMING) }
    var bannerUrl by remember { mutableStateOf("") }
    var statusExpanded by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    val statusOptions = listOf(
        Constants.STATUS_ONGOING to "Berlangsung",
        Constants.STATUS_UPCOMING to "Akan Datang",
        Constants.STATUS_COMPLETED to "Selesai"
    )
    
    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedImageUri = it
            viewModel.uploadImage(it)
        }
    }
    
    LaunchedEffect(eventId) {
        if (eventId != null) {
            viewModel.loadEventForEdit(eventId)
        } else {
            viewModel.resetFormState()
        }
        viewModel.resetUploadState()
    }
    
    LaunchedEffect(formState.event) {
        formState.event?.let { event ->
            name = event.name
            location = event.location
            category = event.category
            date = event.date
            status = event.status
            bannerUrl = event.bannerUrl ?: ""
        }
    }
    
    // Update bannerUrl when upload completes
    LaunchedEffect(uploadState.uploadedUrl) {
        uploadState.uploadedUrl?.let { url ->
            bannerUrl = url
        }
    }
    
    LaunchedEffect(uploadState.error) {
        uploadState.error?.let { error ->
            snackbarHostState.showSnackbar("Upload gagal: $error")
        }
    }
    
    LaunchedEffect(formState.saveSuccess) {
        if (formState.saveSuccess) {
            snackbarHostState.showSnackbar(
                if (isEditing) "Event berhasil diperbarui!" else "Event berhasil ditambahkan!"
            )
            viewModel.resetFormState()
            onNavigateBack()
        }
    }
    
    LaunchedEffect(formState.error) {
        formState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Event" else "Tambah Event") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        if (formState.isLoading && isEditing && formState.event == null) {
            LoadingIndicator(modifier = Modifier.padding(padding))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Image Picker Section
                Text(
                    text = "Banner Event",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (uploadState.isUploading) {
                        CircularProgressIndicator()
                    } else if (bannerUrl.isNotBlank()) {
                        AsyncImage(
                            model = bannerUrl,
                            contentDescription = "Banner",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Selected Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Klik untuk pilih gambar",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Optional: Manual URL input
                OutlinedTextField(
                    value = bannerUrl,
                    onValueChange = { bannerUrl = it },
                    label = { Text("Atau masukkan URL Banner") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("https://example.com/banner.jpg") }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Event Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Event") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Location
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Lokasi") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Category
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Kategori") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("5K, 10K, Half Marathon, dll") }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Date
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Tanggal") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("2024-12-31") }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Status Dropdown
                ExposedDropdownMenuBox(
                    expanded = statusExpanded,
                    onExpandedChange = { statusExpanded = !statusExpanded }
                ) {
                    OutlinedTextField(
                        value = statusOptions.find { it.first == status }?.second ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Status") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false }
                    ) {
                        statusOptions.forEach { (value, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    status = value
                                    statusExpanded = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Save Button
                Button(
                    onClick = {
                        if (isEditing && eventId != null) {
                            viewModel.updateEvent(
                                eventId = eventId,
                                name = name,
                                location = location,
                                category = category,
                                date = date,
                                status = status,
                                bannerUrl = bannerUrl.ifBlank { null }
                            )
                        } else {
                            viewModel.createEvent(
                                name = name,
                                location = location,
                                category = category,
                                date = date,
                                status = status,
                                bannerUrl = bannerUrl.ifBlank { null }
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !formState.isLoading &&
                             !uploadState.isUploading &&
                             name.isNotBlank() &&
                             location.isNotBlank() &&
                             category.isNotBlank() &&
                             date.isNotBlank()
                ) {
                    if (formState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(if (isEditing) "Simpan Perubahan" else "Tambah Event")
                    }
                }
            }
        }
    }
}
