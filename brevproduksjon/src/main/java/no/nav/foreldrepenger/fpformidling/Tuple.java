package no.nav.foreldrepenger.fpformidling;

public record Tuple<T1, T2> (T1 element1, T2 element2) {

    public T1 getElement1() {
        return element1();
    }

    public T2 getElement2() {
        return element2();
    }

}
