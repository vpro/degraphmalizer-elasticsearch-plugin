package dgm.exceptions;

/**
 * Something is wrong while loading the {@link dgm.configuration.Configuration}.
 */
public class ConfigurationException extends DegraphmalizerException {

    public ConfigurationException(String msg) {
        super(msg);
    }

    public ConfigurationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
