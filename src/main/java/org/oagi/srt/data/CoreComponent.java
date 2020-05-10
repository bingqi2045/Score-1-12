package org.oagi.srt.data;

import org.oagi.srt.gateway.http.api.cc_management.data.CcState;

import java.math.BigInteger;

public interface CoreComponent extends Trackable {

    BigInteger getId();

    String getGuid();

    CcState getState();

    String getDen();

}
