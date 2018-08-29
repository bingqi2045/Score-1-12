package org.oagi.srt.gateway.http.configuration.api.bie_management.generate_expression;

import org.oagi.srt.data.TopLevelAbie;
import org.oagi.srt.gateway.http.api.bie_management.service.generate_expression.BieJSONGenerateExpression;
import org.oagi.srt.gateway.http.api.bie_management.service.generate_expression.BieXMLGenerateExpression;
import org.oagi.srt.gateway.http.api.bie_management.service.generate_expression.GenerationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Configuration
public class BieGenerateExpressionConfiguration {

    @Bean
    @Scope(value = SCOPE_PROTOTYPE)
    public BieXMLGenerateExpression bieXMLGenerateExpression() {
        return new BieXMLGenerateExpression();
    }

    @Bean
    @Scope(value = SCOPE_PROTOTYPE)
    public BieJSONGenerateExpression bieJSONGenerateExpression() {
        return new BieJSONGenerateExpression();
    }

    @Bean
    @Scope(value = SCOPE_PROTOTYPE)
    public GenerationContext generationContext(TopLevelAbie topLevelAbie) {
        GenerationContext generationContext = new GenerationContext();
        generationContext.setTopLevelAbie(topLevelAbie);
        return generationContext;
    }
}
