package com.gestioncitas.observer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * ObservableProperty<T> permite registrar listeners que reaccionan
 * cuando cambia el valor de la propiedad.
 */
public class ObservableProperty<T> {
    private T value;
    private final List<Consumer<T>> listeners = new ArrayList<>();

    public ObservableProperty() { }

    public ObservableProperty(T initialValue) {
        this.value = initialValue;
    }

    public T get() {
        return value;
    }

    public void set(T newValue) {
        this.value = newValue;
        notifyListeners(newValue);
    }

    /**
     * Registra un listener que se ejecutará cada vez que el valor cambie.
     * El Consumer recibe el nuevo valor.
     */
    public void addListener(Consumer<T> listener) {
        listeners.add(listener);
    }

    private void notifyListeners(T newValue) {
        for (Consumer<T> listener : listeners) {
            listener.accept(newValue);
        }
    }
}
