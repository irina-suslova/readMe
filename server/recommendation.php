<?php

function get_text($user_id) {
    $con = mysqli_connect(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);
    mysqli_query($con, "set names utf8");
    if(!$con) {
        die('Connection Failed'.mysql_error());
    }
    $sql = "SELECT user_id, text_id, MAX(ID) as max_ID FROM `actions` WHERE (action<>0) AND (user_id=" . strval($user_id) . ") GROUP BY text_id ORDER BY max_ID DESC LIMIT 100";
    $result = mysqli_query($con, $sql);

    $sql_users = "SELECT user_id FROM `users`";
    $result_users = mysqli_query($con, $sql_users);

    $corr = 0;
    $corr_id = -1;

    while (($row_users = mysqli_fetch_array($result_users))) {
        $corr_current = 0;
	    $user_current = intval($row_users['user_id']);
        if (intval($user_current) == intval($user_id)) {
            continue;
        }

        while (($row = mysqli_fetch_array($result))) {
            $sql_main = "SELECT * FROM `actions` WHERE ID=" . strval($row['max_ID']);
            $result_main = mysqli_query($con, $sql_main);
            $row_main = mysqli_fetch_array($result_main);

            $sql_current = "SELECT * FROM `actions` WHERE (user_id=" . strval($user_current) . ") AND (text_id=" . strval($row['text_id']) . ") ORDER BY ID DESC LIMIT 1";
            $result_current = mysqli_query($con, $sql_current);

            if ((mysqli_num_rows($result_current)) != 0) {
        		$row_current = mysqli_fetch_array($result_current);
        
        		if ((intval($row_current['action'])) == (intval($row_main['action']))) {
        		    $corr_current = $corr_current + 1;
                    
        		}
        		if (((intval($row_current['action'])) != (intval($row_main['action']))) && ((intval($row_current['action'])) != 0) ) {
        		    $corr_current = $corr_current - 1;
        		}

        	}

            if ($corr_current >= $corr) {
                $corr = $corr_current;
                $corr_id = $user_current;
    	    }
        }
    }

    if ($corr_id != -1) {
        $sql_user = "SELECT user_id, text_id, action, MAX(ID) as max_ID FROM `actions` WHERE (action<>0) AND (user_id=" . strval($corr_id) . ") GROUP BY text_id";
        $result_user = mysqli_query($con, $sql_user);

        while (($row_user = mysqli_fetch_array($result_user))) {
            $sql_curr_text_user_act = "SELECT user_id, text_id, action, ID FROM `actions` WHERE ID=" . strval($row_user['max_ID']);
            $result_curr_text_user_act = mysqli_query($con, $sql_curr_text_user_act);
            $row_sql_curr_text_user_act = mysqli_fetch_array($result_curr_text_user_act);

            if (intval($row_sql_curr_text_user_act['action']) == 1) {
                $sql_curr_text = "SELECT * FROM `actions` WHERE (user_id=" . strval($user_id) . ") AND (text_id=" . strval($row_user['text_id']) . ") ORDER BY ID DESC LIMIT 1";
                $result_curr_text = mysqli_query($con, $sql_curr_text);

                if ((mysqli_num_rows($result_curr_text)) == 0) {
                    return intval($row_user['text_id']);
                }
            }
        }
    }

    $rec = 2;

    $sql_max = "SELECT MAX(ID) as max_ID FROM `texts`";
    $result_max = mysqli_query($con, $sql_max);
    $row_max = mysqli_fetch_array($result_max);
    $max = intval($row_max['max_ID']);

    while ($rec <= $max) {
        $sql_curr_text = "SELECT * FROM `actions` WHERE (user_id=" . strval($user_id) . ") AND (text_id=" . strval($rec) . ") ORDER BY ID DESC LIMIT 1";
        $result_curr_text = mysqli_query($con, $sql_curr_text);
        if ((mysqli_num_rows($result_curr_text)) == 0) {
            return $rec;
        }
        $rec = $rec + 1;
    }

    return rand(2, $max);
}
?>