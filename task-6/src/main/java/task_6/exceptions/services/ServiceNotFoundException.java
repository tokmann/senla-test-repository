package task_6.exceptions.services;

public class ServiceNotFoundException extends ServiceException {

    public ServiceNotFoundException(String serviceName) {
        super("Услуга '" + serviceName + "' не найдена");
    }

    public ServiceNotFoundException(long serviceId) {
        super("Услуга с ID " + serviceId + " не найдена");
    }
}
