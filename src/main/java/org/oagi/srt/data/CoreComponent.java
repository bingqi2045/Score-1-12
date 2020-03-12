package org.oagi.srt.data;

import org.oagi.srt.gateway.http.api.cc_management.data.CcState;

public interface CoreComponent extends Trackable {

    long getId();

    String getGuid();

    CcState getState();

    String getDen();

}
