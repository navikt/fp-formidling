package no.nav.foreldrepenger.fpformidling.inntektarbeidytelse;

public final class OrganisasjonsNummerValidator {

    private OrganisasjonsNummerValidator() {
    }

    public static boolean erGyldig(String orgnummer) {

        // Skal inneholde 9 siffer og kun tall
        if (orgnummer == null || orgnummer.length() != 9) {
            return false;
        }

        int sisteSiffer = Character.getNumericValue(orgnummer.charAt(orgnummer.length() - 1));

        return getKontrollSiffer(orgnummer) == sisteSiffer;
    }

    private static int getKontrollSiffer(String number) {
        int lastIndex = number.length() - 1;
        int sum = 0;

        for (int i = 0; i < lastIndex; i++) {
            sum += Character.getNumericValue(number.charAt(i)) * getVektTall(i);
        }

        int rest = sum % 11;

        return getKontrollSifferFraRest(rest);
    }

    private static int getVektTall(int i) {
        int[] vekttall = {3, 2, 7, 6, 5, 4, 3, 2};
        return vekttall[i];
    }

    private static int getKontrollSifferFraRest(int rest) {
        if (rest == 0) {
            return 0;
        }
        return 11 - rest;
    }
}
