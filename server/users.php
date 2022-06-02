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
    $checker = false;
    if (mysqli_fetch_array($result) != '') {
        while((!$checker) && ($row=mysqli_fetch_array($result))) {
            $sql_check_last = "SELECT * FROM `users` WHERE user_id='" . strval($row['user_id']) . "' ORDER BY ID DESC LIMIT 1";
            $result_last = mysqli_query($con, $sql_check_last);
            if (mysqli_fetch_array($result_last) != '') {
                $checker = true;
            }
        }
        $json = (object) [
            'duplicate' => $checker,
        ];
    }
    else if (!$checker) {
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
        $sql_check_double = "SELECT * FROM `users` WHERE user_id='" . strval($row['user_id']) . "' ORDER BY ID DESC LIMIT 1";
        $result_check_double = mysqli_query($con, $sql_check_double);
        $row_check_double = mysqli_fetch_array($result_check_double);

        if (strcasecmp($row_check_double, $email) != 0) {
            $json = (object) [
                'user' => false,
                'success' => false
            ];
        }

        else {
            $correct_password = strval($row_check_double['password']);
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
                    'name' => $row_check_double['name'],
                    'nick' => $row_check_double['nick'],
                    'id' => $row_check_double['user_id']
                ];
            }
        }
    }
    $jsonObject = (object) [
        'userObject' => $json
    ];
    $jsonString = json_encode($jsonObject);
    echo $jsonString;
}
if (isset($_GET['get_user'])) {
    $user_id = strval($_GET['user_id']);
    $sql = "SELECT * FROM `users` WHERE user_id='" . strval($user_id) . "' ORDER BY ID DESC LIMIT 1";
    $result = mysqli_query($con, $sql);
    $row = mysqli_fetch_array($result);
    $json;
    $json = (object) [
        'user' => true,
        'success' => true,
        'name' => $row['name'],
        'nick' => $row['nick'],
        'id' => $row['user_id'],
    ];
    $jsonObject = (object) [
        'userObject' => $json
    ];
    $jsonString = json_encode($jsonObject);
    echo $jsonString;
}
?>