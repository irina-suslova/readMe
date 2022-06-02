# ID нашего пользователя узнаем из параметра командной строки
# Такой ID должен существовать в таблице users
# Соответственно запуск из командной строки должен быть таким:
# "python3 rating.py 3"
# Чтобы получить простыню данных для сравнения в pandas,
# необходимо перебрать пользователей и их оценки и создать для себя и другого пользователя одинаковые "простыни" оценок по одним и тем же текстам
# Если оценок для анализа недостаточно, pandas возвращает nan (Not A Number)

import sys
import mysql.connector
from mysql.connector import errorcode
import pandas as pd

my_user_id = sys.argv[1]
actions_tbl = 'actions'
users_tbl = 'users'
texts_tbl = 'texts'

config = {
        'host': '127.0.0.1',
        'database': 'id17784805_database',
        'user': 'id17784805_irina',
        'password': '!YtMio!B+PA8{&7u',
        }

try:
    cnx = mysql.connector.connect(**config)

except mysql.connector.Error as err:
    if err.errno == errorcode.ER_ACCESS_DENIED_ERROR:
        print("Something is wrong with your user name or password")
    elif err.errno == errorcode.ER_BAD_DB_ERROR:
        print("Database does not exist")
    else:
        print(err)
else:
    cursor = cnx.cursor()

    users_query = f'select distinct user_id from {actions_tbl} order by user_id'
    cursor.execute(users_query)
    user_ids = [entry[0] for entry in cursor]

    # text_ids_query = f'select distinct text_id from {actions_tbl} order by text_id'
    # cursor.execute(text_ids_query)
    # text_ids = [entry[0] for entry in cursor]

    my_text_ids_query = (f'select distinct text_id from '
                        f' {actions_tbl} where user_id = {my_user_id}'
                        f' order by text_id')
    cursor.execute(my_text_ids_query)
    my_text_ids = [entry[0] for entry in cursor]

    my_votes = []
    for text_id in my_text_ids:
        latest_vote_query = (f'select action from {actions_tbl}'
                            f' where user_id = {my_user_id} and '
                            f' text_id = {text_id} order by time desc limit 1')
        cursor.execute(latest_vote_query)
        result = cursor.fetchone()
        if result and result[0] != 0:
            my_votes.append(result[0])
    my_votes_df = pd.DataFrame(my_votes)
    # print(my_votes_df)

    for user_id in user_ids:
        if str(user_id) != my_user_id:
            votes = []
            for text_id in my_text_ids:
                latest_vote_query = (f'select action from {actions_tbl}'
                                    f' where user_id = {user_id} and '
                                    f' text_id = {text_id} '
                                    f'order by time desc limit 1')
                cursor.execute(latest_vote_query)
                result = cursor.fetchone()
                if result:
                    votes.append(result[0])
                # else:
                    # votes.append(-1)
            if votes:
                print('My user id:', my_user_id)
                print('My Votes:', my_votes)
                print('His user id:', user_id)
                print('His Votes:', votes)
                votes_df = pd.DataFrame(votes)
                print('His rating to me:', my_votes_df.corrwith(votes_df).array[0])
                print('+' * 40)

    cnx.close()

