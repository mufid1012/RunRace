<?php
/**
 * Upload Endpoint
 * POST /uploads - Upload an image file
 */

function handleUploads($method) {
    switch ($method) {
        case 'POST':
            uploadImage();
            break;
        default:
            sendResponse(false, 'Method not allowed', null, 405);
    }
}

/**
 * Upload Image
 */
function uploadImage() {
    // Allow any authenticated user to upload (for profile photos)
    requireAuth();
    
    // Check if file was uploaded
    if (!isset($_FILES['image']) || $_FILES['image']['error'] !== UPLOAD_ERR_OK) {
        $errorMessage = 'No file uploaded';
        if (isset($_FILES['image'])) {
            switch ($_FILES['image']['error']) {
                case UPLOAD_ERR_INI_SIZE:
                case UPLOAD_ERR_FORM_SIZE:
                    $errorMessage = 'File too large';
                    break;
                case UPLOAD_ERR_NO_FILE:
                    $errorMessage = 'No file selected';
                    break;
                default:
                    $errorMessage = 'Upload failed';
            }
        }
        sendResponse(false, $errorMessage, null, 400);
    }
    
    $file = $_FILES['image'];
    
    // Validate file type
    $allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
    $finfo = finfo_open(FILEINFO_MIME_TYPE);
    $mimeType = finfo_file($finfo, $file['tmp_name']);
    finfo_close($finfo);
    
    if (!in_array($mimeType, $allowedTypes)) {
        sendResponse(false, 'Invalid file type. Only JPEG, PNG, GIF, and WebP are allowed.', null, 400);
    }
    
    // Validate file size (max 5MB)
    $maxSize = 5 * 1024 * 1024;
    if ($file['size'] > $maxSize) {
        sendResponse(false, 'File too large. Maximum size is 5MB.', null, 400);
    }
    
    // Create uploads directory if it doesn't exist
    $uploadDir = __DIR__ . '/../../uploads/';
    if (!is_dir($uploadDir)) {
        mkdir($uploadDir, 0755, true);
    }
    
    // Generate unique filename
    $extension = match($mimeType) {
        'image/jpeg' => 'jpg',
        'image/png' => 'png',
        'image/gif' => 'gif',
        'image/webp' => 'webp',
        default => 'jpg'
    };
    $filename = uniqid('img_') . '_' . time() . '.' . $extension;
    $filepath = $uploadDir . $filename;
    
    // Move uploaded file
    if (!move_uploaded_file($file['tmp_name'], $filepath)) {
        sendResponse(false, 'Failed to save file', null, 500);
    }
    
    // Build URL for the uploaded file
    $protocol = isset($_SERVER['HTTPS']) && $_SERVER['HTTPS'] === 'on' ? 'https' : 'http';
    $host = $_SERVER['HTTP_HOST'];
    
    // Determine base path
    $scriptPath = dirname($_SERVER['SCRIPT_NAME']);
    $basePath = str_replace('/api', '', $scriptPath);
    
    $imageUrl = $protocol . '://' . $host . $basePath . '/uploads/' . $filename;
    
    sendResponse(true, 'Image uploaded successfully', [
        'url' => $imageUrl,
        'filename' => $filename
    ], 201);
}
