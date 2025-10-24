package GestioneEventi.Exceptions;

import lombok.Getter;

import java.util.List;

@Getter
public class ValidationException extends RuntimeException {
    private List<String> errorsMessages;

    public ValidationException(List<String> errorsMessages) {
        super(" validation error ");
        this.errorsMessages = errorsMessages;
    }
}