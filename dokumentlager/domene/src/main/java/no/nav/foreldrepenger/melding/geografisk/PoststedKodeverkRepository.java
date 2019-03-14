package no.nav.foreldrepenger.melding.geografisk;

import java.util.Optional;

import no.nav.foreldrepenger.melding.aktør.AdresseType;

public interface PoststedKodeverkRepository {

    Optional<Poststed> finnPoststed(String postnummer);

    Optional<AdresseType> finnAdresseType(String kode);
}
