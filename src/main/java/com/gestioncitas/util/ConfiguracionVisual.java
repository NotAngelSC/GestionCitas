package com.gestioncitas.util;

import com.gestioncitas.observer.ObservableProperty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Singleton que carga/guarda la configuración visual (colores, logo, nombre del negocio).
 * Usa ObservableProperty para notificar cambios a la UI en tiempo real.
 */
public class ConfiguracionVisual {
    private static ConfiguracionVisual instancia;

    // Propiedades observables
    private final ObservableProperty<String> nombreNegocioProperty = new ObservableProperty<>();
    private final ObservableProperty<String> logoPathProperty    = new ObservableProperty<>();
    private final ObservableProperty<String> colorFondoProperty   = new ObservableProperty<>();
    private final ObservableProperty<String> colorTextoProperty   = new ObservableProperty<>();
    private final ObservableProperty<String> colorBotonesProperty = new ObservableProperty<>();

    private ConfiguracionVisual() {
        // Cargar valores desde la tabla configuracion_visual (primera fila)
        try {
            Connection conn = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = conn.prepareStatement(
                "SELECT nombre_negocio, logo_path, color_fondo, color_texto, color_botones FROM configuracion_visual WHERE id_config = 1"
            );
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                nombreNegocioProperty.set(rs.getString("nombre_negocio"));
                logoPathProperty.set(rs.getString("logo_path"));
                colorFondoProperty.set(rs.getString("color_fondo"));
                colorTextoProperty.set(rs.getString("color_texto"));
                colorBotonesProperty.set(rs.getString("color_botones"));
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            // Si falla, inicializa con valores por defecto
            nombreNegocioProperty.set("Mi Negocio");
            logoPathProperty.set("");
            colorFondoProperty.set("#FFFFFF");
            colorTextoProperty.set("#000000");
            colorBotonesProperty.set("#007BFF");
        }
    }

public static ConfiguracionVisual getInstancia() {
        if (instancia == null) {
            instancia = new ConfiguracionVisual();
        }
        return instancia;
    }

    public String getNombreNegocio() {
        return nombreNegocioProperty.get();
    }
    public void setNombreNegocio(String nuevoNombre) {
        nombreNegocioProperty.set(nuevoNombre);
        persistirCambios("nombre_negocio", nuevoNombre);
    }
    public ObservableProperty<String> nombreNegocioProperty() {
        return nombreNegocioProperty;
    }

    public String getLogoPath() {
        return logoPathProperty.get();
    }
    public void setLogoPath(String nuevoLogo) {
        logoPathProperty.set(nuevoLogo);
        persistirCambios("logo_path", nuevoLogo);
    }
    public ObservableProperty<String> logoPathProperty() {
        return logoPathProperty;
    }

    public String getColorFondo() {
        return colorFondoProperty.get();
    }
    public void setColorFondo(String nuevoColor) {
        colorFondoProperty.set(nuevoColor);
        persistirCambios("color_fondo", nuevoColor);
    }
    public ObservableProperty<String> colorFondoProperty() {
        return colorFondoProperty;
    }

    public String getColorTexto() {
        return colorTextoProperty.get();
    }
    public void setColorTexto(String nuevoColor) {
        colorTextoProperty.set(nuevoColor);
        persistirCambios("color_texto", nuevoColor);
    }
    public ObservableProperty<String> colorTextoProperty() {
        return colorTextoProperty;
    }

    public String getColorBotones() {
        return colorBotonesProperty.get();
    }
    public void setColorBotones(String nuevoColor) {
        colorBotonesProperty.set(nuevoColor);
        persistirCambios("color_botones", nuevoColor);
    }
    public ObservableProperty<String> colorBotonesProperty() {
        return colorBotonesProperty;
    }

    private void persistirCambios(String columna, String valor) {
        try {
            String sql = "UPDATE configuracion_visual SET " + columna + " = ? WHERE id_config = 1";
            Connection conn = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, valor);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
