package no.nav.foreldrepenger.melding.brevmapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.brevSendesTilVerge;
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.erKopi;
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDatoNorsk;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.brevmapper.DokumentdataMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.IAYMapper;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.inntektarbeidytelse.InntektArbeidYtelse;
import no.nav.foreldrepenger.melding.inntektarbeidytelse.Inntektsmelding;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.FellesDokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.IkkeSøktDokumentdata;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalTypeKode;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalTypeKode.IKKE_SØKT)
public class IkkeSøktDokumentdataMapper implements DokumentdataMapper {

    private DomeneobjektProvider domeneobjektProvider;

    IkkeSøktDokumentdataMapper() {
        //CDI
    }

    @Inject
    public IkkeSøktDokumentdataMapper(DomeneobjektProvider domeneobjektProvider) {
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public String getTemplateNavn() {
        return "ikke-sokt";
    }

    @Override
    public IkkeSøktDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelse hendelse, Behandling behandling) {

        var felles = FellesDokumentdata.ny()
                .medSøkerNavn(dokumentFelles.getSakspartNavn())
                .medSøkerPersonnummer(formaterPersonnummer(dokumentFelles.getSakspartId()))
                .medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDatoNorsk(dokumentFelles.getDokumentDato()) : null)
                .medHarVerge(dokumentFelles.getErKopi() != null && dokumentFelles.getErKopi().isPresent())
                .medErKopi(dokumentFelles.getErKopi() != null && dokumentFelles.getErKopi().isPresent() && erKopi(dokumentFelles.getErKopi().get()))
                .medSaksnummer(dokumentFelles.getSaksnummer().getVerdi())
                .medYtelseType(hendelse.getYtelseType().getKode());

        if (brevSendesTilVerge(dokumentFelles)) {
            felles.medMottakerNavn(dokumentFelles.getMottakerNavn());
        }

        InntektArbeidYtelse iay = domeneobjektProvider.hentInntektArbeidYtelse(behandling);
        Inntektsmelding inntektsmelding = IAYMapper.hentNyesteInntektsmelding(iay);

        var ikkeSøktDokumentdataBuilder = IkkeSøktDokumentdata.ny()
                .medFelles(felles.build())
                .medArbeidsgiverNavn(inntektsmelding.getArbeidsgiverNavn())
                .medMottattDato(formaterDatoNorsk(inntektsmelding.getInnsendingstidspunkt()));

        return ikkeSøktDokumentdataBuilder.build();
    }
}
