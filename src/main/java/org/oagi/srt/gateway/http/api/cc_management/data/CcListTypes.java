package org.oagi.srt.gateway.http.api.cc_management.data;

import lombok.Data;
import org.springframework.util.StringUtils;

@Data
public class CcListTypes {

    private boolean acc;
    private boolean asccp;
    private boolean bccp;
    private boolean ascc;
    private boolean bcc;

    public static CcListTypes fromString(String str) {
        if (str != null) {
            str = str.trim();
        }

        CcListTypes types = new CcListTypes();
        if (!StringUtils.isEmpty(str)) {
            for (String type : str.split(",")) {
                switch (type) {
                    case "acc":
                        types.setAcc(true);
                        break;

                    case "asccp":
                        types.setAsccp(true);
                        break;

                    case "bccp":
                        types.setBccp(true);
                        break;

                    case "ascc":
                        types.setAscc(true);
                        break;

                    case "bcc":
                        types.setBcc(true);
                        break;
                }
            }
        }

        return types;
    }

}
