<?php

include "config.php";

$con = mysqli_connect(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);
mysqli_query($con, "set names utf8");
if(!$con) {
    die('Connection Failed'.mysql_error());
}
if (isset($_GET['get_action'])) {
    $text_id = intval($_GET['text_id']);
    $user_id = intval($_GET['user_id']);
    $sql = 'SELECT * FROM `actions` WHERE (text_id=' . strval($text_id) . ') AND (user_id=' . strval($user_id) . ') ORDER BY ID DESC LIMIT 1';
    $result = mysqli_query($con, $sql);
    $json;
    while ($row = mysqli_fetch_array($result)) {
        $json = (object) [
            'id' => $row['ID'],
            'text_id' => $row['text_id'],
            'user_id' => $row['user_id'],
            'action' => $row['action']
        ];
    }
    $jsonObject = (object) [
        'actionObject' => $json
    ];
    $jsonString = json_encode($jsonObject);
    echo $jsonString;
}
if (isset($_GET['set_action'])) {
    $text_id = intval($_GET['text_id']);
    $user_id = intval($_GET['user_id']);
    $action = intval($_GET['action']);
    $sql = 'INSERT INTO `actions` (`ID`, `user_id`, `text_id`, `action`, `time`) VALUES (NULL,' . strval($user_id) . ', ' . strval($text_id) . ', ' . strval($action) . ',  NOW())';
    $result = mysqli_query($con, $sql);
}
?>