package task_6.exceptions.services;

public class ServiceAlreadyExistsException extends ServiceException {

    public ServiceAlreadyExistsException(String serviceName) {
        super("Услуга '" + serviceName + "' уже существует");
    }
}
