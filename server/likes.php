<?php
include "config.php";
$con = mysqli_connect(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);
mysqli_query($con, "set names utf8");
if(!$con) {
    die('Connection Failed'.mysql_error());
}
if (isset($_GET['get_likes'])) {
    $user_id = strval($_GET['user_id']);
    $sql = "SELECT user_id, text_id, MAX(ID) as max_ID FROM `actions` WHERE (user_id=" . strval($user_id) . ") GROUP BY text_id ORDER BY max_ID DESC";
    $result = mysqli_query($con, $sql);
    $count = 0;
    while (($row = mysqli_fetch_array($result)) && ($count <= 20)) {
        $sql_action = "SELECT * FROM `actions` WHERE (action=1) AND (ID=" . strval($row['max_ID']) . ")";
        $result_action = mysqli_query($con, $sql_action);
        if ((mysqli_num_rows($result_action)) != 0) {
            $count = $count + 1;
            $row_action = mysqli_fetch_array($result_action);
            $sql_text = "SELECT * FROM `texts` WHERE ID=" . strval($row_action['text_id']);
            $result_text = mysqli_query($con, $sql_text);
            $row_text = mysqli_fetch_array($result_text);
            $array[] = $row_text['name'];
            $array_text[] = $row_text['text'];
            $array_id[] = $row_text['ID'];
        }
    }
    $json = (object) [
        'liked_texts_names' => $array,
        'liked_texts' => $array_text,
        'liked_texts_ids' => $array_id,
    ];
    echo json_encode($json);
}
?>