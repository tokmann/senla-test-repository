-- Создание таблицы room
CREATE TABLE IF NOT EXISTS room (
                                    id BIGSERIAL PRIMARY KEY,
                                    number INTEGER NOT NULL UNIQUE,
                                    capacity INTEGER NOT NULL,
                                    price DOUBLE PRECISION NOT NULL,
                                    stars INTEGER NOT NULL,
                                    is_occupied BOOLEAN NOT NULL DEFAULT FALSE,
                                    under_maintenance BOOLEAN NOT NULL DEFAULT FALSE,
                                    status_change_enabled BOOLEAN NOT NULL DEFAULT TRUE,
                                    history_size INTEGER NOT NULL DEFAULT 5,
                                    check_in_date DATE,
                                    check_out_date DATE
);

-- Создание таблицы guest
CREATE TABLE IF NOT EXISTS guest (
                                     id BIGSERIAL PRIMARY KEY,
                                     age INTEGER NOT NULL,
                                     first_name VARCHAR(255) NOT NULL,
                                     second_name VARCHAR(255) NOT NULL,
                                     room_id BIGINT REFERENCES room(id) ON DELETE SET NULL
);

-- Создание таблицы service
CREATE TABLE IF NOT EXISTS service (
                                       id BIGSERIAL PRIMARY KEY,
                                       name VARCHAR(255) NOT NULL,
                                       description TEXT,
                                       price DOUBLE PRECISION NOT NULL,
                                       date DATE NOT NULL
);

-- Создание таблицы guest_service
CREATE TABLE IF NOT EXISTS guest_service (
                                             guest_id BIGINT NOT NULL REFERENCES guest(id) ON DELETE CASCADE,
                                             service_id BIGINT NOT NULL REFERENCES service(id) ON DELETE CASCADE,
                                             PRIMARY KEY (guest_id, service_id)
);

-- Создание таблицы stay_history
CREATE TABLE IF NOT EXISTS stay_history (
                                            id BIGSERIAL PRIMARY KEY,
                                            room_id BIGINT NOT NULL REFERENCES room(id) ON DELETE CASCADE,
                                            history_entry TEXT NOT NULL
);