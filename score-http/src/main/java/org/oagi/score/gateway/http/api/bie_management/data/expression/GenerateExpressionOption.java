package org.oagi.score.gateway.http.api.bie_management.data.expression;

import lombok.Data;

import java.util.Collections;
import java.util.Map;

@Data
public class GenerateExpressionOption {

    private Map<String, String> filenames = Collections.emptyMap();
    private Map<String, String> bizCtxIds = Collections.emptyMap();
    private boolean bieDefinition = true;
    private boolean bieGuid;
    private boolean bieCctsMetaData;
    private boolean businessContext;
    private boolean includeCctsDefinitionTag;
    private boolean includeWhoColumns;
    private boolean bieOagiScoreMetaData;
    private boolean basedCcMetaData;

    private String expressionOption;
    private String packageOption;

    private boolean includeBusinessContextInFilename;
    private boolean includeVersionInFilename;

    private boolean arrayForJsonExpression;
    private boolean includeMetaHeaderForJson;
    private String metaHeaderTopLevelAsbiepId;
    private boolean includePaginationResponseForJson;
    private String paginationResponseTopLevelAsbiepId;

    private String openAPIExpressionFormat;
    private boolean openAPICodeGenerationFriendly;
    private boolean openAPI30GetTemplate;
    private boolean arrayForJsonExpressionForOpenAPI30GetTemplate;
    private boolean includeMetaHeaderForJsonForOpenAPI30GetTemplate;
    private String metaHeaderTopLevelAsbiepIdForOpenAPI30GetTemplate;
    private boolean includePaginationResponseForJsonForOpenAPI30GetTemplate;
    private String paginationResponseTopLevelAsbiepIdForOpenAPI30GetTemplate;

    private boolean isOnlyBCCPsForOpenDocumentFormat = true;
    private String odfExpressionFormat = "ODS";

    public boolean hasOpenAPI30GetTemplateOptions() {
        if (arrayForJsonExpressionForOpenAPI30GetTemplate) {
            return true;
        }
        if (includeMetaHeaderForJsonForOpenAPI30GetTemplate) {
            return true;
        }
        if (includePaginationResponseForJsonForOpenAPI30GetTemplate) {
            return true;
        }
        return false;
    }

    private boolean openAPI30PostTemplate;
    private boolean arrayForJsonExpressionForOpenAPI30PostTemplate;
    private boolean includeMetaHeaderForJsonForOpenAPI30PostTemplate;
    private String metaHeaderTopLevelAsbiepIdForOpenAPI30PostTemplate;

    public boolean hasOpenAPI30PostTemplateOptions() {
        if (arrayForJsonExpressionForOpenAPI30PostTemplate) {
            return true;
        }
        if (includeMetaHeaderForJsonForOpenAPI30PostTemplate) {
            return true;
        }
        return false;
    }
}
