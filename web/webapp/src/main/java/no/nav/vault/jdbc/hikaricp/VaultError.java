package no.nav.vault.jdbc.hikaricp;

public final class VaultError extends Exception {
    public VaultError(String message, Throwable cause) {
        super(message, cause);
    }
}
