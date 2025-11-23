package task_7.exceptions.services;

public class ServiceAlreadyExistsException extends ServiceException {

    public ServiceAlreadyExistsException(String serviceName) {
        super("Услуга '" + serviceName + "' уже существует");
    }
}
