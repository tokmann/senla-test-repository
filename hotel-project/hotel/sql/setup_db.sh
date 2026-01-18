#!/bin/bash

DB_NAME="hotel_db"
PSQL="psql -U postgres"

# Создание БД
createdb -U postgres -E UTF8 $DB_NAME || echo "БД $DB_NAME уже существует."

# Запуск DDL скриптов
$PSQL -d $DB_NAME -f create_tables.sql --set client_encoding=UTF8

# Запуск DML скриптов
$PSQL -d $DB_NAME -f insert_test_data.sql --set client_encoding=UTF8

echo "БД запущена!"