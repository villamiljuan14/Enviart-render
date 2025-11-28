package Enviart.Enviart.util.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utilidad para validar contraseñas según políticas de seguridad
 */
public class PasswordValidator {

    private static final int MIN_LENGTH = 8;
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("\\d");

    /**
     * Valida una contraseña según las políticas definidas
     * 
     * @param password Contraseña a validar
     * @return Lista de errores (vacía si es válida)
     */
    public static List<String> validate(String password) {
        List<String> errors = new ArrayList<>();

        if (password == null || password.isEmpty()) {
            errors.add("La contraseña no puede estar vacía");
            return errors;
        }

        if (password.length() < MIN_LENGTH) {
            errors.add("La contraseña debe tener al menos " + MIN_LENGTH + " caracteres");
        }

        if (!UPPERCASE_PATTERN.matcher(password).find()) {
            errors.add("La contraseña debe contener al menos una letra mayúscula");
        }

        if (!LOWERCASE_PATTERN.matcher(password).find()) {
            errors.add("La contraseña debe contener al menos una letra minúscula");
        }

        if (!DIGIT_PATTERN.matcher(password).find()) {
            errors.add("La contraseña debe contener al menos un número");
        }

        return errors;
    }

    /**
     * Verifica si una contraseña es válida
     * 
     * @param password Contraseña a validar
     * @return true si es válida, false en caso contrario
     */
    public static boolean isValid(String password) {
        return validate(password).isEmpty();
    }

    /**
     * Obtiene un mensaje de error concatenado
     * 
     * @param password Contraseña a validar
     * @return Mensaje de error o null si es válida
     */
    public static String getErrorMessage(String password) {
        List<String> errors = validate(password);
        if (errors.isEmpty()) {
            return null;
        }
        return String.join(". ", errors);
    }
}
