<?php
include "config.php";
$con = mysqli_connect(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);
mysqli_query($con, "set names utf8");
if(!$con) {
    die('Connection Failed'.mysql_error());
}
if (isset($_GET['get_text_by_id'])) {
    $id = intval($_GET['get_text_by_id']);
    $sql = 'SELECT ID, name, source, text FROM `texts` WHERE ID=' . strval($id);
    $result = mysqli_query($con, $sql);
    $json;
    while ($row = mysqli_fetch_array($result)) {
        $json = (object) [
            'id' => $row['ID'],
            'name' => $row['name'],
            'source' => $row['source'],
            'text' => $row['text']
        ];
    }
    $jsonObject = (object) [
        'textObject' => $json
    ];
    $jsonString = json_encode($jsonObject);
    echo $jsonString;
}
if (isset($_GET['get_text'])) {
    $user_id = intval($_GET['user_id']);
    $sql_actions = 'SELECT ID, user_id, text_id, time FROM `actions` WHERE user_id=' . strval($user_id) . ' AND time=(SELECT MAX(time) FROM `actions` WHERE user_id=' . strval($user_id) . ') ORDER BY ID DESC LIMIT 1';
    $result = mysqli_query($con, $sql_actions);
    $row = mysqli_fetch_array($result);
    $sql_text = 'SELECT ID, name, source, text FROM `texts` WHERE ID=' . strval(intval(1 + $row['text_id']));
    $result = mysqli_query($con, $sql_text);
    $json;
    while ($row = mysqli_fetch_array($result)) {
        $json = (object) [
            'id' => $row['ID'],
            'name' => $row['name'],
            'source' => $row['source'],
            'text' => $row['text']
        ];
    }
    $jsonObject = (object) [
        'textObject' => $json
    ];
    $jsonString = json_encode($jsonObject);
    echo $jsonString;
}
?>