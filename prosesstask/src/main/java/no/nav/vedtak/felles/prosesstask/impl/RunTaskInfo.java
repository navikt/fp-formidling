package no.nav.vedtak.felles.prosesstask.impl;

import java.time.LocalDateTime;
import java.util.Objects;

import no.nav.vedtak.felles.prosesstask.api.ProsessTaskDispatcher;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskInfo;

/**
 * Info knyttet til en enkelt kjøring av en task.
 */
class RunTaskInfo {

    private static final LocalDateTime MIN_TIMESTAMP = LocalDateTime.of(1970, 01, 01, 00, 00);

    private final ProsessTaskDispatcher taskDispatcher;
    private final Long id;
    private final String taskType;
    private final LocalDateTime timestampLowWatermark;

    RunTaskInfo(ProsessTaskDispatcher dispatcher, ProsessTaskInfo task) {
        this(dispatcher, task.getId(), task.getTaskType(), task.getSistKjørt());
    }

    RunTaskInfo(ProsessTaskDispatcher dispatcher, Long id, String taskType, LocalDateTime timestampLowWatermark) {
        Objects.requireNonNull(id, "id"); //$NON-NLS-1$
        Objects.requireNonNull(taskType, "taskName"); //$NON-NLS-1$

        this.id = id;
        this.taskType = taskType;
        if (timestampLowWatermark != null) {
            this.timestampLowWatermark = timestampLowWatermark.withNano(0); // rundt av nedover til sekund for kunne match med neste kjøring (som har sekund oppløsning)
        } else {
            this.timestampLowWatermark = MIN_TIMESTAMP;
        }
        this.taskDispatcher = dispatcher;
    }

    ProsessTaskDispatcher getTaskDispatcher() {
        return taskDispatcher;
    }

    Long getId() {
        return id;
    }

    String getTaskType() {
        return taskType;
    }

    LocalDateTime getTimestampLowWatermark() {
        return timestampLowWatermark;
    }
}