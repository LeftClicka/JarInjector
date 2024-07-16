package de.leftclicka;

/**
 * A simple exception handler that can be passed to the injector routine.
 * Since that routine can throw so many things it's easier if it's up to YOU to handle them.
 */
public interface ExceptionHandler {

    /**
     * Default implementation that wraps the thrown exception in a runtime exception and rethrows it.
     */
    ExceptionHandler WRAP_AND_THROW = e -> {
        if (e instanceof RuntimeException)
            throw (RuntimeException)e;
        throw new RuntimeException(e);
    };

    void handle(Exception e);

}
