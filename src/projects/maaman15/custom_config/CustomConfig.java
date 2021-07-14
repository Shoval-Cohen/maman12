package projects.maaman15.custom_config;

import sinalgo.configuration.Configuration;
import sinalgo.configuration.CorruptConfigurationEntryException;

/**
 * This is a singleton class that reads and stores all of the {@link CustomConfigObject} that this projects has.
 */
public class CustomConfig {

    private CustomConfigObject<Double> maxUDGRadius;  // The unit disk geometric radius parameter
    private CustomConfigObject<Integer> minimumNodes; // The minimum number of nodes parameter

    private static final CustomConfig instance = new CustomConfig(); // single instance

    private CustomConfig() {
        initValuesFromConfig();
    }

    /**
     * Init parameters values by reading the custom parameters from the config file.
     * If some exception is raised while reading the different parameters values, the default value will be set and an error message will be printed.
     */
    private void initValuesFromConfig() {
        maxUDGRadius = new CustomConfigObject<>(CustomConfigConst.UDG_RADIUS_SIZE_LABEL, CustomConfigConst.DEFAULT_UDG_CONNECTION_DISTANCE);
        minimumNodes = new CustomConfigObject<>(CustomConfigConst.MINIMUM_NODES_LABEL, CustomConfigConst.DEFAULT_MINIMUM_NODES);

        readAndSetDoubleConfig(maxUDGRadius);
        readAndSetIntegerConfig(minimumNodes);
    }

    public void readAndSetDoubleConfig(CustomConfigObject<Double> customConfigObject) {
        double tempDoubleValue = customConfigObject.getDefaultValue();
        try {
            tempDoubleValue = Configuration.getDoubleParameter(customConfigObject.getLabel());
        } catch (CorruptConfigurationEntryException e) {
            printErrorMessage(customConfigObject);
        }
        customConfigObject.setValue(tempDoubleValue);
    }

    public void readAndSetIntegerConfig(CustomConfigObject<Integer> customConfigObject) {
        int tempIntValue = customConfigObject.getDefaultValue();
        try {
            tempIntValue = Configuration.getIntegerParameter(customConfigObject.getLabel());
        } catch (CorruptConfigurationEntryException e) {
            printErrorMessage(customConfigObject);
        }
        customConfigObject.setValue(tempIntValue);
    }

    private <T extends Number> void printErrorMessage(CustomConfigObject<T> myCustomConfigObject) {
        System.err.println("Couldn't find the label: " + myCustomConfigObject.getLabel()
                + ", setting to default value = " + myCustomConfigObject.getDefaultValue());
    }

    // getters

    public static CustomConfig getInstance() {
        return instance;
    }

    public double getMaxUDGRadius() {
        return maxUDGRadius.getValue();
    }

    public int getMinimumNodes() {
        return minimumNodes.getValue();
    }
}
