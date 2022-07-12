package org.oagi.score.service.log.model;

public enum LogAction {
    Added,
    Revised,
    Canceled,
    Modified,
    Deleted,
    Restored,
    Associated,
    Refactored,
    Ungrouped,
    IGNORE,
    // This tag indicates the current status of the component. DO NOT use for DB records.
    Current
}
