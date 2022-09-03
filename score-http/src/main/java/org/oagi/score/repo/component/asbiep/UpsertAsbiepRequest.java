package org.oagi.score.repo.component.asbiep;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class UpsertAsbiepRequest extends RepositoryRequest {

    private final String topLevelAsbiepId;
    private final AsbiepNode.Asbiep asbiep;

    private String roleOfAbieId;
    private String refTopLevelAsbiepId;
    private boolean refTopLevelAsbiepIdNull;

    public UpsertAsbiepRequest(AuthenticatedPrincipal user, LocalDateTime localDateTime,
                               String topLevelAsbiepId, AsbiepNode.Asbiep asbiep) {
        super(user, localDateTime);
        this.topLevelAsbiepId = topLevelAsbiepId;
        this.asbiep = asbiep;
    }

    public String getTopLevelAsbiepId() {
        return topLevelAsbiepId;
    }

    public AsbiepNode.Asbiep getAsbiep() {
        return asbiep;
    }

    public String getRoleOfAbieId() {
        return roleOfAbieId;
    }

    public void setRoleOfAbieId(String roleOfAbieId) {
        this.roleOfAbieId = roleOfAbieId;
    }

    public String getRefTopLevelAsbiepId() {
        return refTopLevelAsbiepId;
    }

    public void setRefTopLevelAsbiepId(String refTopLevelAsbiepId) {
        this.refTopLevelAsbiepId = refTopLevelAsbiepId;
    }

    public boolean isRefTopLevelAsbiepIdNull() {
        return refTopLevelAsbiepIdNull;
    }

    public void setRefTopLevelAsbiepIdNull(boolean refTopLevelAsbiepIdNull) {
        this.refTopLevelAsbiepIdNull = refTopLevelAsbiepIdNull;
    }
}
