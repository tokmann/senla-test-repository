package task_11.db.interfaces;

import task_11.model.Service;

import java.util.Optional;

public interface ServiceRepository extends BaseRepository<Service> {
    Optional<Service> findByName(String name);
}