<?php
/**
 * Event Endpoints
 * GET /events - List all events
 * GET /events?status=X - Filter by status
 * GET /events/{id} - Get event by ID
 * GET /events/{id}/participants - Get event participants (Admin)
 * POST /events - Create event (Admin)
 * PUT /events/{id} - Update event (Admin)
 * DELETE /events/{id} - Delete event (Admin)
 * POST /events/{id}/register - Register for event
 * DELETE /events/{id}/unregister - Unregister from event
 */

function handleEvents($method, $id, $action) {
    // Handle registration actions
    if ($action === 'register') {
        if ($method === 'POST') {
            registerForEvent($id);
        } elseif ($method === 'DELETE') {
            unregisterFromEvent($id);
        } else {
            sendResponse(false, 'Method not allowed', null, 405);
        }
        return;
    }
    
    if ($action === 'unregister') {
        if ($method === 'DELETE') {
            unregisterFromEvent($id);
        } else {
            sendResponse(false, 'Method not allowed', null, 405);
        }
        return;
    }
    
    // Handle participants action (Admin only)
    if ($action === 'participants') {
        if ($method === 'GET') {
            getEventParticipants($id);
        } else {
            sendResponse(false, 'Method not allowed', null, 405);
        }
        return;
    }
    
    switch ($method) {
        case 'GET':
            if ($id) {
                getEventById($id);
            } else {
                getEvents();
            }
            break;
            
        case 'POST':
            createEvent();
            break;
            
        case 'PUT':
            if ($id) {
                updateEvent($id);
            } else {
                sendResponse(false, 'Event ID required', null, 400);
            }
            break;
            
        case 'DELETE':
            if ($id) {
                deleteEvent($id);
            } else {
                sendResponse(false, 'Event ID required', null, 400);
            }
            break;
            
        default:
            sendResponse(false, 'Method not allowed', null, 405);
    }
}

/**
 * Get All Events
 */
function getEvents() {
    $db = getDB();
    
    $status = $_GET['status'] ?? null;
    
    // Query with registration count
    $baseQuery = "
        SELECT e.*, COALESCE(r.registration_count, 0) as registration_count
        FROM events e
        LEFT JOIN (
            SELECT event_id, COUNT(*) as registration_count
            FROM registrations
            GROUP BY event_id
        ) r ON e.id = r.event_id
    ";
    
    if ($status && in_array($status, ['ongoing', 'upcoming', 'completed'])) {
        $stmt = $db->prepare($baseQuery . " WHERE e.status = ? ORDER BY e.tanggal ASC");
        $stmt->execute([$status]);
    } else {
        $stmt = $db->query($baseQuery . " ORDER BY e.tanggal ASC");
    }
    
    $events = $stmt->fetchAll();
    
    // Format response
    $formattedEvents = array_map(function($event) {
        return [
            'id' => (int) $event['id'],
            'nama_event' => $event['nama_event'],
            'lokasi' => $event['lokasi'],
            'kategori' => $event['kategori'],
            'tanggal' => $event['tanggal'],
            'status' => $event['status'],
            'banner_url' => $event['banner_url'],
            'registration_count' => (int) $event['registration_count']
        ];
    }, $events);
    
    sendResponse(true, 'Events retrieved', $formattedEvents);
}

/**
 * Get Event by ID
 */
function getEventById($id) {
    $db = getDB();
    
    $stmt = $db->prepare("
        SELECT e.*, COALESCE(r.registration_count, 0) as registration_count
        FROM events e
        LEFT JOIN (
            SELECT event_id, COUNT(*) as registration_count
            FROM registrations
            GROUP BY event_id
        ) r ON e.id = r.event_id
        WHERE e.id = ?
    ");
    $stmt->execute([$id]);
    $event = $stmt->fetch();
    
    if (!$event) {
        sendResponse(false, 'Event tidak ditemukan', null, 404);
    }
    
    $formattedEvent = [
        'id' => (int) $event['id'],
        'nama_event' => $event['nama_event'],
        'lokasi' => $event['lokasi'],
        'kategori' => $event['kategori'],
        'tanggal' => $event['tanggal'],
        'status' => $event['status'],
        'banner_url' => $event['banner_url'],
        'registration_count' => (int) $event['registration_count']
    ];
    
    sendResponse(true, 'Event found', $formattedEvent);
}

/**
 * Create Event (Admin only)
 */
function createEvent() {
    requireAdmin();
    
    $data = getRequestBody();
    
    $required = ['nama_event', 'lokasi', 'kategori', 'tanggal', 'status'];
    foreach ($required as $field) {
        if (empty($data[$field])) {
            sendResponse(false, "Field '$field' harus diisi", null, 400);
        }
    }
    
    if (!in_array($data['status'], ['ongoing', 'upcoming', 'completed'])) {
        sendResponse(false, 'Status tidak valid', null, 400);
    }
    
    $db = getDB();
    
    $stmt = $db->prepare("INSERT INTO events (nama_event, lokasi, kategori, tanggal, status, banner_url) VALUES (?, ?, ?, ?, ?, ?)");
    $stmt->execute([
        $data['nama_event'],
        $data['lokasi'],
        $data['kategori'],
        $data['tanggal'],
        $data['status'],
        $data['banner_url'] ?? null
    ]);
    
    $eventId = $db->lastInsertId();
    
    // Get created event
    $stmt = $db->prepare("SELECT * FROM events WHERE id = ?");
    $stmt->execute([$eventId]);
    $event = $stmt->fetch();
    
    $formattedEvent = [
        'id' => (int) $event['id'],
        'nama_event' => $event['nama_event'],
        'lokasi' => $event['lokasi'],
        'kategori' => $event['kategori'],
        'tanggal' => $event['tanggal'],
        'status' => $event['status'],
        'banner_url' => $event['banner_url']
    ];
    
    sendResponse(true, 'Event berhasil dibuat', $formattedEvent, 201);
}

/**
 * Update Event (Admin only)
 */
function updateEvent($id) {
    requireAdmin();
    
    $data = getRequestBody();
    
    $db = getDB();
    
    // Check if event exists
    $stmt = $db->prepare("SELECT id FROM events WHERE id = ?");
    $stmt->execute([$id]);
    if (!$stmt->fetch()) {
        sendResponse(false, 'Event tidak ditemukan', null, 404);
    }
    
    $required = ['nama_event', 'lokasi', 'kategori', 'tanggal', 'status'];
    foreach ($required as $field) {
        if (empty($data[$field])) {
            sendResponse(false, "Field '$field' harus diisi", null, 400);
        }
    }
    
    if (!in_array($data['status'], ['ongoing', 'upcoming', 'completed'])) {
        sendResponse(false, 'Status tidak valid', null, 400);
    }
    
    $stmt = $db->prepare("UPDATE events SET nama_event = ?, lokasi = ?, kategori = ?, tanggal = ?, status = ?, banner_url = ? WHERE id = ?");
    $stmt->execute([
        $data['nama_event'],
        $data['lokasi'],
        $data['kategori'],
        $data['tanggal'],
        $data['status'],
        $data['banner_url'] ?? null,
        $id
    ]);
    
    // Get updated event
    $stmt = $db->prepare("SELECT * FROM events WHERE id = ?");
    $stmt->execute([$id]);
    $event = $stmt->fetch();
    
    $formattedEvent = [
        'id' => (int) $event['id'],
        'nama_event' => $event['nama_event'],
        'lokasi' => $event['lokasi'],
        'kategori' => $event['kategori'],
        'tanggal' => $event['tanggal'],
        'status' => $event['status'],
        'banner_url' => $event['banner_url']
    ];
    
    sendResponse(true, 'Event berhasil diperbarui', $formattedEvent);
}

/**
 * Delete Event (Admin only)
 */
function deleteEvent($id) {
    requireAdmin();
    
    $db = getDB();
    
    // Check if event exists
    $stmt = $db->prepare("SELECT id FROM events WHERE id = ?");
    $stmt->execute([$id]);
    if (!$stmt->fetch()) {
        sendResponse(false, 'Event tidak ditemukan', null, 404);
    }
    
    $stmt = $db->prepare("DELETE FROM events WHERE id = ?");
    $stmt->execute([$id]);
    
    sendResponse(true, 'Event berhasil dihapus');
}

/**
 * Register for Event
 */
function registerForEvent($eventId) {
    $user = requireAuth();
    
    $db = getDB();
    
    // Check if event exists
    $stmt = $db->prepare("SELECT * FROM events WHERE id = ?");
    $stmt->execute([$eventId]);
    $event = $stmt->fetch();
    
    if (!$event) {
        sendResponse(false, 'Event tidak ditemukan', null, 404);
    }
    
    // Check if event is completed
    if ($event['status'] === 'completed') {
        sendResponse(false, 'Event sudah selesai', null, 400);
    }
    
    // Check H-7 registration deadline
    // Cannot register if event is within 7 days (status: ongoing)
    $eventDate = new DateTime($event['tanggal']);
    $today = new DateTime();
    $daysUntilEvent = $today->diff($eventDate)->days;
    $isPast = $eventDate < $today;
    
    if ($isPast) {
        sendResponse(false, 'Event sudah berlalu', null, 400);
    }
    
    if ($daysUntilEvent <= 7) {
        sendResponse(false, 'Pendaftaran sudah ditutup (H-7 sebelum event)', null, 400);
    }
    
    // Check if already registered
    $stmt = $db->prepare("SELECT id FROM registrations WHERE user_id = ? AND event_id = ?");
    $stmt->execute([$user['id'], $eventId]);
    if ($stmt->fetch()) {
        sendResponse(false, 'Anda sudah terdaftar di event ini', null, 400);
    }
    
    // Register
    $stmt = $db->prepare("INSERT INTO registrations (user_id, event_id) VALUES (?, ?)");
    $stmt->execute([$user['id'], $eventId]);
    
    $registrationId = $db->lastInsertId();
    
    // Get registration with event
    $stmt = $db->prepare("
        SELECT r.*, e.nama_event, e.lokasi, e.kategori, e.tanggal, e.status, e.banner_url
        FROM registrations r
        JOIN events e ON r.event_id = e.id
        WHERE r.id = ?
    ");
    $stmt->execute([$registrationId]);
    $registration = $stmt->fetch();
    
    $formattedRegistration = [
        'id' => (int) $registration['id'],
        'user_id' => (int) $registration['user_id'],
        'event_id' => (int) $registration['event_id'],
        'registered_at' => $registration['registered_at'],
        'event' => [
            'id' => (int) $registration['event_id'],
            'nama_event' => $registration['nama_event'],
            'lokasi' => $registration['lokasi'],
            'kategori' => $registration['kategori'],
            'tanggal' => $registration['tanggal'],
            'status' => $registration['status'],
            'banner_url' => $registration['banner_url']
        ]
    ];
    
    sendResponse(true, 'Berhasil mendaftar event', $formattedRegistration, 201);
}

/**
 * Unregister from Event
 */
function unregisterFromEvent($eventId) {
    $user = requireAuth();
    
    $db = getDB();
    
    // Check if registered
    $stmt = $db->prepare("SELECT id FROM registrations WHERE user_id = ? AND event_id = ?");
    $stmt->execute([$user['id'], $eventId]);
    if (!$stmt->fetch()) {
        sendResponse(false, 'Anda tidak terdaftar di event ini', null, 400);
    }
    
    $stmt = $db->prepare("DELETE FROM registrations WHERE user_id = ? AND event_id = ?");
    $stmt->execute([$user['id'], $eventId]);
    
    sendResponse(true, 'Berhasil membatalkan pendaftaran');
}

/**
 * Get Event Participants (Admin only)
 */
function getEventParticipants($eventId) {
    requireAdmin();
    
    $db = getDB();
    
    // Check if event exists
    $stmt = $db->prepare("SELECT * FROM events WHERE id = ?");
    $stmt->execute([$eventId]);
    $event = $stmt->fetch();
    
    if (!$event) {
        sendResponse(false, 'Event tidak ditemukan', null, 404);
    }
    
    // Get participants with user info
    $stmt = $db->prepare("
        SELECT r.id, r.registered_at, u.id as user_id, u.name, u.email, u.photo_url
        FROM registrations r
        JOIN users u ON r.user_id = u.id
        WHERE r.event_id = ?
        ORDER BY r.registered_at DESC
    ");
    $stmt->execute([$eventId]);
    $participants = $stmt->fetchAll();
    
    $formattedParticipants = array_map(function($p) {
        return [
            'id' => (int) $p['id'],
            'user_id' => (int) $p['user_id'],
            'user_name' => $p['name'],
            'user_email' => $p['email'],
            'user_photo_url' => $p['photo_url'],
            'registered_at' => $p['registered_at']
        ];
    }, $participants);
    
    $responseData = [
        'event' => [
            'id' => (int) $event['id'],
            'nama_event' => $event['nama_event'],
            'lokasi' => $event['lokasi'],
            'kategori' => $event['kategori'],
            'tanggal' => $event['tanggal'],
            'status' => $event['status'],
            'banner_url' => $event['banner_url']
        ],
        'participants' => $formattedParticipants
    ];
    
    sendResponse(true, 'Participants retrieved', $responseData);
}
