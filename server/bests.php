<?php
include "config.php";
$con = mysqli_connect(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);
mysqli_query($con, "set names utf8");
if(!$con) {
    die('Connection Failed'.mysql_error());
}
$sql = "SELECT time, action, text_id, count(user_id) as count_ID FROM `actions` WHERE (action=1) AND ( TIMESTAMPDIFF(DAY, time, NOW()) < 7 ) GROUP BY text_id ORDER BY count_ID DESC LIMIT 20";
$result = mysqli_query($con, $sql);
while (($row = mysqli_fetch_array($result))) {
    $sql_text = "SELECT * FROM `texts` WHERE ID=" . strval($row['text_id']);
    $result_text = mysqli_query($con, $sql_text);
    $row_text = mysqli_fetch_array($result_text);
    $array[] = $row_text['name'];
    $array_text[] = $row_text['text'];
    $array_id[] = $row_text['ID'];
}
$json = (object) [
    'best_texts_names' => $array,
    'best_texts' => $array_text,
    'best_texts_ids' => $array_id,
];
echo json_encode($json);
?>