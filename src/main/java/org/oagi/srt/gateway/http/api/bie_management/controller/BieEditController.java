package org.oagi.srt.gateway.http.api.bie_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.oagi.srt.data.BieState;
import org.oagi.srt.gateway.http.api.bie_management.data.TopLevelAsbiepRequest;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.*;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.tree.BieEditAbieNode;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.tree.BieEditAsbiepNode;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.tree.BieEditRef;
import org.oagi.srt.gateway.http.api.bie_management.service.BieCreateFromExistingBieService;
import org.oagi.srt.gateway.http.api.bie_management.service.BieEditService;
import org.oagi.srt.gateway.http.api.common.data.AccessPrivilege;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.repo.component.abie.AbieNode;
import org.oagi.srt.repo.component.agency_id_list.AvailableAgencyIdList;
import org.oagi.srt.repo.component.asbie.AsbieNode;
import org.oagi.srt.repo.component.asbiep.AsbiepNode;
import org.oagi.srt.repo.component.bbie.BbieNode;
import org.oagi.srt.repo.component.bbie_sc.BbieScNode;
import org.oagi.srt.repo.component.bbiep.BbiepNode;
import org.oagi.srt.repo.component.bdt_pri_restri.AvailableBdtPriRestri;
import org.oagi.srt.repo.component.bdt_sc_pri_restri.AvailableBdtScPriRestri;
import org.oagi.srt.repo.component.code_list.AvailableCodeList;
import org.oagi.srt.repo.component.dt.BdtNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.*;

import static org.oagi.srt.gateway.http.api.common.data.AccessPrivilege.*;

@RestController
public class BieEditController {

    @Autowired
    private BieEditService service;

    @Autowired
    private BieCreateFromExistingBieService createBieFromExistingBieService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/profile_bie/node/root/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BieEditNode getRootNode(@AuthenticationPrincipal User user,
                                   @PathVariable("id") BigInteger topLevelAsbiepId) {
        BieEditAbieNode rootNode = service.getRootNode(user, topLevelAsbiepId);
        BieState state = rootNode.getTopLevelAsbiepState();
        rootNode.setTopLevelAsbiepState(state);

        BigInteger userId = sessionService.userId(user);
        AccessPrivilege accessPrivilege = Prohibited;
        switch (state) {
            case Initiating:
                accessPrivilege = Unprepared;
                break;

            case WIP:
                if (rootNode.getOwnerUserId().equals(userId)) {
                    accessPrivilege = CanEdit;
                } else {
                    accessPrivilege = Prohibited;
                }
                break;

            case QA:
                if (rootNode.getOwnerUserId().equals(userId)) {
                    accessPrivilege = CanMove;
                } else {
                    accessPrivilege = CanView;
                }

                break;

            case Production:
                accessPrivilege = CanView;
                break;
        }

        rootNode.setAccess(accessPrivilege.name());

        return rootNode;
    }

    @RequestMapping(value = "/profile_bie/node/root/{id}", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BieEditNode updateRootNode(@AuthenticationPrincipal User user,
                                      @PathVariable("id") BigInteger topLevelAsbiepId,
                                      @RequestBody TopLevelAsbiepRequest request) {

        return service.updateRootNode(user, request);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAsbiepId}/abie/{manifestId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public AbieNode getAbieDetail(@AuthenticationPrincipal User user,
                                  @PathVariable("topLevelAsbiepId") BigInteger topLevelAsbiepId,
                                  @PathVariable("manifestId") BigInteger manifestId,
                                  @RequestParam("hashPath") String hashPath) {
        return service.getAbieDetail(user, topLevelAsbiepId, manifestId, hashPath);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAsbiepId}/asbie/{manifestId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public AsbieNode getAsbieDetail(@AuthenticationPrincipal User user,
                                    @PathVariable("topLevelAsbiepId") BigInteger topLevelAsbiepId,
                                    @PathVariable("manifestId") BigInteger manifestId,
                                    @RequestParam("hashPath") String hashPath) {
        return service.getAsbieDetail(user, topLevelAsbiepId, manifestId, hashPath);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAsbiepId}/bbie/{manifestId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BbieNode getBbieDetail(@AuthenticationPrincipal User user,
                                  @PathVariable("topLevelAsbiepId") BigInteger topLevelAsbiepId,
                                  @PathVariable("manifestId") BigInteger manifestId,
                                  @RequestParam("hashPath") String hashPath) {
        return service.getBbieDetail(user, topLevelAsbiepId, manifestId, hashPath);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAsbiepId}/asbiep/{manifestId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public AsbiepNode getAsbiepDetail(@AuthenticationPrincipal User user,
                                      @PathVariable("topLevelAsbiepId") BigInteger topLevelAsbiepId,
                                      @PathVariable("manifestId") BigInteger manifestId,
                                      @RequestParam("hashPath") String hashPath) {
        return service.getAsbiepDetail(user, topLevelAsbiepId, manifestId, hashPath);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAsbiepId}/bbiep/{manifestId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BbiepNode getBbiepDetail(@AuthenticationPrincipal User user,
                                    @PathVariable("topLevelAsbiepId") BigInteger topLevelAsbiepId,
                                    @PathVariable("manifestId") BigInteger manifestId,
                                    @RequestParam("hashPath") String hashPath) {
        return service.getBbiepDetail(user, topLevelAsbiepId, manifestId, hashPath);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAsbiepId}/bbiep/{manifestId}/bdt_pri_restri",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AvailableBdtPriRestri> availableBdtPriRestriListByBccpManifestId(
            @AuthenticationPrincipal User user,
            @PathVariable("topLevelAsbiepId") BigInteger topLevelAsbiepId,
            @PathVariable("manifestId") BigInteger manifestId) {
        return service.availableBdtPriRestriListByBccpManifestId(user, topLevelAsbiepId, manifestId);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAsbiepId}/bbiep/{manifestId}/code_list",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AvailableCodeList> availableCodeListListByBccpManifestId(
            @AuthenticationPrincipal User user,
            @PathVariable("topLevelAsbiepId") BigInteger topLevelAsbiepId,
            @PathVariable("manifestId") BigInteger manifestId) {
        return service.availableCodeListListByBccpManifestId(user, topLevelAsbiepId, manifestId);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAsbiepId}/bbiep/{manifestId}/agency_id_list",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AvailableAgencyIdList> availableAgencyIdListListByBccpManifestId(
            @AuthenticationPrincipal User user,
            @PathVariable("topLevelAsbiepId") BigInteger topLevelAsbiepId,
            @PathVariable("manifestId") BigInteger manifestId) {
        return service.availableAgencyIdListListByBccpManifestId(user, topLevelAsbiepId, manifestId);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAsbiepId}/bbie_sc/{manifestId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BbieScNode getBbieScDetail(@AuthenticationPrincipal User user,
                                      @PathVariable("topLevelAsbiepId") BigInteger topLevelAsbiepId,
                                      @PathVariable("manifestId") BigInteger manifestId,
                                      @RequestParam("hashPath") String hashPath) {
        return service.getBbieScDetail(user, topLevelAsbiepId, manifestId, hashPath);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAsbiepId}/bbie_sc/{manifestId}/bdt_sc_pri_restri",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AvailableBdtScPriRestri> availableBdtScPriRestriListByBdtScManifestId(
            @AuthenticationPrincipal User user,
            @PathVariable("topLevelAsbiepId") BigInteger topLevelAsbiepId,
            @PathVariable("manifestId") BigInteger manifestId) {
        return service.availableBdtScPriRestriListByBdtScManifestId(user, topLevelAsbiepId, manifestId);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAsbiepId}/bbie_sc/{manifestId}/code_list",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AvailableCodeList> availableCodeListListByBdtScManifestId(
            @AuthenticationPrincipal User user,
            @PathVariable("topLevelAsbiepId") BigInteger topLevelAsbiepId,
            @PathVariable("manifestId") BigInteger manifestId) {
        return service.availableCodeListListByBdtScManifestId(user, topLevelAsbiepId, manifestId);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAsbiepId}/bbie_sc/{manifestId}/agency_id_list",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AvailableAgencyIdList> availableAgencyIdListListByBdtScManifestId(
            @AuthenticationPrincipal User user,
            @PathVariable("topLevelAsbiepId") BigInteger topLevelAsbiepId,
            @PathVariable("manifestId") BigInteger manifestId) {
        return service.availableAgencyIdListListByBdtScManifestId(user, topLevelAsbiepId, manifestId);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAsbiepId}/bdt/{manifestId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BdtNode getBdtDetail(@AuthenticationPrincipal User user,
                                @PathVariable("manifestId") BigInteger manifestId,
                                @PathVariable("topLevelAsbiepId") BigInteger topLevelAsbiepId) {
        return service.getBdtDetail(user, topLevelAsbiepId, manifestId);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAsbiepId}/used_list",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BieEditUsed> getUsedAbieList(@AuthenticationPrincipal User user,
                                             @PathVariable("topLevelAsbiepId") BigInteger topLevelAsbiepId) {
        return service.getBieUsedList(user, topLevelAsbiepId);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAsbiepId}/ref_list",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BieEditRef> getRefAsbieList(@AuthenticationPrincipal User user,
                                           @PathVariable("topLevelAsbiepId") BigInteger topLevelAsbiepId) {
        return service.getBieRefList(user, topLevelAsbiepId);
    }

    @RequestMapping(value = "/profile_bie/node/root/{id}/state", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateState(@AuthenticationPrincipal User user,
                            @PathVariable("id") BigInteger topLevelAsbiepId,
                            @RequestBody Map<String, Object> body) {
        BieState state = BieState.valueOf((String) body.get("state"));
        service.updateState(user, topLevelAsbiepId, state);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAsbiepId}/detail", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BieEditUpdateDetailResponse updateDetails(@AuthenticationPrincipal User user,
                                                     @PathVariable("topLevelAsbiepId") BigInteger topLevelAsbiepId,
                                                     @RequestBody BieEditUpdateDetailRequest request) {
        request.setTopLevelAsbiepId(topLevelAsbiepId);
        return service.updateDetails(user, request);
    }

    @RequestMapping(value = "/profile_bie/node/extension/local", method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CreateExtensionResponse createLocalAbieExtension(@AuthenticationPrincipal User user,
                                                            @RequestBody BieEditAsbiepNode extensionNode) {
        CreateExtensionResponse response = service.createLocalAbieExtension(user, extensionNode);
        return response;
    }

    @RequestMapping(value = "/profile_bie/node/extension/global", method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CreateExtensionResponse createGlobalAbieExtension(@AuthenticationPrincipal User user,
                                                             @RequestBody BieEditAsbiepNode extensionNode) {
        CreateExtensionResponse response = service.createGlobalAbieExtension(user, extensionNode);
        return response;
    }

    /* Reuse BIE */

    @RequestMapping(value = "/profile_bie/{topLevelAsbiepId}/asbiep/{manifestId}/reuse",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void reuseBIE(@AuthenticationPrincipal User user,
                         @PathVariable("topLevelAsbiepId") BigInteger topLevelAsbiepId,
                         @PathVariable("manifestId") BigInteger manifestId,
                         @RequestBody ReuseBIERequest reuseBIERequest) {

        reuseBIERequest.setTopLevelAsbiepId(topLevelAsbiepId);
        reuseBIERequest.setAsccpManifestId(manifestId);
        service.reuseBIE(user, reuseBIERequest);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAsbiepId}/asbiep/{manifestId}/remove_reuse",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void reuseBIE(@AuthenticationPrincipal User user,
                         @PathVariable("topLevelAsbiepId") BigInteger topLevelAsbiepId,
                         @PathVariable("manifestId") BigInteger manifestId,
                         @RequestBody RemoveReusedBIERequest removeReusedBIERequest) {

        removeReusedBIERequest.setTopLevelAsbiepId(topLevelAsbiepId);
        removeReusedBIERequest.setAsccpManifestId(manifestId);
        service.removeReusedBIE(user, removeReusedBIERequest);
    }

    @RequestMapping(value = "/profile_bie/node/create_bie_from_existing_bie",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void createBieFromExistingBie(@AuthenticationPrincipal User user,
                                         @RequestBody CreateBieFromExistingBieRequest request) {

        createBieFromExistingBieService.createBieFromExistingBie(user, request);
    }
}
