<?php
$servername = "localhost";
$username = "root";
$password = "";
$conn = new mysqli($servername, $username, $password);
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 
$sql = "CREATE DATABASE IF NOT EXISTS hotel_db";
if (!$conn->query($sql) === TRUE) {
    echo "Erro ao criar banco de dados: " . $conn->error;
}
$sql = "CREATE TABLE IF NOT EXISTS hotel_db.Devices (
        user VARCHAR(255),
        registration_id VARCHAR(200) PRIMARY KEY
        )";
$action = $_POST['action'];
$user = $_POST['user'];

if ($conn->query($sql) === FALSE) {
    echo "Erro ao criar tabela: " . $conn->error;
}
if ($action == "register") {
    $registrationId = $_POST['regId'];
	$stmt = $conn->prepare(
		"INSERT INTO hotel_db.Devices (user, registration_id) VALUES (?, ?)");
    $json = json_decode(file_get_contents('php://input'));
    $stmt->bind_param("ss", $user, $registrationId);
    $stmt->execute();
    $stmt->close();   
} else if ($action == "push") {
    $apiKey = "SUA_API_KEY";
    $message = $_POST["message"];
    $url = "https://fcm.googleapis.com/fcm/send";
    $sql = "SELECT registration_id FROM hotel_db.Devices WHERE user = '". $user ."'";
    $result = $conn->query($sql);
    if ($result && $result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            $regId = $row["registration_id"]; 
            $ch = curl_init($url);
            $json_data = '{ "to": "'. $regId .'", "data": { "message": "'. $message .'"} }';
            curl_setopt($ch, CURLOPT_POST, 1);
            curl_setopt($ch, CURLOPT_POSTFIELDS, $json_data);
            curl_setopt($ch, CURLOPT_HTTPHEADER, 
                array("Content-Type: application/json", 
                      "Authorization: key=". $apiKey)); 
            $response = curl_exec($ch);
            echo $response;  
        }
    }
}
$conn->close();
?>
