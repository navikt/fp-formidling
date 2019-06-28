package no.nav.foreldrepenger.melding.datamapper;

import static no.nav.foreldrepenger.melding.datamapper.mal.DokumentType.DEFAULT_PERSON_STATUS;
import static org.mockito.Mockito.doReturn;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

import org.mockito.Mockito;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametereImpl;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;

public class DatamapperTestUtil {

    public static String FRITEKST = "FRITEKST";
    public static final LocalDate FØRSTE_JANUAR_TJUENITTEN = LocalDate.of(2019, 1, 1);
    public static final int BEHANDLINGSFRIST = 4;


    static BrevParametereImpl brevParametere = new BrevParametereImpl(14, 14, Period.ofWeeks(6), Period.ofWeeks(6));

    public static BrevParametere getBrevParametere() {
        return brevParametere;
    }

    public static DokumentFelles getDokumentFelles() {
        DokumentFelles dokumentFelles = Mockito.mock(DokumentFelles.class);
        doReturn("Aleksander").when(dokumentFelles).getSakspartNavn();
        doReturn(DEFAULT_PERSON_STATUS).when(dokumentFelles).getSakspartPersonStatus();
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
        BehandlingType førstegangssøknad = Mockito.mock(BehandlingType.class);
        doReturn(BEHANDLINGSFRIST).when(førstegangssøknad).getBehandlingstidFristUker();
        doReturn(BehandlingType.FØRSTEGANGSSØKNAD.getKode()).when(førstegangssøknad).getKode();
        return Behandling.builder().medId(123l)
                .medBehandlingType(førstegangssøknad);
    }

    public static Behandling standardBehandling() {
        return standardBehandlingBuilder().build();
    }

}
