package hotel.db.interfaces;

import hotel.model.Service;

public interface ServiceRepository extends BaseRepository<Service> {
    Service findByName(String name);
}