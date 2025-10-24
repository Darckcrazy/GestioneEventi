package GestioneEventi.Exceptions;

import java.util.UUID;

public class NotFoundException extends RuntimeException {
    public NotFoundException(UUID id) {
        super("Record id " + id + " Not found!");
    }

    public NotFoundException(String msg) {
        super(msg);
    }
}
