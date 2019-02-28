package no.nav.foreldrepenger.melding.sikkerhet;

import java.util.Properties;

public class PropertySetter {
    private final Properties propertiesToSet;

    PropertySetter(Properties propertiesToSet) {
        this.propertiesToSet = propertiesToSet;
    }

    final void setOn(Properties properties) {
        for (String propName : propertiesToSet.stringPropertyNames()) {
            properties.setProperty(propName, propertiesToSet.getProperty(propName));
        }
    }
}
