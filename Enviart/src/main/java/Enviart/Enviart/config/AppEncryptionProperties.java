package Enviart.Enviart.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.encryption")
public class AppEncryptionProperties {

    /**
     * Clave de cifrado AES (32 caracteres para AES-256)
     */
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
