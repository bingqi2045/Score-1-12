package org.oagi.srt.gateway.http.api.cc_management.data;

import org.oagi.srt.gateway.http.api.common.data.Trackable;

public interface CoreComponent extends Trackable {

    long getId();

    String getGuid();

    int getState();

    String getDen();

}
