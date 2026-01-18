package task_11.exceptions.services;

public class ServiceNotFoundException extends ServiceException {

    public ServiceNotFoundException(String serviceName) {
        super("Услуга '" + serviceName + "' не найдена");
    }

    public ServiceNotFoundException(long serviceId, Exception e) {
        super("Услуга с ID " + serviceId + " не найдена", e);
    }
}
