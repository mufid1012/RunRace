package com.example.runrace_finalproject.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.runrace_finalproject.ui.components.LoadingIndicator
import com.example.runrace_finalproject.viewmodel.AdminNewsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNewsScreen(
    newsId: Int? = null,
    onNavigateBack: () -> Unit,
    viewModel: AdminNewsViewModel = hiltViewModel()
) {
    val editState by viewModel.editState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    val isEditMode = newsId != null && newsId > 0
    
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    
    LaunchedEffect(newsId) {
        if (isEditMode) {
            viewModel.loadNewsById(newsId!!)
        } else {
            viewModel.resetEditState()
        }
    }
    
    LaunchedEffect(editState.news) {
        editState.news?.let { news ->
            title = news.title
            content = news.content
            imageUrl = news.imageUrl ?: ""
        }
    }
    
    LaunchedEffect(editState.saveSuccess) {
        if (editState.saveSuccess) {
            snackbarHostState.showSnackbar(
                if (isEditMode) "Berita berhasil diperbarui"
                else "Berita berhasil dibuat"
            )
            viewModel.resetSaveSuccess()
            onNavigateBack()
        }
    }
    
    LaunchedEffect(editState.error) {
        editState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearEditError()
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (isEditMode) "Edit Berita" else "Tambah Berita",
                        fontWeight = FontWeight.Bold
                    ) 
                },
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
        if (editState.isLoading && isEditMode && editState.news == null) {
            LoadingIndicator(modifier = Modifier.padding(padding))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul Berita") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Isi Berita") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    maxLines = 10
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("URL Gambar (Opsional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = {
                        if (isEditMode) {
                            viewModel.updateNews(newsId!!, title, content, imageUrl)
                        } else {
                            viewModel.createNews(title, content, imageUrl)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !editState.isLoading && title.isNotBlank() && content.isNotBlank()
                ) {
                    if (editState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            if (isEditMode) "Perbarui Berita" else "Simpan Berita",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}
