package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.avslagfp;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.opprettFellesBuilder;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.MottattdokumentMapper.finnførsteMottatteSøknad;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BeregningsgrunnlagMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.fagsak.FagsakBackend;
import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.avslagfp.ForeldrepengerAvslagDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalTypeKode;
import no.nav.foreldrepenger.fpformidling.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseForeldrepenger;
import no.nav.foreldrepenger.fpformidling.uttak.ForeldrepengerUttak;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalTypeKode.FORELDREPENGER_AVSLAG)
public class ForeldrepengerAvslagDokumentdataMapper implements DokumentdataMapper {

    private static final Map<RelasjonsRolleType, String> relasjonskodeTypeMap;
    static {
        relasjonskodeTypeMap = new EnumMap<>(RelasjonsRolleType.class);
        relasjonskodeTypeMap.put(RelasjonsRolleType.MORA, "MOR");
        relasjonskodeTypeMap.put(RelasjonsRolleType.FARA, "FAR");
        relasjonskodeTypeMap.put(RelasjonsRolleType.MEDMOR, "MEDMOR");
    }

    private BrevParametere brevParametere;
    private DomeneobjektProvider domeneobjektProvider;

    ForeldrepengerAvslagDokumentdataMapper() {
        //CDI
    }

    @Inject
    public ForeldrepengerAvslagDokumentdataMapper(BrevParametere brevParametere,
                                                  DomeneobjektProvider domeneobjektProvider) {
        this.brevParametere = brevParametere;
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public String getTemplateNavn() {
        return "foreldrepenger-avslag";
    }

    @Override
    public ForeldrepengerAvslagDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelse dokumentHendelse,
                                                               Behandling behandling, boolean erUtkast) {

        var fellesBuilder = opprettFellesBuilder(dokumentFelles, dokumentHendelse, behandling, erUtkast);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), behandling.getSpråkkode()) : null);
        fellesBuilder.medErAutomatiskBehandlet(dokumentFelles.getAutomatiskBehandlet());
        FritekstDto.fra(dokumentHendelse, behandling).ifPresent(fellesBuilder::medFritekst);

        var fagsak = domeneobjektProvider.hentFagsakBackend(behandling);
        var mottatteDokumenter = domeneobjektProvider.hentMottatteDokumenter(behandling);
        var familiehendelse = domeneobjektProvider.hentFamiliehendelse(behandling);
        var beregningsgrunnlagOpt = domeneobjektProvider.hentBeregningsgrunnlagHvisFinnes(behandling);
        var halvG = BeregningsgrunnlagMapper.getHalvGOrElseZero(beregningsgrunnlagOpt);
        var uttakResultatPerioder = domeneobjektProvider.hentForeldrepengerUttakHvisFinnes(behandling);

        var dokumentdataBuilder = ForeldrepengerAvslagDokumentdata.ny()
                .medFelles(fellesBuilder.build())
                .medRelasjonskode(finnRelasjonskode(fagsak))
                .medMottattDato(formaterDato(finnførsteMottatteSøknad(mottatteDokumenter), behandling.getSpråkkode()))
                .medGjelderFødsel(familiehendelse.gjelderFødsel())
                .medBarnErFødt(familiehendelse.barnErFødt())
                .medAnnenForelderHarRett(uttakResultatPerioder.map(ForeldrepengerUttak::annenForelderHarRett).orElse(false))
                .medAntallBarn(familiehendelse.antallBarn())
                .medHalvG(halvG)
                .medKlagefristUker(brevParametere.getKlagefristUker())
                .medKreverSammenhengendeUttak(domeneobjektProvider.kreverSammenhengendeUttak(behandling));

        mapAvslåttePerioder(behandling, dokumentdataBuilder, uttakResultatPerioder);

        return dokumentdataBuilder.build();
    }

    private void mapAvslåttePerioder(Behandling behandling, ForeldrepengerAvslagDokumentdata.Builder dokumentdataBuilder,
                                     Optional<ForeldrepengerUttak> uttakResultatPerioder) {
        var tilkjentYtelseFP = domeneobjektProvider.hentTilkjentYtelseFPHvisFinnes(behandling);
        var avslåttePerioderOgLovhjemmel = AvslåttPeriodeMapper.mapAvslåttePerioderOgLovhjemmel(
                behandling,
                tilkjentYtelseFP.map(TilkjentYtelseForeldrepenger::getPerioder).orElse(Collections.emptyList()),
                uttakResultatPerioder);

        dokumentdataBuilder.medLovhjemmelForAvslag(avslåttePerioderOgLovhjemmel.element2());
        dokumentdataBuilder.medAvslåttePerioder(avslåttePerioderOgLovhjemmel.element1());
    }

    private String finnRelasjonskode(FagsakBackend fagsak) {
        if (RelasjonsRolleType.erRegistrertForeldre(fagsak.getRelasjonsRolleType())) {
            return relasjonskodeTypeMap.get(fagsak.getRelasjonsRolleType());
        }
        return "ANNET";
    }
}
