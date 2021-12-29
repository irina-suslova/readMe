<?php

define('DB_SERVER', "localhost"); // сервер

define('DB_DATABASE', "id17784805_database"); // база данных
define('DB_USER', "id17784805_irina"); //логин админа БД
define('DB_PASSWORD', "!YtMio!B+PA8{&7u"); // пароль админа БД

$con = mysqli_connect(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);
mysqli_query($con, "set names utf8");
?>