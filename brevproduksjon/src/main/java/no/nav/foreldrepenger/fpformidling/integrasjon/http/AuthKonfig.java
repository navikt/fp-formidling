package no.nav.foreldrepenger.fpformidling.integrasjon.http;

import java.util.Optional;

public interface AuthKonfig {

    Optional<String> getAuthorization();

    Optional<String> getConsumerId();

}
