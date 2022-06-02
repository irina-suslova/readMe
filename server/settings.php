<?php
include "config.php";
$con = mysqli_connect(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);
mysqli_query($con, "set names utf8");
if(!$con) {
    die('Connection Failed'.mysql_error());
}
if (isset($_GET['change_name'])) {
    $name = strval($_GET['name']);
    $user_id = strval($_GET['user_id']);
    $sql = "SELECT * FROM `users` WHERE user_id=" . $user_id . " ORDER BY ID DESC LIMIT 1";
    $result = mysqli_query($con, $sql);
    $row = mysqli_fetch_array($result);
    $json;
    if (strcasecmp($row['name'], $name) == 0) {
        $json = (object) [
            'duplicate' => true,
        ];
    }
    else {
        $sql_insert = "INSERT INTO `users` (`ID`, `user_id`, `nick`, `name`, `birthday`, `email`, `gender`, `password`) VALUES (NULL, '" . strval($user_id) . "', '" . strval($row['nick']) . "', '" . strval($name) . "', '" . strval($row['birthday']) . "', '" . strval($row['email']) . "', '" . strval($row['gender']) . "', '" . strval($row['password']) . "')";
        $result_insert = mysqli_query($con, $sql_insert);
        $json = (object) [
            'duplicate' => false,
        ];
    }
    echo json_encode($json);
}
if (isset($_GET['change_email'])) {
    $email = strval($_GET['email']);
    $user_id = strval($_GET['user_id']);
    $sql_check = "SELECT * FROM `users` WHERE email='" . strval($email) . "'";
    $result = mysqli_query($con, $sql_check);
    $checker = false;
    $json;
    if (mysqli_num_rows($result) != '') {
        while ((!$checker) && ($row=mysqli_fetch_array($result))) {
            $sql_check_last = "SELECT * FROM `users` WHERE user_id='" . strval($row['user_id']) . "' ORDER BY ID DESC LIMIT 1";
            $result_last = mysqli_query($con, $sql_check_last);
            if (mysqli_num_rows($result_last) != 0) {
                $checker = true;
            }
        }
        $json = (object) [
            'duplicate' => $checker,
        ];
    }
    else if (!$checker) {
        $sql = "SELECT * FROM `users` WHERE user_id=" . $user_id . " ORDER BY ID DESC LIMIT 1";
        $result = mysqli_query($con, $sql);
        $row = mysqli_fetch_array($result);
        $sql_insert = "INSERT INTO `users` (`ID`, `user_id`, `nick`, `name`, `birthday`, `email`, `gender`, `password`) VALUES (NULL, '" . strval($user_id) . "', '" . strval($row['nick']) . "', '" . strval($row['name']) . "', '" . strval($row['birthday']) . "', '" . strval($email) . "', '" . strval($row['gender']) . "', '" . strval($row['password']) . "')";
        $result_insert = mysqli_query($con, $sql_insert);
        $json = (object) [
            'duplicate' => false,
        ];
    }
    echo json_encode($json);
}
if (isset($_GET['change_password'])) {
    $password = strval($_GET['password']);
    $user_id = strval($_GET['user_id']);
    $sql = "SELECT * FROM `users` WHERE user_id=" . $user_id . " ORDER BY ID DESC LIMIT 1";
    $result = mysqli_query($con, $sql);
    $row = mysqli_fetch_array($result);
    $json;

    $sql_insert = "INSERT INTO `users` (`ID`, `user_id`, `nick`, `name`, `birthday`, `email`, `gender`, `password`) VALUES (NULL, '" . strval($user_id) . "', '" . strval($row['nick']) . "', '" . strval($row['name']) . "', '" . strval($row['birthday']) . "', '" . strval($row['email']) . "', '" . strval($row['gender']) . "', '" . strval($password) . "')";
    $result_insert = mysqli_query($con, $sql_insert);
    if ($result_insert) {
        $json = (object) [
            'success' => true,
        ];
    }
    else {
        $json = (object) [
            'success' => false,
        ];
    }
    echo json_encode($json);
}
?>