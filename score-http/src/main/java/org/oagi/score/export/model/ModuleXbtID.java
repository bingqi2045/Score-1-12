package org.oagi.score.export.model;

import lombok.Data;
import org.jooq.types.ULong;

@Data
public class ModuleXbtID {
    public ULong moduleId;
    public String xbtId;
    public String path;
}
