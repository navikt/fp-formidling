package no.nav.foreldrepenger.melding.datamapper.domene;

@SuppressWarnings("java:S1700")
public final class AvvistGrunn {

    private String avvistGrunn;


    public AvvistGrunn(String avvistGrunn) {

        this.avvistGrunn = avvistGrunn;
    }

    @SuppressWarnings("WeakerAccess") // Handlebar trenger public getter
    public String getAvvistGrunn() {
        return avvistGrunn;
    }
}
