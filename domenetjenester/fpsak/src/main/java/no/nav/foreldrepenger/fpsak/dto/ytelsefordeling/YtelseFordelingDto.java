package no.nav.foreldrepenger.fpsak.dto.ytelsefordeling;

public class YtelseFordelingDto {
    private int gjeldendeDekningsgrad;

    private YtelseFordelingDto() {
    }

    public int getGjeldendeDekningsgrad() {
        return gjeldendeDekningsgrad;
    }

    public static class Builder {

        private final YtelseFordelingDto kladd = new YtelseFordelingDto();

        public Builder medGjeldendeDekningsgrad(int gjeldendeDekningsgrad) {
            kladd.gjeldendeDekningsgrad = gjeldendeDekningsgrad;
            return this;
        }

        public YtelseFordelingDto build() {
            return kladd;
        }
    }
}
