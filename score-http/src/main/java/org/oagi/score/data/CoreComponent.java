package org.oagi.score.data;

import org.oagi.score.gateway.http.api.cc_management.data.CcState;

import java.math.BigInteger;

public interface CoreComponent extends Trackable {

    BigInteger getId();

    String getGuid();

    CcState getState();

    String getDen();

}
