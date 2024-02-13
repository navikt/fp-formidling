package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import java.time.LocalDate;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.MottattdokumentMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.domene.klage.KlageDokument;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.InnhenteOpplysningerDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalType.INNHENTE_OPPLYSNINGER)
public class InnhenteOpplysningerDokumentdataMapper implements DokumentdataMapper {

    private BrevMapperUtil brevMapperUtil;
    private DomeneobjektProvider domeneobjektProvider;

    InnhenteOpplysningerDokumentdataMapper() {
        //CDI
    }

    @Inject
    public InnhenteOpplysningerDokumentdataMapper(BrevMapperUtil brevMapperUtil, DomeneobjektProvider domeneobjektProvider) {
        this.brevMapperUtil = brevMapperUtil;
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public String getTemplateNavn() {
        return "innhente-opplysninger";
    }

    @Override
    public InnhenteOpplysningerDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles,
                                                               DokumentHendelse hendelse,
                                                               Behandling behandling,
                                                               boolean erUtkast) {

        var fellesBuilder = BrevMapperUtil.opprettFellesBuilder(dokumentFelles, behandling, erUtkast);
        fellesBuilder.medBrevDato(
            dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), behandling.getSpråkkode()) : null);
        fellesBuilder.medFritekst(FritekstDto.fra(hendelse.getFritekst()));

        var dokumentdataBuilder = InnhenteOpplysningerDokumentdata.ny()
            .medFelles(fellesBuilder.build())
            .medFørstegangsbehandling(behandling.erFørstegangssøknad())
            .medRevurdering(behandling.erRevurdering())
            .medEndringssøknad(BrevMapperUtil.erEndringssøknad(behandling))
            .medDød(BrevMapperUtil.erDød(dokumentFelles))
            .medKlage(behandling.erKlage())
            .medSøknadDato(finnSøknadDato(behandling))
            .medFristDato(formaterDato(brevMapperUtil.getSvarFrist(), behandling.getSpråkkode()));

        return dokumentdataBuilder.build();
    }

    private String finnSøknadDato(Behandling behandling) {
        var mottatteDokumenter = domeneobjektProvider.hentMottatteDokumenter(behandling);

        Optional<KlageDokument> klageDokument = Optional.empty();
        if (behandling.erKlage()) {
            klageDokument = Optional.of(domeneobjektProvider.hentKlageDokument(behandling));
        }

        var mottattDato = klageDokument.map(kd -> hentMottattDatoFraKlage(kd, behandling))
            .orElseGet(() -> MottattdokumentMapper.finnSisteMottatteSøknad(mottatteDokumenter));
        return formaterDato(mottattDato, behandling.getSpråkkode());
    }

    private LocalDate hentMottattDatoFraKlage(KlageDokument klageDokument, Behandling behandling) {
        return klageDokument.mottattDato() != null ? klageDokument.mottattDato() : behandling.getOpprettetDato().toLocalDate();
    }
}
