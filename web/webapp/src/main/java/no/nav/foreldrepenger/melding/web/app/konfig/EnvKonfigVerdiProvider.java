package no.nav.foreldrepenger.melding.web.app.konfig;

import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;

import no.nav.vedtak.konfig.KonfigVerdi;
import no.nav.vedtak.konfig.PropertiesKonfigVerdiProvider;

//FIXME (u139158): PFP-1176 Bør flyttes til no.nav.vedtak.felles:felles-util siden alle applikasjonene blir NAIS-apps
@ApplicationScoped
public class EnvKonfigVerdiProvider extends PropertiesKonfigVerdiProvider {

    public EnvKonfigVerdiProvider() {
        super(getEnv());
    }

    private static Properties getEnv() {
        Properties props = new Properties();
        props.putAll(System.getenv());
        return props;
    }

    @Override
    public int getPrioritet() {
        return 20;
    }

    @Override
    public <V> V getVerdi(String key, KonfigVerdi.Converter<V> converter) {
        V verdi = super.getVerdi(key, converter);
        if (verdi == null) {
            verdi = super.getVerdi(upperKey(key), converter);
        }
        if (verdi == null) {
            verdi = super.getVerdi(endpointUrlKey(key), converter);
        }
        return verdi;
    }

    @Override
    public boolean harVerdi(String key) {
        return super.harVerdi(key) || super.harVerdi(upperKey(key)) || super.harVerdi(endpointUrlKey(key));
    }

    private String endpointUrlKey(String key) {
        // hack diff mellom NAIS og SKYA env for endepunkter
        return key == null ? null : upperKey(key).replaceAll("_URL$", "_ENDPOINTURL");
    }

    private String upperKey(String key) {
        // hack diff mellom NAIS og SKYA env (upper vs. lower case og '_' istdf. '.')
        return key == null ? null : key.toUpperCase().replace('.', '_');
    }
}
