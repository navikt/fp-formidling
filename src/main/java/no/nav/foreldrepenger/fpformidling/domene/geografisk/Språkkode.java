package no.nav.foreldrepenger.fpformidling.domene.geografisk;


import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

import java.util.Optional;
import java.util.stream.Stream;

public enum Språkkode {

    @JsonEnumDefaultValue NB,
    NN,
    EN;

    public static Språkkode defaultNorsk(String kode) {
        return finnSpråkIgnoreCase(kode).orElse(NB);
    }

    private static Optional<Språkkode> finnSpråkIgnoreCase(String kode) {
        if (kode == null) {
            return Optional.empty();
        }
        return Stream.of(NB, NN, EN).filter(sp -> kode.equalsIgnoreCase(sp.name())).findFirst();
    }
}
