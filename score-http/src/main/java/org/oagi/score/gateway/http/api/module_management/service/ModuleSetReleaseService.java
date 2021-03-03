package org.oagi.score.gateway.http.api.module_management.service;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.oagi.score.export.ExportContext;
import org.oagi.score.export.ExportContextBuilder;
import org.oagi.score.export.impl.DefaultExportContextBuilder;
import org.oagi.score.export.impl.XMLExportSchemaModuleVisitor;
import org.oagi.score.export.model.SchemaModule;
import org.oagi.score.gateway.http.api.module_management.data.ModuleAssignComponents;
import org.oagi.score.gateway.http.api.release_management.data.AssignComponents;
import org.oagi.score.gateway.http.helper.Zip;
import org.oagi.score.repo.api.ScoreRepositoryFactory;
import org.oagi.score.repo.api.module.model.*;
import org.oagi.score.repo.api.user.model.ScoreUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
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

    public ModuleAssignComponents getAssignableCCs(GetAssignableCCListRequest request) {
        ModuleAssignComponents assignComponents = new ModuleAssignComponents();

        GetModuleSetReleaseRequest moduleSetReleaseRequest = new GetModuleSetReleaseRequest(request.getRequester());
        moduleSetReleaseRequest.setModuleSetReleaseId(request.getModuleSetReleaseId());
        GetModuleSetReleaseResponse moduleSetReleaseResponse = scoreRepositoryFactory.createModuleSetReleaseReadRepository().getModuleSetRelease(moduleSetReleaseRequest);
        request.setReleaseId(moduleSetReleaseResponse.getModuleSetRelease().getReleaseId());
        List<AssignableNode> accList = scoreRepositoryFactory.createModuleSetReleaseReadRepository().getAssignableACCByModuleSetReleaseId(request);
        List<AssignableNode> asccpList = scoreRepositoryFactory.createModuleSetReleaseReadRepository().getAssignableASCCPByModuleSetReleaseId(request);
        List<AssignableNode> bccpList = scoreRepositoryFactory.createModuleSetReleaseReadRepository().getAssignableBCCPByModuleSetReleaseId(request);
        List<AssignableNode> dtList = scoreRepositoryFactory.createModuleSetReleaseReadRepository().getAssignableDTByModuleSetReleaseId(request);
        List<AssignableNode> codeListList = scoreRepositoryFactory.createModuleSetReleaseReadRepository().getAssignableCodeListByModuleSetReleaseId(request);
        List<AssignableNode> agencyIdListList = scoreRepositoryFactory.createModuleSetReleaseReadRepository().getAssignableAgencyIdListByModuleSetReleaseId(request);
        List<AssignableNode> xbtList = scoreRepositoryFactory.createModuleSetReleaseReadRepository().getAssignableXBTByModuleSetReleaseId(request);
        assignComponents.setAssignableAccManifestMap(accList.stream().collect(Collectors.toMap(AssignableNode::getManifestId, Function.identity())));
        assignComponents.setAssignableAsccpManifestMap(asccpList.stream().collect(Collectors.toMap(AssignableNode::getManifestId, Function.identity())));
        assignComponents.setAssignableBccpManifestMap(bccpList.stream().collect(Collectors.toMap(AssignableNode::getManifestId, Function.identity())));
        assignComponents.setAssignableDtManifestMap(dtList.stream().collect(Collectors.toMap(AssignableNode::getManifestId, Function.identity())));
        assignComponents.setAssignableCodeListManifestMap(codeListList.stream().collect(Collectors.toMap(AssignableNode::getManifestId, Function.identity())));
        assignComponents.setAssignableAgencyIdListManifestMap(agencyIdListList.stream().collect(Collectors.toMap(AssignableNode::getManifestId, Function.identity())));
        assignComponents.setAssignableXbtManifestMap(xbtList.stream().collect(Collectors.toMap(AssignableNode::getManifestId, Function.identity())));

        return assignComponents;
    }

    public ModuleAssignComponents getAssignedCCs(ScoreUser user, BigInteger moduleSetReleaseId, BigInteger moduleId) {
        ModuleAssignComponents assignComponents = new ModuleAssignComponents();
        GetAssignedCCListRequest request = new GetAssignedCCListRequest(user);
        request.setModuleSetReleaseId(moduleSetReleaseId);
        request.setModuleId(moduleId);
        List<AssignableNode> accList = scoreRepositoryFactory.createModuleSetReleaseReadRepository().getAssignedACCByModuleSetReleaseId(request);
        List<AssignableNode> asccpList = scoreRepositoryFactory.createModuleSetReleaseReadRepository().getAssignedASCCPByModuleSetReleaseId(request);
        List<AssignableNode> bccpList = scoreRepositoryFactory.createModuleSetReleaseReadRepository().getAssignedBCCPByModuleSetReleaseId(request);
        List<AssignableNode> dtList = scoreRepositoryFactory.createModuleSetReleaseReadRepository().getAssignedDTByModuleSetReleaseId(request);
        List<AssignableNode> codeListList = scoreRepositoryFactory.createModuleSetReleaseReadRepository().getAssignedCodeListByModuleSetReleaseId(request);
        List<AssignableNode> agencyIdListList = scoreRepositoryFactory.createModuleSetReleaseReadRepository().getAssignedAgencyIdListByModuleSetReleaseId(request);
        List<AssignableNode> xbtList = scoreRepositoryFactory.createModuleSetReleaseReadRepository().getAssignedXBTByModuleSetReleaseId(request);
        assignComponents.setAssignedAccManifestMap(accList.stream().collect(Collectors.toMap(AssignableNode::getManifestId, Function.identity())));
        assignComponents.setAssignedAsccpManifestMap(asccpList.stream().collect(Collectors.toMap(AssignableNode::getManifestId, Function.identity())));
        assignComponents.setAssignedBccpManifestMap(bccpList.stream().collect(Collectors.toMap(AssignableNode::getManifestId, Function.identity())));
        assignComponents.setAssignedDtManifestMap(dtList.stream().collect(Collectors.toMap(AssignableNode::getManifestId, Function.identity())));
        assignComponents.setAssignedCodeListManifestMap(codeListList.stream().collect(Collectors.toMap(AssignableNode::getManifestId, Function.identity())));
        assignComponents.setAssignedAgencyIdListManifestMap(agencyIdListList.stream().collect(Collectors.toMap(AssignableNode::getManifestId, Function.identity())));
        assignComponents.setAssignedXbtManifestMap(xbtList.stream().collect(Collectors.toMap(AssignableNode::getManifestId, Function.identity())));

        return assignComponents;
    }
}
