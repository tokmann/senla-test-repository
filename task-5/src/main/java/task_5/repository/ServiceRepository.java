package task_5.repository;

import task_5.model.Service;

import java.util.Optional;

public interface ServiceRepository extends BaseRepository<Service> {
    Optional<Service> findByName(String name);
}