package org.oagi.score.data;

import org.oagi.score.service.common.data.CcState;
import org.oagi.score.service.common.data.Trackable;

public interface CoreComponent extends Trackable {

    String getId();

    String getGuid();

    CcState getState();

    String getDen();

}
