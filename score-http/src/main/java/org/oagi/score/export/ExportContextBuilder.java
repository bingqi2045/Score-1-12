package org.oagi.score.export;

import java.math.BigInteger;

public interface ExportContextBuilder {

    public ExportContext build(BigInteger moduleSetReleaseId);

}
