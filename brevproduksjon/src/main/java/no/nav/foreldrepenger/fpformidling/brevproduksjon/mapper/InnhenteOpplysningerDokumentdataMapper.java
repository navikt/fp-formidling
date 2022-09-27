package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.erDød;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.erEndringssøknad;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.opprettFellesBuilder;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import java.time.LocalDate;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.MottattdokumentMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.InnhenteOpplysningerDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
import no.nav.foreldrepenger.fpformidling.klage.KlageDokument;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalTypeKode;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalTypeKode.INNHENTE_OPPLYSNINGER)
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
    public InnhenteOpplysningerDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelse hendelse,
                                                               Behandling behandling, boolean erUtkast) {

        var fellesBuilder = opprettFellesBuilder(dokumentFelles, hendelse, behandling, erUtkast);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), behandling.getSpråkkode()) : null);
        fellesBuilder.medFritekst(FritekstDto.fra(hendelse.getFritekst()));

        var dokumentdataBuilder = InnhenteOpplysningerDokumentdata.ny()
                .medFelles(fellesBuilder.build())
                .medFørstegangsbehandling(behandling.erFørstegangssøknad())
                .medRevurdering(behandling.erRevurdering())
                .medEndringssøknad(erEndringssøknad(behandling))
                .medDød(erDød(dokumentFelles))
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
