-- Тестовые комнаты
INSERT INTO room (number, capacity, price, stars, history_size) VALUES (101, 2, 100.0, 3, 5);
INSERT INTO room (number, capacity, price, stars, history_size) VALUES (102, 4, 200.0, 4, 5);
INSERT INTO room (number, capacity, price, stars, history_size) VALUES (103, 1, 80.0, 2, 5);

-- Тестовые гости
INSERT INTO guest (age, first_name, second_name, room_id) VALUES (30, 'Джон', 'Доу', 1);  -- Комната 101
INSERT INTO guest (age, first_name, second_name, room_id) VALUES (25, 'Джейн', 'Смит', 1);  -- Комната 101
INSERT INTO guest (age, first_name, second_name, room_id) VALUES (40, 'Алиса', 'Джонсон', 2);  -- Комната 102

-- Тестовые сервисы
INSERT INTO service (name, description, price, date, guest_id) VALUES ('Завтрак', 'Утренний перекус', 15.0, '2026-01-10', 1);
INSERT INTO service (name, description, price, date, guest_id) VALUES ('Спа', 'Сеанс релаксации', 50.0, '2026-01-11', 1);
INSERT INTO service (name, description, price, date, guest_id) VALUES ('Прачечная', 'Чистка одежды', 20.0, '2026-01-12', 2);

-- Тестовая история заселения (дял комнаты 101)
INSERT INTO stay_history (room_id, history_entry) VALUES (1, 'Гости: Джон Доу проживали с 2026-01-01 по 2026-01-05');
INSERT INTO stay_history (room_id, history_entry) VALUES (1, 'Гости: Джейн Смит проживали с 2026-01-06 по 2026-01-10');