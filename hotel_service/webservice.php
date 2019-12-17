<?php
$servername = "127.0.0.1";
$username = "root";
$password = "";
$conn = new mysqli($servername, $username, $password);
if ($conn->connect_error) {
    die("Erro ao conectar com banco de dados: " . $conn->connect_error);
} 
$sql = "CREATE DATABASE IF NOT EXISTS hotel_db";
if (!$conn->query($sql) === TRUE) {
    echo "Erro ao criar banco de dados: " . $conn->error;
}
$sql = "CREATE TABLE IF NOT EXISTS hotel_db.Hotel (
id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY, 
user VARCHAR(200),
name VARCHAR(500) NOT NULL,
address VARCHAR(500) NOT NULL,
rating FLOAT(15,12),
photo_url VARCHAR(1000)
)";
if ($conn->query($sql) === FALSE) {
    echo "Erro ao criar tabela: " . $conn->error;
}
$httpMethod = $_SERVER['REQUEST_METHOD'];
if ($httpMethod == 'POST') {
    $stmt = $conn->prepare(
        "INSERT INTO hotel_db.Hotel (user, name, address, rating, photo_url) VALUES (?, ?, ?, ?, ?)");
    $segments = explode("/", parse_url($_SERVER["REQUEST_URI"], PHP_URL_PATH));
    $json = json_decode(file_get_contents('php://input'));
    $user = $segments[count($segments)-1];
    $name       = $json->{'name'};
    $address    = $json->{'address'};
    $rating     = $json->{'rating'};
    $photourl   = $json->{'photoUrl'};
    $stmt->bind_param("sssds", $user, $name, $address, $rating, $photourl);
    $stmt->execute();
    $stmt->close();
    $id = $conn->insert_id;
    $jsonResponse = array("id"=>(int)$id);
    echo json_encode($jsonResponse);
} else if ($httpMethod == 'GET') {
    $jsonArray = array();
    $sql = "SELECT id, user, name, address, rating, photo_url FROM hotel_db.Hotel ";
    $singleLine = false;
    $segments = explode("/", parse_url($_SERVER["REQUEST_URI"], PHP_URL_PATH));
    $id = $segments[count($segments)-1];
    $user = $segments[count($segments)-2];
    if (is_numeric($user)){        
        $singleLine = true;
        $sql = $sql ." WHERE user = '". $user ."' AND id = ". $id;
    } else {
        $user = $id;
        $sql = $sql ." WHERE user = '". $user ."'";
    }
    $result = $conn->query($sql);
    if ($result && $result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            $jsonLinha = array(
                "id"         => $row["id"],
                "user"       => $user,
                "name"       => $row["name"],
                "address"    => $row["address"],
                "rating"     => (float)$row["rating"],
                "photoUrl"   => $row["photo_url"]);
            $jsonArray[] = $jsonLinha;  
            if ($singleLine){
                echo json_encode($jsonLinha);
                break;
            }       
        }
    }
    if (!$singleLine) {
        echo json_encode($jsonArray);
    }
} else if ($httpMethod == 'PUT') {
    $stmt = $conn->prepare(
        "UPDATE hotel_db.Hotel SET user=?, name=?, address=?, rating=?, photo_url=? WHERE id=?");
    $json  = json_decode(file_get_contents('php://input'));
    $segments = explode("/", $_SERVER["REQUEST_URI"]);
    $id = $segments[count($segments)-1];
    $user = $segments[count($segments)-2];
    $name = $json->{'name'};
    $address = $json->{'address'};
    $rating = $json->{'rating'};
    $photourl = $json->{'photoUrl'};
    $stmt->bind_param("sssdsi", $user, $name, $address, $rating, $photourl, $id);
    $stmt->execute();
    $stmt->close();
    $jsonResponse = array("id"=>(int)$id);
    $image_path =  "upload/" . $id ."___*";
    if ($photourl == NULL || $photourl == ""){
        $files = glob($image_path); //get all file names
        foreach($files as $file){
            if(is_file($file))
                unlink($file); //delete file
        }
    }
    echo json_encode($jsonResponse);
} else if ($httpMethod == 'DELETE') {
    $stmt = $conn->prepare("DELETE FROM hotel_db.Hotel WHERE id=? AND user=?");
    $segments = explode("/", $_SERVER["REQUEST_URI"]);
    $id = $segments[count($segments)-1];
    $user = $segments[count($segments)-2];
    $stmt->bind_param("is", $id, $user);
    $stmt->execute();
    $stmt->close();
    $jsonResponse = array("id"=>(int)$id);
    $image_path =  "upload/" . $id ."___*";
    
    $files = glob($image_path); //get all file names
    foreach($files as $file){
        if(is_file($file))
            unlink($file); //delete file
    }
    echo json_encode($jsonResponse);
}
$conn->close();
?>
