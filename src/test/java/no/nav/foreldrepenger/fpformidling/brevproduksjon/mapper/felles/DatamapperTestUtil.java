package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import static no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles.PersonStatus.ANNET;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlag.BehandlingType;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlag.Behandlingsresultat;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlag.FagsakStatus;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlag.FamilieHendelse;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlag.InnsynBehandling;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlag.KlageBehandling;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlag.RelasjonsRolleType;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.brevGrunnlag;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.UUID;

import org.mockito.Mockito;

import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles.Kopi;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.domene.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlag;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders;
import no.nav.foreldrepenger.fpformidling.typer.DokumentMal;
import no.nav.foreldrepenger.fpformidling.typer.PersonIdent;
import no.nav.foreldrepenger.fpformidling.typer.Saksnummer;
import no.nav.foreldrepenger.kontrakter.fpsak.inntektsmeldinger.ArbeidsforholdInntektsmeldingerDto;

public class DatamapperTestUtil {

    public static final String SØKERS_NAVN = "Bruker Brukersen";
    public static final String SØKERS_FNR = "11111111111";
    public static final String VERGES_NAVN = "Verge Vergesen";
    public static final String VERGES_FNR = "99999999999";
    public static final String SAKSNUMMER = "123456";
    public static final String FRITEKST = "FRITEKST";
    public static final Period SVARFRIST = Period.ofWeeks(6);

    private static final int KLAGEFRIST = 14;
    private static final int KLAGEFRIST_INNSYN = 14;
    private static final Period SØK_ANTALL_UKER = Period.ofWeeks(6);

    private static BrevParametere brevParametere = new BrevParametere(KLAGEFRIST, KLAGEFRIST_INNSYN, SVARFRIST, SØK_ANTALL_UKER);

    public static BrevParametere getBrevParametere() {
        return brevParametere;
    }

    public static DokumentFelles getDokumentFelles() {
        var dokumentFelles = Mockito.mock(DokumentFelles.class);
        when(dokumentFelles.getSakspartNavn()).thenReturn(SØKERS_NAVN);
        when(dokumentFelles.getSakspartPersonStatus()).thenReturn(ANNET);
        return dokumentFelles;
    }

    public static DokumentFelles lagStandardDokumentFelles(FagsakYtelseType fagsakYtelseType) {
        return lagStandardDokumentFelles(null, false, fagsakYtelseType);
    }

    public static DokumentFelles lagStandardDokumentFelles(Kopi kopi, boolean tilVerge, FagsakYtelseType fagsakYtelseType) {
        return standardDokumentFellesBuilder(kopi, tilVerge, fagsakYtelseType).build();
    }

    public static DokumentFelles.Builder lagStandardDokumentFellesBuilder(FagsakYtelseType fagsakYtelseType) {
        return standardDokumentFellesBuilder(null, false, fagsakYtelseType);
    }

    public static DokumentFelles.Builder standardDokumentFellesBuilder(Kopi kopi, boolean tilVerge, FagsakYtelseType fagsakYtelseType) {
        return DokumentFelles.builder()
            .medAutomatiskBehandlet(Boolean.TRUE)
            .medDokumentDato(LocalDate.now())
            .medMottakerId(tilVerge ? VERGES_FNR : SØKERS_FNR)
            .medMottakerNavn(tilVerge ? VERGES_NAVN : SØKERS_NAVN)
            .medSaksnummer(new Saksnummer(SAKSNUMMER))
            .medSakspartId(PersonIdent.fra(SØKERS_FNR))
            .medSakspartNavn(SØKERS_NAVN)
            .medErKopi(kopi)
            .medMottakerType(DokumentFelles.MottakerType.PERSON)
            .medSpråkkode(Språkkode.NB)
            .medSakspartPersonStatus(ANNET)
            .medYtelseType(fagsakYtelseType);
    }

    public static DokumentHendelse.Builder lagStandardHendelseBuilder() {
        return DokumentHendelse.builder()
            .medBestillingUuid(UUID.randomUUID())
            .medBehandlingUuid(UUID.randomUUID())
            .medDokumentMal(DokumentMal.FRITEKSTBREV)
            .medFritekst(FRITEKST);
    }

    public static DokumentHendelse standardDokumenthendelse() {
        return lagStandardHendelseBuilder().build();
    }

    public static BrevGrunnlagBuilders.BrevGrunnlagBuilder defaultBuilder() {
        return brevGrunnlag().uuid(UUID.randomUUID())
            .saksnummer(UUID.randomUUID().toString())
            .fagsakStatus(FagsakStatus.UNDER_BEHANDLING)
            .relasjonsRolleType(RelasjonsRolleType.MORA)
            .aktørId(UUID.randomUUID().toString())
            .opprettet(LocalDateTime.now().minusDays(1))
            .behandlendeEnhet("enhet")
            .språkkode(BrevGrunnlag.Språkkode.BOKMÅL)
            .automatiskBehandlet(true)
            .behandlingÅrsakTyper(List.of())
            .inntektsmeldinger(List.of());
    }

    public static BrevGrunnlag standardBrevGrunnlag(BrevGrunnlag.FagsakYtelseType ytelseType,
                                                    Behandlingsresultat behandlingsresultat,
                                                    KlageBehandling klageBehandling,
                                                    InnsynBehandling innsynBehandling,
                                                    ArbeidsforholdInntektsmeldingerDto imStatus) {
        var behandlingType =
            klageBehandling != null ? BehandlingType.KLAGE : innsynBehandling != null ? BehandlingType.INNSYN : BehandlingType.FØRSTEGANGSSØKNAD;
        return defaultBuilder().fagsakYtelseType(ytelseType)
            .behandlingType(behandlingType)
            .familieHendelse(standardFamilieHendelse())
            .behandlingsresultat(behandlingsresultat)
            .inntektsmeldingerStatus(imStatus)
            .klageBehandling(klageBehandling)
            .innsynBehandling(innsynBehandling)
            .build();
    }

    public static BrevGrunnlag standardForeldrepengerBrevGrunnlag() {
        return standardForeldrepengerBrevGrunnlag(null);
    }

    public static BrevGrunnlag standardForeldrepengerBrevGrunnlag(ArbeidsforholdInntektsmeldingerDto imStatus) {
        return defaultBuilder()
            .fagsakYtelseType(BrevGrunnlag.FagsakYtelseType.FORELDREPENGER)
            .behandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
            .familieHendelse(standardFamilieHendelse())
            .inntektsmeldingerStatus(imStatus)
            .build();
    }

    public static BrevGrunnlag klageForeldrepengerBrevGrunnlag(KlageBehandling klageBehandling) {
        return defaultBuilder()
            .fagsakYtelseType(BrevGrunnlag.FagsakYtelseType.FORELDREPENGER)
            .behandlingType(BehandlingType.KLAGE)
            .familieHendelse(standardFamilieHendelse())
            .klageBehandling(klageBehandling)
            .build();
    }

    public static BrevGrunnlag innsynBrevGrunnlag(InnsynBehandling innsynBehandling, BrevGrunnlag.FagsakYtelseType fagsakYtelseType) {
        return defaultBuilder()
            .fagsakYtelseType(fagsakYtelseType)
            .behandlingType(BehandlingType.INNSYN)
            .familieHendelse(standardFamilieHendelse())
            .innsynBehandling(innsynBehandling)
            .build();
    }

    public static BrevGrunnlag standardSvangerskapspengerBrevGrunnlag(ArbeidsforholdInntektsmeldingerDto imStatus) {
        return defaultBuilder()
            .fagsakYtelseType(BrevGrunnlag.FagsakYtelseType.SVANGERSKAPSPENGER)
            .behandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
            .familieHendelse(standardFamilieHendelse())
            .inntektsmeldingerStatus(imStatus)
            .build();
    }

    private static FamilieHendelse standardFamilieHendelse() {
        return new FamilieHendelse(List.of(), LocalDate.now().minusWeeks(1), 1, null);
    }

    public static BrevGrunnlag avslåttBrevGrunnlag(BrevGrunnlag.FagsakYtelseType ytelseType,
                                                   Avslagsårsak avslagsårsak,
                                                   String avslagsfritekst,
                                                   FamilieHendelse familieHendelse,
                                                   Behandlingsresultat.VilkårType vilkårType) {
        var behandlingsresultat = new Behandlingsresultat(null, null, Behandlingsresultat.BehandlingResultatType.AVSLÅTT, avslagsårsak.getKode(),
            new Behandlingsresultat.Fritekst("avslag", null, avslagsfritekst), null, false, null, List.of(), List.of(vilkårType));
        return defaultBuilder().fagsakYtelseType(ytelseType)
            .behandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
            .familieHendelse(familieHendelse)
            .behandlingsresultat(behandlingsresultat)
            .build();
    }


}
