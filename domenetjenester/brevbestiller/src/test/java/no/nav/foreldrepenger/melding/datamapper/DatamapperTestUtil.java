package no.nav.foreldrepenger.melding.datamapper;

import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

import org.mockito.Mockito;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;

public class DatamapperTestUtil {

    public static final String SOEKERS_NAVN = "SOEKERS_NAVN";
    public static final String FRITEKST = "FRITEKST";
    public static final LocalDate FØRSTE_JANUAR_TJUENITTEN = LocalDate.of(2019, 1, 1);
    public static final Period SVARFRIST = Period.ofWeeks(6);

    private static final int KLAGEFRIST = 14;
    private static final int KLAGEFRIST_INNSYN = 14;
    private static final Period SØK_ANTALL_UKER = Period.ofWeeks(6);

    private static BrevParametere brevParametere = new BrevParametere(KLAGEFRIST, KLAGEFRIST_INNSYN, SVARFRIST, SØK_ANTALL_UKER);

    public static BrevParametere getBrevParametere() {
        return brevParametere;
    }

    public static DokumentFelles getDokumentFelles() {
        DokumentFelles dokumentFelles = Mockito.mock(DokumentFelles.class);
        when(dokumentFelles.getSakspartNavn()).thenReturn(SOEKERS_NAVN);
        when(dokumentFelles.getSakspartPersonStatus()).thenReturn("ANNET");
        return dokumentFelles;
    }

    public static FellesType getFellesType() {
        return new FellesType();
    }

    public static DokumentHendelse.Builder lagStandardHendelseBuilder() {
        return DokumentHendelse.builder()
                .medBestillingUuid(UUID.randomUUID())
                .medBehandlingUuid(UUID.randomUUID())
                .medFritekst(FRITEKST)
                .medYtelseType(FagsakYtelseType.FORELDREPENGER);
    }

    public static DokumentHendelse standardDokumenthendelse() {
        return lagStandardHendelseBuilder().build();
    }

    public static Behandling.Builder standardBehandlingBuilder() {
        return Behandling.builder().medId(123L).medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD).medSpråkkode(Språkkode.nb);
    }

    public static Behandling standardBehandling() {
        return standardBehandlingBuilder().build();
    }

}
