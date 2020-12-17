package no.nav.foreldrepenger.fpsak.dto.soknad;

import java.time.LocalDate;
import java.util.Map;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

public class SoknadAdopsjonDto extends SoknadDto {
    private LocalDate omsorgsovertakelseDato;
    private LocalDate barnetsAnkomstTilNorgeDato;
    private Map<Integer, LocalDate> adopsjonFodelsedatoer;
    private KodeDto farSokerType;

    public SoknadAdopsjonDto() {
        super();
    }

    public LocalDate getOmsorgsovertakelseDato() {
        return omsorgsovertakelseDato;
    }

    public Map<Integer, LocalDate> getAdopsjonFodelsedatoer() {
        return adopsjonFodelsedatoer;
    }


    public KodeDto getFarSokerType() {
        return farSokerType;
    }

    public boolean erOmsorgsovertakelse() {
        return farSokerType != null && !farSokerType.getKode().equals("ADOPTERER_ALENE");
    }

    public void setOmsorgsovertakelseDato(LocalDate omsorgsovertakelseDato) {
        this.omsorgsovertakelseDato = omsorgsovertakelseDato;
    }

    public void setAdopsjonFodelsedatoer(Map<Integer, LocalDate> adopsjonFodelsedatoer) {
        this.adopsjonFodelsedatoer = adopsjonFodelsedatoer;
    }

    public void setFarSokerType(KodeDto farSokerType) {
        this.farSokerType = farSokerType;
    }

    public LocalDate getBarnetsAnkomstTilNorgeDato() {
        return barnetsAnkomstTilNorgeDato;
    }

    public void setBarnetsAnkomstTilNorgeDato(LocalDate barnetsAnkomstTilNorgeDato) {
        this.barnetsAnkomstTilNorgeDato = barnetsAnkomstTilNorgeDato;
    }
}
