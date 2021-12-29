<?php
include "config.php";
$con = mysqli_connect(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);
mysqli_query($con, "set names utf8");
if(!$con) {
    die('Connection Failed'.mysql_error());
}
if (isset($_GET['register'])) {
    $name = strval($_GET['name']);
    $email = strval($_GET['email']);
    $birthday = strval($_GET['birthday']);
    $gender = strval($_GET['gender']);
    $password = strval($_GET['password']);
    $sql_check = "SELECT * FROM `users` WHERE email='" . strval($email) . "'";
    $result = mysqli_query($con, $sql_check);
    if (mysqli_fetch_array($result) != '') {
        $json = (object) [
            'duplicate' => true,
        ];
    }
    else {
        $sql_last = 'SELECT MAX(user_id) AS max_user_id FROM `users`';
        $result = mysqli_query($con, $sql_last);
        $id = 0;
        if ($result != false) {
            $row = mysqli_fetch_array($result);
            $id = intval($row['max_user_id']) + 1;
        }
        $nick = 'User' . strval($id);
        $sql = "INSERT INTO `users` (`ID`, `user_id`, `nick`, `name`, `birthday`, `email`, `gender`, `password`) VALUES (NULL, '" . strval($id) . "', '" . strval($nick) . "', '" . strval($name) . "', '" . strval($birthday) . "', '" . strval($email) . "', '" . strval($gender) . "', '" . strval($password) . "')";
        $result = mysqli_query($con, $sql);
        
        $json = (object) [
            'duplicate' => false,
            'id' => $id,
            'name' => $name,
            'nick' => $nick
        ];
    }
    $jsonObject = (object) [
        'userObject' => $json
    ];
    $jsonString = json_encode($jsonObject);
    echo $jsonString;
}
if (isset($_GET['sign_in'])) {
    $email = strval($_GET['email']);
    $password = strval($_GET['password']);
    $sql = "SELECT * FROM `users` WHERE email='" . strval($email) . "' ORDER BY ID DESC LIMIT 1";
    $result = mysqli_query($con, $sql);
    $row = mysqli_fetch_array($result);
    $json;
    if ($row == '') {
        $json = (object) [
            'user' => false,
            'success' => false
        ];
    }
    else {
        $correct_password = strval($row['password']);
        if ($correct_password != $password) {
            $json = (object) [
                'user' => true,
                'success' => false
            ];
        }
        else {
            $json = (object) [
                'user' => true,
                'success' => true,
                'name' => $row['name'],
                'nick' => $row['nick'],
                'id' => $row['user_id']
            ];
        }
    }
    $jsonObject = (object) [
        'userObject' => $json
    ];
    $jsonString = json_encode($jsonObject);
    echo $jsonString;
}
?>