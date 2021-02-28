package org.oagi.score.gateway.http.api.module_management.service;

import org.oagi.score.export.ExportContext;
import org.oagi.score.export.ExportContextBuilder;
import org.oagi.score.export.impl.DefaultExportContextBuilder;
import org.oagi.score.export.impl.XMLExportSchemaModuleVisitor;
import org.oagi.score.export.model.SchemaModule;
import org.oagi.score.repo.api.ScoreRepositoryFactory;
import org.oagi.score.repo.api.module.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.math.BigInteger;

@Service
@Transactional(readOnly = true)
public class ModuleSetReleaseService {


    @Autowired
    private ScoreRepositoryFactory scoreRepositoryFactory;

    public GetModuleSetReleaseListResponse getModuleSetReleaseList(GetModuleSetReleaseListRequest request) {
        return scoreRepositoryFactory.createModuleSetReleaseReadRepository().getModuleSetReleaseList(request);
    }

    public GetModuleSetReleaseResponse getModuleSetRelease(GetModuleSetReleaseRequest request) {
        return scoreRepositoryFactory.createModuleSetReleaseReadRepository().getModuleSetRelease(request);
    }

    @Transactional
    public CreateModuleSetReleaseResponse createModuleSetRelease(CreateModuleSetReleaseRequest request) {
        return scoreRepositoryFactory.createModuleSetReleaseWriteRepository().createModuleSetRelease(request);
    }

    @Transactional
    public UpdateModuleSetReleaseResponse updateModuleSetRelease(UpdateModuleSetReleaseRequest request) {
        return scoreRepositoryFactory.createModuleSetReleaseWriteRepository().updateModuleSetRelease(request);
    }

    @Transactional
    public DeleteModuleSetReleaseResponse discardModuleSetRelease(DeleteModuleSetReleaseRequest request) {
        return scoreRepositoryFactory.createModuleSetReleaseWriteRepository().deleteModuleSetRelease(request);
    }

    public void exportModuleSetRelease(BigInteger moduleSetReleaseId) throws Exception {

        ExportContextBuilder exportContextBuilder = new DefaultExportContextBuilder();
        ExportContext exportContext = exportContextBuilder.build(moduleSetReleaseId);
        XMLExportSchemaModuleVisitor visitor = new XMLExportSchemaModuleVisitor();
        for (SchemaModule schemaModule : exportContext.getSchemaModules()) {
            visitor.setBaseDirectory(new File("./data"));
            schemaModule.visit(visitor);
        }
    }
}
