package gov.nysenate.ess.core.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.util.HashMap;
import java.util.Map;

public class ConfigView implements ViewObject {

    private Map<String, String> config = new HashMap<>();

    public ConfigView() {
    }

    /**
     * Adds a config field to this view
     *
     * @param name
     * @param value
     */
    public void put(String name, String value) {
        config.put(name, value);
    }

    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }

    @Override
    public String getViewType() {
        return "config-view";
    }
}
