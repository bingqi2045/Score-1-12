package org.oagi.score.gateway.http.api.cc_management.data;

import lombok.Data;

import java.util.List;

@Data
public class CreateOagisBodResponse {

    private List<String> manifestIdList;

}
