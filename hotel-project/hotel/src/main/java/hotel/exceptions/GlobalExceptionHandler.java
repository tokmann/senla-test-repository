package hotel.exceptions;

import hotel.exceptions.dao.DaoException;
import hotel.exceptions.db.DatabaseConfigurationException;
import hotel.exceptions.db.DatabaseConnectionException;
import hotel.exceptions.db.TransactionException;
import hotel.exceptions.guests.GuestAlreadyCheckedInException;
import hotel.exceptions.guests.GuestException;
import hotel.exceptions.guests.GuestNotCheckedInException;
import hotel.exceptions.guests.GuestNotFoundException;
import hotel.exceptions.rooms.RoomAlreadyExistsException;
import hotel.exceptions.rooms.RoomCapacityExceededException;
import hotel.exceptions.rooms.RoomException;
import hotel.exceptions.rooms.RoomNotFoundException;
import hotel.exceptions.rooms.RoomOccupiedException;
import hotel.exceptions.rooms.RoomUnderMaintenanceException;
import hotel.exceptions.services.ServiceAlreadyExistsException;
import hotel.exceptions.services.ServiceException;
import hotel.exceptions.services.ServiceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({
            GuestNotFoundException.class,
            RoomNotFoundException.class,
            ServiceNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundException(RuntimeException ex) {
        log.warn("Ресурс не найден: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            RoomAlreadyExistsException.class,
            GuestAlreadyCheckedInException.class,
            ServiceAlreadyExistsException.class
    })
    public ResponseEntity<ErrorResponse> handleConflictException(RuntimeException ex) {
        log.warn("Конфликт: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({
            RoomCapacityExceededException.class,
            RoomOccupiedException.class,
            RoomUnderMaintenanceException.class,
            GuestNotCheckedInException.class,
            ValidationException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequestException(RuntimeException ex) {
        log.warn("Неудачный запрос: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            DaoException.class,
            TransactionException.class,
            DatabaseConnectionException.class,
            DatabaseConfigurationException.class,
            RoomException.class,
            GuestException.class,
            ServiceException.class
    })
    public ResponseEntity<ErrorResponse> handleServerException(RuntimeException ex) {
        log.error("Ошибка сервера: {}", ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Внутренняя ошибка сервера",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        log.error("Непредвиденная ошибка: {}", ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Непредвиденная ошибка" + ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
