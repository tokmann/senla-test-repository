package hotel.db.interfaces;

import hotel.model.Service;

import java.util.Optional;

public interface ServiceRepository extends BaseRepository<Service> {
    Optional<Service> findByName(String name);
}