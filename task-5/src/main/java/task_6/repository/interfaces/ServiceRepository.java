package task_6.repository.interfaces;

import task_6.model.Service;

import java.util.Optional;

public interface ServiceRepository extends BaseRepository<Service> {
    Optional<Service> findByName(String name);
}