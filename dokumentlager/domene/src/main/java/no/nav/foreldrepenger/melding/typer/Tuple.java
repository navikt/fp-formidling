package no.nav.foreldrepenger.melding.typer;

public final record Tuple<T1, T2> (T1 element1, T2 element2) {

    public static <T1, T2> Tuple<T1, T2> of(T1 element1, T2 element2) {
        return new Tuple<>(element1, element2);
    }
}
