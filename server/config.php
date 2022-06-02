<?php

define('DB_SERVER', "server_name"); // сервер

define('DB_DATABASE', "database_name"); // база данных
define('DB_USER', "database_login"); //логин админа БД
define('DB_PASSWORD', "database_password"); // пароль админа БД

$con = mysqli_connect(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);
mysqli_query($con, "set names utf8");
?>