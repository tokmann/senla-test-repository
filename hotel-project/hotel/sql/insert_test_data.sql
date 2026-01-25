INSERT INTO rooms (number, capacity, price, stars, history_size, is_occupied, under_maintenance, status_change_enabled, check_in_date, check_out_date)
VALUES
    (101, 2, 100.0, 3, 5, TRUE, FALSE, TRUE, '2026-01-10', '2026-01-15'),
    (102, 4, 200.0, 4, 5, TRUE, FALSE, TRUE, '2026-01-08', '2026-01-18'),
    (103, 1, 80.0, 2, 5, FALSE, FALSE, TRUE, NULL, NULL),
    (104, 3, 150.0, 3, 5, FALSE, TRUE, TRUE, NULL, NULL),
    (105, 2, 120.0, 4, 10, FALSE, FALSE, FALSE, NULL, NULL);

INSERT INTO guests (age, first_name, second_name, room_id)
VALUES
    (30, 'Ivan', 'Ivanov', 1),
    (25, 'Mariya', 'Petrova', 1),
    (40, 'Sergey', 'Sidorov', 2),
    (35, 'Anna', 'Kozlova', 2),
    (28, 'Dmitriy', 'Smirnov', NULL),
    (45, 'Elena', 'Vasileva', NULL);

INSERT INTO services (name, description, price, date)
VALUES
    ('Breakfast', 'Continental breakfast', 15.0, CURRENT_DATE),
    ('SPA', 'SPA procedures', 50.0, CURRENT_DATE),
    ('Laundry', 'Laundry service', 20.0, CURRENT_DATE),
    ('Transfer', 'Airport transfer', 30.0, CURRENT_DATE);

INSERT INTO guest_services (guest_id, service_id)
VALUES
    (1, 1), (1, 2),
    (2, 1), (2, 3),
    (3, 2), (3, 4),
    (4, 3), (4, 4),
    (5, 1), (5, 4),
    (6, 2), (6, 3);

INSERT INTO stay_history (room_id, history_entry)
VALUES
    (1, 'Guests: Ivan Ivanov, Mariya Petrova lived from 2026-01-10 to 2026-01-15'),
    (1, 'Guests: Petr Petrov, Olga Sokolova lived from 2025-12-25 to 2026-01-05'),
    (2, 'Guests: Sergey Sidorov, Anna Kozlova lived from 2026-01-08 to 2026-01-18'),
    (2, 'Guests: Mikhail Kuznetsov, Natalya Popova lived from 2025-12-20 to 2026-01-02'),
    (3, 'Guest: Maksim Novikov lived from 2025-12-30 to 2026-01-03');