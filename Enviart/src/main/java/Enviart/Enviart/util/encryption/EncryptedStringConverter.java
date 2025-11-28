package Enviart.Enviart.util.encryption;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Converter JPA para cifrar/descifrar automáticamente campos String
 * Uso: @Convert(converter = EncryptedStringConverter.class)
 */
@Converter
@Component
public class EncryptedStringConverter implements AttributeConverter<String, String> {

    private static AESEncryptor encryptor;

    @Autowired
    public void setEncryptor(AESEncryptor aesEncryptor) {
        EncryptedStringConverter.encryptor = aesEncryptor;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (encryptor == null) {
            throw new IllegalStateException("AESEncryptor no está inicializado");
        }
        return encryptor.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (encryptor == null) {
            throw new IllegalStateException("AESEncryptor no está inicializado");
        }

        // Si el dato está vacío o nulo, devolverlo tal cual
        if (dbData == null || dbData.isEmpty()) {
            return dbData;
        }

        // Intentar descifrar, si falla asumir que es dato sin cifrar (migración)
        try {
            return encryptor.decrypt(dbData);
        } catch (Exception e) {
            // Si falla el descifrado, probablemente sea un dato antiguo sin cifrar
            // Devolver el valor original
            return dbData;
        }
    }
}
