package projects.maaman15.custom_config;

/**
 * A single config object.
 * It contains the label, value value and the default value.
 *
 * @param <T> the class of the value
 */
public class CustomConfigObject<T extends Number> {
    private final String label; // The labels inside the config
    private T value; // the value inside the config file (if not present it will be the same as the default value)
    private final T defaultValue; // the default value

    public CustomConfigObject(String label, T defaultValue) {
        this.label = label;
        this.value = defaultValue;
        this.defaultValue = defaultValue;
    }

    // getters and setters

    public String getLabel() {
        return label;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public T getValue() {
        return value;
    }

    public CustomConfigObject<T> setValue(T value) {
        this.value = value;
        return this;
    }
}
