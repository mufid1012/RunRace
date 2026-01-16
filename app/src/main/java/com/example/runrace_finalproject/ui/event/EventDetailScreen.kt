package com.example.runrace_finalproject.ui.event

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.runrace_finalproject.ui.components.ErrorMessage
import com.example.runrace_finalproject.ui.components.LoadingIndicator
import com.example.runrace_finalproject.utils.Constants
import com.example.runrace_finalproject.viewmodel.EventViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    eventId: Int,
    onNavigateBack: () -> Unit,
    viewModel: EventViewModel = hiltViewModel()
) {
    val state by viewModel.detailState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(eventId) {
        viewModel.loadEventDetail(eventId)
    }
    
    LaunchedEffect(state.registrationSuccess) {
        if (state.registrationSuccess) {
            snackbarHostState.showSnackbar("Berhasil mendaftar event!")
            viewModel.resetRegistrationSuccess()
        }
    }
    
    LaunchedEffect(state.unregistrationSuccess) {
        if (state.unregistrationSuccess) {
            snackbarHostState.showSnackbar("Berhasil membatalkan pendaftaran!")
            viewModel.resetUnregistrationSuccess()
        }
    }
    
    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearDetailError()
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Detail Event") },
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
        when {
            state.isLoading -> {
                LoadingIndicator(modifier = Modifier.padding(padding))
            }
            state.error != null && state.event == null -> {
                ErrorMessage(
                    message = state.error!!,
                    onRetry = { viewModel.loadEventDetail(eventId) },
                    modifier = Modifier.padding(padding)
                )
            }
            state.event != null -> {
                val event = state.event!!
                
                // Calculate days until event and registration status
                val daysUntilEvent = try {
                    val eventDate = LocalDate.parse(event.date, DateTimeFormatter.ISO_LOCAL_DATE)
                    val today = LocalDate.now()
                    ChronoUnit.DAYS.between(today, eventDate)
                } catch (e: Exception) {
                    -1L // Error parsing date
                }
                
                val canRegister = daysUntilEvent > 7
                val isEventPast = daysUntilEvent < 0
                
                // Determine actual status based on date
                val actualStatus = when {
                    isEventPast -> "completed"
                    daysUntilEvent <= 7 -> "ongoing"
                    else -> "upcoming"
                }
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Banner
                    AsyncImage(
                        model = event.bannerUrl ?: "https://via.placeholder.com/400x200",
                        contentDescription = event.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        contentScale = ContentScale.Crop
                    )
                    
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Status Badge
                        val statusColor = when (actualStatus) {
                            "ongoing" -> Color(0xFFFF9800) // Orange for H-7
                            "upcoming" -> Color(0xFF4CAF50) // Green for open registration
                            else -> MaterialTheme.colorScheme.tertiary
                        }
                        val statusText = when (actualStatus) {
                            "ongoing" -> "Pendaftaran Ditutup"
                            "upcoming" -> "Pendaftaran Dibuka"
                            else -> "Selesai"
                        }
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = statusColor.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = statusText,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = statusColor,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            
                            if (state.isRegistered) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "✓ Terdaftar",
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                        
                        // Days until event info
                        if (daysUntilEvent > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (daysUntilEvent == 1L) "Besok!" else "H-$daysUntilEvent",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (daysUntilEvent <= 7) Color(0xFFFF5722) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Event Name
                        Text(
                            text = event.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Category
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = event.category,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Info Cards
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                // Date
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Tanggal",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = event.date,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                                
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 12.dp)
                                )
                                
                                // Location
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Lokasi",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = event.location,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        // Action Buttons
                        when {
                            isEventPast -> {
                                // Event sudah lewat
                                OutlinedButton(
                                    onClick = { },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    enabled = false,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        "Event Telah Selesai",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }
                            state.isRegistered -> {
                                // Sudah terdaftar - bisa batalkan
                                OutlinedButton(
                                    onClick = { viewModel.unregisterFromEvent(event.id) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    enabled = !state.isUnregistering,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    if (state.isUnregistering) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Cancel,
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "Batalkan Pendaftaran",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                }
                            }
                            canRegister -> {
                                // Bisa daftar (lebih dari 7 hari)
                                Button(
                                    onClick = { viewModel.registerForEvent(event.id) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    enabled = !state.isRegistering,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    if (state.isRegistering) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.HowToReg,
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "Daftar Event",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                }
                            }
                            else -> {
                                // H-7 atau kurang - pendaftaran ditutup
                                OutlinedButton(
                                    onClick = { },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    enabled = false,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Pendaftaran Ditutup (H-7)",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
