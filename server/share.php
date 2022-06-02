<?php
include "config.php";
$con = mysqli_connect(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);
mysqli_query($con, "set names utf8");
if(!$con) {
    die('Connection Failed'.mysql_error());
}
if (isset($_GET['text_id'])) {
    $id = intval($_GET['text_id']);
    $sql = 'SELECT ID, name, source, text FROM `texts` WHERE ID=' . strval($id);
    $result = mysqli_query($con, $sql);
    $row = mysqli_fetch_array($result);
    $text_name = strval($row['name']);
    $text_source = strval($row['source']);
    $text = strval($row['text']);
}
?>

<!DOCTYPE html>

<html lang="ru">

<head>
    
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Эта ссылка загружает с CDN все необходимые файлы Bootstrap -->

    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.0/css/bootstrap.min.css" integrity="sha384-9aIt2nRpC12Uk9gS9baDl411NQApFmC26EwAOH8WgZl5MYYxFfc+NcPb1dKGj7Sk" crossorigin="anonymous">

    <style type="text/css">
    .readme { 
        background-color: #46a146;
    }
    .mainbg {
        background-color: #cbcbcb;
    }
    .card {
        background-color: #ffffff;
        margin-top: 60px;
    }
    .source {
        padding-bottom: 10px;
    }
    </style>

</head>

<body class="mainbg">

    <header>
      <!-- Fixed navbar -->
        <nav class="navbar navbar-expand-md navbar-dark fixed-top readme">
            <div class="container-fluid">
                <a class="navbar-brand" href="http://iisuslova.hostingem.ru/get_text.html?text_id=1">Read Me</a>
            </div>
        </nav>
    </header>
    
    <!-- Begin page content -->
    <main class="flex-shrink-0">
        <div class="container border border-success rounded card">
            <h1 class="mt-1"><?php echo $text_name; ?></h1>
            <p class="text-muted border-bottom source"><i><?php echo $text_source; ?></i></p>
            <p class="fw-light"><?php echo $text; ?></p>
        </div>
    </main>
    
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js" integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo" crossorigin="anonymous"></script>
    
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.0/js/bootstrap.min.js" integrity="sha384-OgVRvuATP1z7JjHLkuOU7Xw704+h835Lr+6QL9UvYjZE3Ipu6Tp75j7Bh/kR0JKI" crossorigin="anonymous" ></script>

</body>

</html>