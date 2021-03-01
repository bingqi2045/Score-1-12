package org.oagi.score.gateway.http.api.module_management.service;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.oagi.score.export.ExportContext;
import org.oagi.score.export.ExportContextBuilder;
import org.oagi.score.export.impl.DefaultExportContextBuilder;
import org.oagi.score.export.impl.XMLExportSchemaModuleVisitor;
import org.oagi.score.export.model.SchemaModule;
import org.oagi.score.gateway.http.helper.Zip;
import org.oagi.score.repo.api.ScoreRepositoryFactory;
import org.oagi.score.repo.api.module.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Transactional(readOnly = true)
public class ModuleSetReleaseService {


    @Autowired
    private ScoreRepositoryFactory scoreRepositoryFactory;

    @Autowired
    private  DefaultExportContextBuilder defaultExportContextBuilder;

    @Autowired
    private XMLExportSchemaModuleVisitor visitor;

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

    public File exportModuleSetRelease(BigInteger moduleSetReleaseId) throws Exception {
        ExportContext exportContext = defaultExportContextBuilder.build(moduleSetReleaseId);

        List<File> files = new ArrayList<>();

        for (SchemaModule schemaModule : exportContext.getSchemaModules()) {
            visitor.setBaseDirectory(new File("./data"));
            schemaModule.visit(visitor);
            File file = visitor.endSchemaModule(schemaModule);
            if (file != null) {
                files.add(file);
            }
        }

        return Zip.compression(files, "test.zip");
    }
}
