<?php
include "config.php";
$con = mysqli_connect(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);
mysqli_query($con, "set names utf8");
if(!$con) {
    die('Connection Failed'.mysql_error());
}
if (isset($_GET['get_likes'])) {
    $user_id = strval($_GET['user_id']);
    $sql = "SELECT * FROM `actions` WHERE (action=1) AND (user_id=" . strval($user_id) . ")  ORDER BY ID DESC LIMIT 15";
    $result = mysqli_query($con, $sql);
    while (($row = mysqli_fetch_array($result))) {
        $sql_text = "SELECT * FROM `texts` WHERE ID=" . strval($row['text_id']);
        $result_text = mysqli_query($con, $sql_text);
        $row_text = mysqli_fetch_array($result_text);
        $array[] = $row_text['name'];
    }
    $json = (object) [
        'liked_texts_names' => $array,
    ];
    echo json_encode($json);
}
?>