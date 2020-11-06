package no.nav.foreldrepenger.melding.datamapper.brev;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.RelasjonskodeType;
import no.nav.foreldrepenger.melding.personopplysning.NavBrukerKjønn;
import no.nav.foreldrepenger.melding.personopplysning.RelasjonsRolleType;

public class AvslagEngangstønadBrevMapperTest {

    @Test
    public void skal_mappe_relasjonsrolle_far() {
        assertThat(AvslagEngangstønadBrevMapper
                .tilRelasjonskodeType(RelasjonsRolleType.REGISTRERT_PARTNER, NavBrukerKjønn.MANN))
                .isEqualTo(RelasjonskodeType.FAR);
    }


    @Test
    public void skal_mappe_relasjonsrolle_mor() {
        assertThat(AvslagEngangstønadBrevMapper
                .tilRelasjonskodeType(RelasjonsRolleType.MORA, NavBrukerKjønn.KVINNE))
                .isEqualTo(RelasjonskodeType.MOR);
    }

    @Test
    public void skal_mappe_relasjonsrolle_medmor() {
        assertThat(AvslagEngangstønadBrevMapper
                .tilRelasjonskodeType(RelasjonsRolleType.SAMBOER, NavBrukerKjønn.KVINNE))
                .isEqualTo(RelasjonskodeType.MEDMOR);
    }

    // Denne oppstår ved avslag/dokumentasjon før man har registrert ferdig papirsøknad. 14 tilfelle i P
    @Test
    public void skal_mappe_relasjonsrolle_ukjent() {
        assertThat(AvslagEngangstønadBrevMapper
                .tilRelasjonskodeType(RelasjonsRolleType.UDEFINERT, NavBrukerKjønn.KVINNE))
                .isEqualTo(RelasjonskodeType.MEDMOR);
    }

}
