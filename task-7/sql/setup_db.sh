#!/bin/bash

DB_NAME="hotel_db"
PSQL="psql -U postgres"

# Создание БД
createdb -U postgres $DB_NAME || echo "БД $DB_NAME уже существует."

# Запуск DDL скриптов
$PSQL -d $DB_NAME -f create_tables.sql

# Запуск DML скриптов
$PSQL -d $DB_NAME -f insert_test_data.sql

echo "БД запущена! Подключиться можно командой: psql -U postgres -d $DB_NAME"