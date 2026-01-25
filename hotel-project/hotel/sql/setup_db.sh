#!/bin/bash

DB_NAME="hotel_db"
PSQL="psql -U postgres"

$PSQL -d postgres -c "SELECT pg_terminate_backend(pg_stat_activity.pid) FROM pg_stat_activity WHERE pg_stat_activity.datname = '$DB_NAME' AND pid <> pg_backend_pid();" >/dev/null 2>&1

$PSQL -d postgres -c "DROP DATABASE IF EXISTS \"$DB_NAME\";"

echo "Создание базы данных $DB_NAME"
$PSQL -d postgres -c "CREATE DATABASE \"$DB_NAME\" WITH OWNER = postgres ENCODING = 'UTF8' LC_COLLATE = 'ru_RU.UTF-8' LC_CTYPE = 'ru_RU.UTF-8' TEMPLATE = template0 CONNECTION LIMIT = -1;" >/dev/null 2>&1

if [ $? -ne 0 ]; then
    $PSQL -d postgres -c "CREATE DATABASE \"$DB_NAME\" WITH OWNER = postgres ENCODING = 'UTF8' TEMPLATE = template0;" >/dev/null 2>&1
fi

$PSQL -d $DB_NAME -f create_tables.sql --set client_encoding=UTF8
$PSQL -d $DB_NAME -f insert_test_data.sql --set client_encoding=UTF8

echo "База данных успешно создана и заполнена"