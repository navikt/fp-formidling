package no.nav.foreldrepenger.melding.brevmapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.erDød;
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.erEndringssøknad;
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.ivaretaLinjeskiftIFritekst;
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.opprettFellesBuilder;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDato;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.brevmapper.DokumentdataMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.MottattdokumentMapper;
import no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.InnhenteOpplysningerDokumentdata;
import no.nav.foreldrepenger.melding.klage.KlageDokument;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalTypeKode;
import no.nav.foreldrepenger.melding.mottattdokument.MottattDokument;

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
        fellesBuilder.medFritekst(ivaretaLinjeskiftIFritekst(hendelse.getFritekst()));

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
        List<MottattDokument> mottatteDokumenter = domeneobjektProvider.hentMottatteDokumenter(behandling);

        Optional<KlageDokument> klageDokument = Optional.empty();
        if (behandling.erKlage()) {
            klageDokument = Optional.of(domeneobjektProvider.hentKlageDokument(behandling));
        }

        LocalDate mottattDato = klageDokument.map(kd -> hentMottattDatoFraKlage(kd, behandling))
                .orElseGet(() -> MottattdokumentMapper.finnSøknadsdatoFraMottatteDokumenter(behandling, mottatteDokumenter));
        return formaterDato(mottattDato, behandling.getSpråkkode());
    }

    private LocalDate hentMottattDatoFraKlage(KlageDokument klageDokument, Behandling behandling) {
        return klageDokument.motattDato() != null ? klageDokument.motattDato() : behandling.getOpprettetDato().toLocalDate();
    }
}
