package no.nav.vedtak.felles.prosesstask.dbstoette;

import no.nav.vedtak.felles.testutilities.db.RepositoryRule;

public class UnittestRepositoryRule extends RepositoryRule {

    static {
        Databaseskjemainitialisering.kjørMigreringHvisNødvendig();
        Databaseskjemainitialisering.settPlaceholdereOgJdniOppslag();
    }

    public UnittestRepositoryRule() {
        super();
    }

    public UnittestRepositoryRule(boolean transaksjonell) {
        super(transaksjonell);
    }

    @Override
    protected void init() {
    }

}
