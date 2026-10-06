package util;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Validaciones reutilizables de entradas de formulario.
 * Cada método lanza {@link IllegalArgumentException} con un mensaje listo para mostrar al usuario.
 */
public final class Validador {
    private static final int MAX_TEXTO = 100; // coincide con VARCHAR(100) de la BD
    private static final DateTimeFormatter FECHA =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter HORA =
            DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(ResolverStyle.STRICT);

    private Validador() {}

    /** Valida un nombre de persona: obligatorio, hasta 100 caracteres, solo letras, espacios, . ' y -. */
    public static String nombre(String texto) {
        String t = texto == null ? "" : texto.trim();
        if (t.isEmpty()) throw new IllegalArgumentException("Ingrese el nombre del repartidor.");
        if (t.length() < 2) throw new IllegalArgumentException("El nombre debe tener al menos 2 caracteres.");
        if (t.length() > MAX_TEXTO) throw new IllegalArgumentException("El nombre no puede superar los " + MAX_TEXTO + " caracteres.");
        if (!t.matches("\\p{L}[\\p{L} .'-]*")) {
            throw new IllegalArgumentException("El nombre solo puede contener letras, espacios, punto, apóstrofe o guion.");
        }
        return t;
    }

    /** Valida una dirección: obligatoria, entre 5 y 100 caracteres y con al menos una letra. */
    public static String direccion(String texto) {
        String t = texto == null ? "" : texto.trim();
        if (t.isEmpty()) throw new IllegalArgumentException("Ingrese la dirección del pedido.");
        if (t.length() < 5) throw new IllegalArgumentException("La dirección debe tener al menos 5 caracteres.");
        if (t.length() > MAX_TEXTO) throw new IllegalArgumentException("La dirección no puede superar los " + MAX_TEXTO + " caracteres.");
        if (!t.matches(".*\\p{L}.*")) throw new IllegalArgumentException("La dirección debe contener letras (calle o sector).");
        return t;
    }

    /** Valida una fecha con formato AAAA-MM-DD (rechaza fechas inexistentes como 2025-02-30). */
    public static Date fecha(String texto) {
        try {
            return Date.valueOf(LocalDate.parse(texto == null ? "" : texto.trim(), FECHA));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Fecha inválida. Use el formato AAAA-MM-DD (ej: 2025-10-31).");
        }
    }

    /** Valida una hora con formato HH:mm de 24 horas. */
    public static Time hora(String texto) {
        try {
            return Time.valueOf(LocalTime.parse(texto == null ? "" : texto.trim(), HORA));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Hora inválida. Use el formato HH:MM de 24 horas (ej: 14:30).");
        }
    }
}
