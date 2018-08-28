package org.oagi.srt.gateway.http.api.bie_management.service.generate_expression;

import org.oagi.srt.data.TopLevelAbie;
import org.oagi.srt.gateway.http.api.bie_management.data.expression.GenerateExpressionOption;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Scope;

import java.io.File;
import java.io.IOException;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Scope(SCOPE_PROTOTYPE)
public class BieJSONGenerateExpression implements BieGenerateExpression, InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public void generate(TopLevelAbie topLevelAbie, GenerateExpressionOption option) {

    }

    @Override
    public File asFile(String filename) throws IOException {
        return null;
    }
}
