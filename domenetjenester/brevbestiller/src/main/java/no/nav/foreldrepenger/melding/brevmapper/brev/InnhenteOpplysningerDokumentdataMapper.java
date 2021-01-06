package no.nav.foreldrepenger.melding.brevmapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.brevSendesTilVerge;
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.erDød;
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.erEndringssøknad;
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.erKopi;
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.konverterFritekstTilListe;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDatoEngelsk;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDatoNorsk;

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
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.FellesDokumentdata;
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
    public InnhenteOpplysningerDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelse hendelse, Behandling behandling) {

        var felles = FellesDokumentdata.ny()
                .medSøkerNavn(dokumentFelles.getSakspartNavn())
                .medSøkerPersonnummer(formaterPersonnummer(dokumentFelles.getSakspartId()))
                .medBrevDato(dokumentFelles.getDokumentDato()!= null ? formaterDatoNorsk(dokumentFelles.getDokumentDato()) : null)
                .medHarVerge(dokumentFelles.getErKopi() != null && dokumentFelles.getErKopi().isPresent())
                .medErKopi(dokumentFelles.getErKopi() != null && dokumentFelles.getErKopi().isPresent() && erKopi(dokumentFelles.getErKopi().get()))
                .medSaksnummer(dokumentFelles.getSaksnummer().getVerdi())
                .medYtelseType(hendelse.getYtelseType().getKode());

        if (brevSendesTilVerge(dokumentFelles)) {
            felles.medMottakerNavn(dokumentFelles.getMottakerNavn());
        }

        var innhenteOpplysningerDokumentDataBuilder = InnhenteOpplysningerDokumentdata.ny()
                .medFelles(felles.build())
                .medFørstegangsbehandling(behandling.erFørstegangssøknad())
                .medRevurdering(behandling.erRevurdering())
                .medEndringssøknad(erEndringssøknad(behandling))
                .medDød(erDød(dokumentFelles))
                .medKlage(behandling.erKlage())
                .medSøknadDato(finnSøknadDato(behandling))
                .medFristDato(Språkkode.EN.equals(behandling.getSpråkkode()) ? formaterDatoEngelsk(brevMapperUtil.getSvarFrist()) : formaterDatoNorsk(brevMapperUtil.getSvarFrist()))
                .medDokumentListe(konverterFritekstTilListe(hendelse.getFritekst()));

        return innhenteOpplysningerDokumentDataBuilder.build();
    }

    private String finnSøknadDato(Behandling behandling) {
        List<MottattDokument> mottatteDokumenter = domeneobjektProvider.hentMottatteDokumenter(behandling);

        Optional<KlageDokument> klageDokument = Optional.empty();
        if (behandling.erKlage()) {
            klageDokument = Optional.of(domeneobjektProvider.hentKlageDokument(behandling));
        }

        LocalDate mottattDato = klageDokument.map(kd -> hentMottattDatoFraKlage(kd, behandling))
                .orElseGet(() -> MottattdokumentMapper.finnSøknadsdatoFraMottatteDokumenter(behandling, mottatteDokumenter));
        return formaterDatoNorsk(mottattDato);
    }

    private LocalDate hentMottattDatoFraKlage(KlageDokument klageDokument, Behandling behandling) {
        return klageDokument.getMottattDato() != null ? klageDokument.getMottattDato() : behandling.getOpprettetDato().toLocalDate();
    }
}
