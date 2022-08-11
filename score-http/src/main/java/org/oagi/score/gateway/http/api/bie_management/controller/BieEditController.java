package org.oagi.score.gateway.http.api.bie_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.oagi.score.gateway.http.api.bie_management.data.BieEvent;
import org.oagi.score.gateway.http.api.bie_management.data.bie_edit.*;
import org.oagi.score.gateway.http.api.bie_management.data.bie_edit.tree.BieEditAbieNode;
import org.oagi.score.gateway.http.api.bie_management.data.bie_edit.tree.BieEditAsbiepNode;
import org.oagi.score.gateway.http.api.bie_management.data.bie_edit.tree.BieEditRef;
import org.oagi.score.gateway.http.api.bie_management.service.BieCreateFromExistingBieService;
import org.oagi.score.gateway.http.api.bie_management.service.BieEditService;
import org.oagi.score.gateway.http.api.bie_management.service.BieService;
import org.oagi.score.repo.api.bie.model.BieState;
import org.oagi.score.gateway.http.configuration.security.SessionService;
import org.oagi.score.repo.component.abie.AbieNode;
import org.oagi.score.repo.component.agency_id_list.AvailableAgencyIdList;
import org.oagi.score.repo.component.asbie.AsbieNode;
import org.oagi.score.repo.component.asbiep.AsbiepNode;
import org.oagi.score.repo.component.bbie.BbieNode;
import org.oagi.score.repo.component.bbie_sc.BbieScNode;
import org.oagi.score.repo.component.bbiep.BbiepNode;
import org.oagi.score.repo.component.bdt_pri_restri.AvailableBdtPriRestri;
import org.oagi.score.repo.component.bdt_sc_pri_restri.AvailableBdtScPriRestri;
import org.oagi.score.repo.component.code_list.AvailableCodeList;
import org.oagi.score.repo.component.dt.BdtNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
public class BieEditController {

    @Autowired
    private BieService bieService;

    @Autowired
    private BieEditService bieEditService;

    @Autowired
    private BieCreateFromExistingBieService createBieFromExistingBieService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/profile_bie/node/root/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BieEditNode getRootNode(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                   @PathVariable("id") BigInteger topLevelAsbiepId) {
        BieEditAbieNode rootNode = bieEditService.getRootNode(user, topLevelAsbiepId);
        return rootNode;
    }

    @RequestMapping(value = "/profile_bie/{topLevelAsbiepId}/abie/{manifestId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public AbieNode getAbieDetail(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                  @PathVariable("topLevelAsbiepId") BigInteger topLevelAsbiepId,
                                  @PathVariable("manifestId") BigInteger manifestId,
                                  @RequestParam("hashPath") String hashPath) {
        return bieEditService.getAbieDetail(user, topLevelAsbiepId, manifestId, hashPath);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAsbiepId}/asbie/{manifestId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public AsbieNode getAsbieDetail(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                    @PathVariable("topLevelAsbiepId") BigInteger topLevelAsbiepId,
                                    @PathVariable("manifestId") BigInteger manifestId,
                                    @RequestParam("hashPath") String hashPath) {
        return bieEditService.getAsbieDetail(user, topLevelAsbiepId, manifestId, hashPath);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAsbiepId}/bbie/{manifestId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BbieNode getBbieDetail(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                  @PathVariable("topLevelAsbiepId") BigInteger topLevelAsbiepId,
                                  @PathVariable("manifestId") BigInteger manifestId,
                                  @RequestParam("hashPath") String hashPath) {
        return bieEditService.getBbieDetail(user, topLevelAsbiepId, manifestId, hashPath);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAsbiepId}/asbiep/{manifestId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public AsbiepNode getAsbiepDetail(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                      @PathVariable("topLevelAsbiepId") BigInteger topLevelAsbiepId,
                                      @PathVariable("manifestId") BigInteger manifestId,
                                      @RequestParam("hashPath") String hashPath) {
        return bieEditService.getAsbiepDetail(user, topLevelAsbiepId, manifestId, hashPath);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAsbiepId}/bbiep/{manifestId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BbiepNode getBbiepDetail(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                    @PathVariable("topLevelAsbiepId") BigInteger topLevelAsbiepId,
                                    @PathVariable("manifestId") BigInteger manifestId,
                                    @RequestParam("hashPath") String hashPath) {
        return bieEditService.getBbiepDetail(user, topLevelAsbiepId, manifestId, hashPath);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAsbiepId}/bbiep/{manifestId}/bdt_pri_restri",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AvailableBdtPriRestri> availableBdtPriRestriListByBccpManifestId(
            @AuthenticationPrincipal AuthenticatedPrincipal user,
            @PathVariable("topLevelAsbiepId") BigInteger topLevelAsbiepId,
            @PathVariable("manifestId") BigInteger manifestId) {
        return bieEditService.availableBdtPriRestriListByBccpManifestId(user, topLevelAsbiepId, manifestId);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAsbiepId}/bbiep/{manifestId}/code_list",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AvailableCodeList> availableCodeListListByBccpManifestId(
            @AuthenticationPrincipal AuthenticatedPrincipal user,
            @PathVariable("topLevelAsbiepId") BigInteger topLevelAsbiepId,
            @PathVariable("manifestId") BigInteger manifestId) {
        return bieEditService.availableCodeListListByBccpManifestId(user, topLevelAsbiepId, manifestId);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAsbiepId}/bbiep/{manifestId}/agency_id_list",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AvailableAgencyIdList> availableAgencyIdListListByBccpManifestId(
            @AuthenticationPrincipal AuthenticatedPrincipal user,
            @PathVariable("topLevelAsbiepId") BigInteger topLevelAsbiepId,
            @PathVariable("manifestId") BigInteger manifestId) {
        return bieEditService.availableAgencyIdListListByBccpManifestId(user, topLevelAsbiepId, manifestId);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAsbiepId}/bbie_sc/{manifestId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BbieScNode getBbieScDetail(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                      @PathVariable("topLevelAsbiepId") BigInteger topLevelAsbiepId,
                                      @PathVariable("manifestId") BigInteger manifestId,
                                      @RequestParam("hashPath") String hashPath) {
        return bieEditService.getBbieScDetail(user, topLevelAsbiepId, manifestId, hashPath);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAsbiepId}/bbie_sc/{manifestId}/bdt_sc_pri_restri",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AvailableBdtScPriRestri> availableBdtScPriRestriListByBdtScManifestId(
            @AuthenticationPrincipal AuthenticatedPrincipal user,
            @PathVariable("topLevelAsbiepId") BigInteger topLevelAsbiepId,
            @PathVariable("manifestId") BigInteger manifestId) {
        return bieEditService.availableBdtScPriRestriListByBdtScManifestId(user, topLevelAsbiepId, manifestId);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAsbiepId}/bbie_sc/{manifestId}/code_list",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AvailableCodeList> availableCodeListListByBdtScManifestId(
            @AuthenticationPrincipal AuthenticatedPrincipal user,
            @PathVariable("topLevelAsbiepId") BigInteger topLevelAsbiepId,
            @PathVariable("manifestId") BigInteger manifestId) {
        return bieEditService.availableCodeListListByBdtScManifestId(user, topLevelAsbiepId, manifestId);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAsbiepId}/bbie_sc/{manifestId}/agency_id_list",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AvailableAgencyIdList> availableAgencyIdListListByBdtScManifestId(
            @AuthenticationPrincipal AuthenticatedPrincipal user,
            @PathVariable("topLevelAsbiepId") BigInteger topLevelAsbiepId,
            @PathVariable("manifestId") BigInteger manifestId) {
        return bieEditService.availableAgencyIdListListByBdtScManifestId(user, topLevelAsbiepId, manifestId);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAsbiepId}/dt/{manifestId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BdtNode getBdtDetail(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                @PathVariable("manifestId") BigInteger manifestId,
                                @PathVariable("topLevelAsbiepId") BigInteger topLevelAsbiepId) {
        return bieEditService.getBdtDetail(user, topLevelAsbiepId, manifestId);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAsbiepId}/used_list",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BieEditUsed> getUsedAbieList(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                             @PathVariable("topLevelAsbiepId") BigInteger topLevelAsbiepId) {
        return bieEditService.getBieUsedList(user, topLevelAsbiepId);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAsbiepId}/ref_list",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BieEditRef> getRefAsbieList(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                            @PathVariable("topLevelAsbiepId") BigInteger topLevelAsbiepId) {
        return bieEditService.getBieRefList(user, topLevelAsbiepId);
    }

    @RequestMapping(value = "/profile_bie/node/root/{id}/state", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateState(@AuthenticationPrincipal AuthenticatedPrincipal user,
                            @PathVariable("id") BigInteger topLevelAsbiepId,
                            @RequestBody Map<String, Object> body) {
        BieState state = BieState.valueOf((String) body.get("state"));
        bieEditService.updateState(user, topLevelAsbiepId, state);

        BieEvent event = new BieEvent();
        event.setAction("UpdateState");
        event.setTopLevelAsbiepId(topLevelAsbiepId);
        event.addProperty("actor", user.getName());
        event.addProperty("state", state);
        event.addProperty("timestamp", LocalDateTime.now());
        bieService.fireBieEvent(event);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAsbiepId}/detail", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BieEditUpdateDetailResponse updateDetails(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                                     @PathVariable("topLevelAsbiepId") BigInteger topLevelAsbiepId,
                                                     @RequestBody BieEditUpdateDetailRequest request) {
        request.setTopLevelAsbiepId(topLevelAsbiepId);
        BieEditUpdateDetailResponse response = bieEditService.updateDetails(user, request);

        BieEvent event = new BieEvent();
        event.setAction("UpdateDetails");
        event.setTopLevelAsbiepId(topLevelAsbiepId);
        event.addProperty("actor", user.getName());
        if (!response.getAbieDetailMap().isEmpty()) {
            event.addProperty("abieDetailMap", response.getAbieDetailMap());
        }
        if (!response.getAsbieDetailMap().isEmpty()) {
            event.addProperty("asbieDetailMap", response.getAsbieDetailMap());
        }
        if (!response.getBbieDetailMap().isEmpty()) {
            event.addProperty("bbieDetailMap", response.getBbieDetailMap());
        }
        if (!response.getAsbiepDetailMap().isEmpty()) {
            event.addProperty("asbiepDetailMap", response.getAsbiepDetailMap());
        }
        if (!response.getBbiepDetailMap().isEmpty()) {
            event.addProperty("bbiepDetailMap", response.getBbiepDetailMap());
        }
        if (!response.getBbieScDetailMap().isEmpty()) {
            event.addProperty("bbieScDetailMap", response.getBbieScDetailMap());
        }
        event.addProperty("timestamp", response.getTimestamp());
        bieService.fireBieEvent(event);

        return response;
    }

    @RequestMapping(value = "/profile_bie/node/extension/local", method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CreateExtensionResponse createLocalAbieExtension(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                                            @RequestBody BieEditAsbiepNode extensionNode) {
        CreateExtensionResponse response = bieEditService.createLocalAbieExtension(user, extensionNode);
        return response;
    }

    @RequestMapping(value = "/profile_bie/node/extension/global", method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CreateExtensionResponse createGlobalAbieExtension(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                                             @RequestBody BieEditAsbiepNode extensionNode) {
        CreateExtensionResponse response = bieEditService.createGlobalAbieExtension(user, extensionNode);
        return response;
    }

    /* Reuse BIE */

    @RequestMapping(value = "/profile_bie/{topLevelAsbiepId}/asbiep/{manifestId}/reuse",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void reuseBIE(@AuthenticationPrincipal AuthenticatedPrincipal user,
                         @PathVariable("topLevelAsbiepId") BigInteger topLevelAsbiepId,
                         @PathVariable("manifestId") BigInteger manifestId,
                         @RequestBody ReuseBIERequest reuseBIERequest) {

        reuseBIERequest.setTopLevelAsbiepId(topLevelAsbiepId);
        reuseBIERequest.setAsccpManifestId(manifestId);
        bieEditService.reuseBIE(user, reuseBIERequest);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAsbiepId}/remove_reuse",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void reuseBIE(@AuthenticationPrincipal AuthenticatedPrincipal user,
                         @PathVariable("topLevelAsbiepId") BigInteger topLevelAsbiepId,
                         @RequestBody RemoveReusedBIERequest removeReusedBIERequest) {

        removeReusedBIERequest.setTopLevelAsbiepId(topLevelAsbiepId);
        bieEditService.removeReusedBIE(user, removeReusedBIERequest);
    }

    @RequestMapping(value = "/profile_bie/node/create_bie_from_existing_bie",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void createBieFromExistingBie(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                         @RequestBody CreateBieFromExistingBieRequest request) {

        createBieFromExistingBieService.createBieFromExistingBie(user, request);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAsbiepId}/reset_detail",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void resetDetailBIE(@AuthenticationPrincipal AuthenticatedPrincipal user,
                         @PathVariable("topLevelAsbiepId") BigInteger topLevelAsbiepId,
                         @RequestBody ResetDetailBIERequest request) {

        request.setTopLevelAsbiepId(topLevelAsbiepId);
        bieEditService.resetDetailBIE(user, request);
    }
}
