

CREATE TABLE IF NOT EXISTS rooms (
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

CREATE TABLE IF NOT EXISTS guests (
                                      id BIGSERIAL PRIMARY KEY,
                                      age INTEGER NOT NULL,
                                      first_name VARCHAR(50) NOT NULL,
                                      second_name VARCHAR(50) NOT NULL,
                                      room_id BIGINT REFERENCES rooms(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS services (
                                        id BIGSERIAL PRIMARY KEY,
                                        name VARCHAR(100) NOT NULL UNIQUE,
                                        description VARCHAR(500),
                                        price DOUBLE PRECISION NOT NULL,
                                        date DATE
);

CREATE TABLE IF NOT EXISTS guest_services (
                                              guest_id BIGINT NOT NULL REFERENCES guests(id) ON DELETE CASCADE,
                                              service_id BIGINT NOT NULL REFERENCES services(id) ON DELETE CASCADE,
                                              PRIMARY KEY (guest_id, service_id)
);

CREATE TABLE IF NOT EXISTS stay_history (
                                            id BIGSERIAL PRIMARY KEY,
                                            room_id BIGINT NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
                                            history_entry VARCHAR(500) NOT NULL,
                                            entry_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_guests_room_id ON guests(room_id);
CREATE INDEX IF NOT EXISTS idx_stay_history_room_id ON stay_history(room_id);
CREATE INDEX IF NOT EXISTS idx_guest_services_guest_id ON guest_services(guest_id);
CREATE INDEX IF NOT EXISTS idx_guest_services_service_id ON guest_services(service_id);