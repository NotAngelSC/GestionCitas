package com.gestioncitas.observer;

import javafx.scene.layout.Region;

/**
 * ColorChangeListener implementa un listener que, al recibir un nuevo color (en formato HEX),
 * aplica ese color de fondo a un Region de JavaFX.
 *
 * Puedes usarlo para escuchar cambios en la propiedad color y automáticamente
 * cambiar el estilo CSS de un contenedor.
 */
public class ColorChangeListener implements java.util.function.Consumer<String> {

    private final Region targetRegion;

    /**
     * @param targetRegion El contenedor (Pane, VBox, BorderPane, etc.) cuyo fondo cambiamos.
     */
    public ColorChangeListener(Region targetRegion) {
        this.targetRegion = targetRegion;
    }

    @Override
    public void accept(String nuevoColorHex) {
        // Se asume que nuevoColorHex es algo como "#FF0000"
        targetRegion.setStyle("-fx-background-color: " + nuevoColorHex + ";");
    }
}
