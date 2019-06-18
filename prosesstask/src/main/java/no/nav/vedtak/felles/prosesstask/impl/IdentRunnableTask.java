package no.nav.vedtak.felles.prosesstask.impl;

/**
 * IdentRunnable som tar id og Runnable.
 */
class IdentRunnableTask implements IdentRunnable {
    private Long id;
    private Runnable runnable;

    IdentRunnableTask(Long id, Runnable run) {
        this.id = id;
        this.runnable = run;
    }

    @Override
    public void run() {
        runnable.run();
    }

    @Override
    public Long getId() {
        return id;
    }
}
