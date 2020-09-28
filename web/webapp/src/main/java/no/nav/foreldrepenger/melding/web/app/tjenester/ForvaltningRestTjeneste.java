package no.nav.foreldrepenger.melding.web.app.tjenester;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.CREATE;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt.DRIFT;

import java.time.LocalDate;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.DokgenRestKlient;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.EngangsstønadInnvilgelseDokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.FellesDokumentdata;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;

@Path("/forvaltning")
@ApplicationScoped
@Transactional
public class ForvaltningRestTjeneste {

    private DokgenRestKlient dokgenRestKlient;

    public ForvaltningRestTjeneste() {
        // CDI
    }

    @Inject
    public ForvaltningRestTjeneste(DokgenRestKlient dokgenRestKlient) {
        this.dokgenRestKlient = dokgenRestKlient;
    }

    //TODO(JEJ): Fjerne når vi ikke lengre trenger til testing:
    @POST
    @Path("/teste-dokgen")
    @Consumes(APPLICATION_JSON)
    @Produces("application/pdf")
    @Operation(description = "Tester brev med Dokgen",
            tags = "forvaltning",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Returnerer PDF",
                            content = @Content(
                                    mediaType = "application/pdf",
                                    schema = @Schema(type = "string", format = "byte")
                            )
                    )
            })
    @BeskyttetRessurs(action = CREATE, ressurs = DRIFT)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Response testeDokgen() {
        String malType = "engangsstonad-innvilgelse";

        FellesDokumentdata fellesDokumentdata = new FellesDokumentdata.Builder()
                .søkerNavn("Dolly Duck")
                .søkerPersonnummer("11111111111")
                .brevDato(LocalDate.now())
                .build();

        EngangsstønadInnvilgelseDokumentdata dokumentdata = new EngangsstønadInnvilgelseDokumentdata.Builder()
                .felles(fellesDokumentdata)
                .revurdering(true)
                .førstegangsbehandling(true)
                .medhold(false)
                .innvilgetBeløp(85000)
                .klagefristUker(6)
                .død(false)
                .fbEllerMedhold(true)
                .kontaktTelefonnummer("22 55 55 55")
                .endretSats(0)
                .build();

        Optional<byte[]> resultat = dokgenRestKlient.genererPdf(malType, dokumentdata);
        Response.ResponseBuilder responseBuilder = Response.ok(resultat.get());
        responseBuilder.type("application/pdf");
        responseBuilder.header("Content-Disposition", "attachment; filename=dokument.pdf");
        return responseBuilder.build();
    }
}
