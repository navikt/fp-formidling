package no.nav.foreldrepenger.melding.web.server.jetty.sikkerhet;


import java.util.Collections;

import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.security.auth.login.AppConfigurationEntry;

import no.nav.vedtak.sikkerhet.loginmodule.LoginConfigNames;
import no.nav.vedtak.sikkerhet.loginmodule.LoginContextConfiguration;

@Alternative
@Priority(1)
public class JettyLoginContextConfiguration extends LoginContextConfiguration {

    JettyLoginContextConfiguration() {
        replaceConfiguration(LoginConfigNames.SAML.name(), lagConfigNamesSaml());
        replaceConfiguration(LoginConfigNames.TASK_OIDC.name(), lagConfigNamesTaskOidc());
    }

    private AppConfigurationEntry[] lagConfigNamesSaml() {
        return new AppConfigurationEntry[]{
                new AppConfigurationEntry(
                        "no.nav.vedtak.sikkerhet.loginmodule.SamlLoginModule",
                        AppConfigurationEntry.LoginModuleControlFlag.REQUISITE,
                        Collections.emptyMap()),
                new AppConfigurationEntry(
                        "no.nav.foreldrepenger.info.web.server.sikkerhet.JettyLoginModule",
                        AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
                        Collections.singletonMap("password-stacking", "useFirstPass"))
        };
    }

    private AppConfigurationEntry[] lagConfigNamesTaskOidc() {
        return new AppConfigurationEntry[]{
                new AppConfigurationEntry(
                        "no.nav.vedtak.sikkerhet.loginmodule.OIDCLoginModule",
                        AppConfigurationEntry.LoginModuleControlFlag.REQUISITE,
                        Collections.emptyMap()),
                new AppConfigurationEntry(
                        "no.nav.foreldrepenger.info.web.server.sikkerhet.JettyLoginModule",
                        AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
                        Collections.singletonMap("password-stacking", "useFirstPass"))
        };
    }
}
