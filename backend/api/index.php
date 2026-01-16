<?php
/**
 * RunRace API - Main Router
 * Entry point for all API requests
 */

require_once __DIR__ . '/../config.php';
require_once __DIR__ . '/../jwt.php';

// Get request method and path
$method = $_SERVER['REQUEST_METHOD'];
$requestUri = $_SERVER['REQUEST_URI'];

// Parse path - remove query string and base path
$path = parse_url($requestUri, PHP_URL_PATH);

// Remove various possible base paths
$basePaths = ['/backend/api', '/api', '/public', '/public/api'];
foreach ($basePaths as $basePath) {
    if (strpos($path, $basePath) === 0) {
        $path = substr($path, strlen($basePath));
        break;
    }
}

$path = trim($path, '/');

// Route the request
$segments = $path ? explode('/', $path) : [];
$resource = $segments[0] ?? '';
$id = $segments[1] ?? null;
$action = $segments[2] ?? null;

// Include appropriate handler
switch ($resource) {
    case 'auth':
        require_once __DIR__ . '/endpoints/auth.php';
        handleAuth($method, $id);
        break;
        
    case 'events':
        require_once __DIR__ . '/endpoints/events.php';
        handleEvents($method, $id, $action);
        break;
        
    case 'registrations':
        require_once __DIR__ . '/endpoints/registrations.php';
        handleRegistrations($method, $id);
        break;
        
    case 'news':
        require_once __DIR__ . '/endpoints/news.php';
        handleNews($method, $id);
        break;
        
    case 'users':
        require_once __DIR__ . '/endpoints/users.php';
        handleUsers($method, $id);
        break;
        
    case 'uploads':
        require_once __DIR__ . '/endpoints/uploads.php';
        handleUploads($method);
        break;
        
    default:
        sendResponse(false, 'Endpoint not found', null, 404);
}
