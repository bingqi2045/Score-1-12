package org.oagi.srt.gateway.http.api.bie_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.oagi.srt.data.BieState;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.*;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.tree.BieEditAbieNode;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.tree.BieEditAsbiepNode;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.tree.BieEditRef;
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
    private SessionService sessionService;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/profile_bie/node/root/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BieEditNode getRootNode(@AuthenticationPrincipal User user,
                                   @PathVariable("id") BigInteger topLevelAbieId) {
        BieEditAbieNode rootNode = service.getRootNode(user, topLevelAbieId);
        BieState state = BieState.valueOf((Integer) rootNode.getTopLevelAbieState());
        rootNode.setTopLevelAbieState(state.toString());

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

    @RequestMapping(value = "/profile_bie/{topLevelAbieId}/abie/{manifestId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public AbieNode getAbieDetail(@AuthenticationPrincipal User user,
                                  @PathVariable("topLevelAbieId") BigInteger topLevelAbieId,
                                  @PathVariable("manifestId") BigInteger manifestId,
                                  @RequestParam("hashPath") String hashPath) {
        return service.getAbieDetail(user, topLevelAbieId, manifestId, hashPath);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAbieId}/asbie/{manifestId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public AsbieNode getAsbieDetail(@AuthenticationPrincipal User user,
                                    @PathVariable("topLevelAbieId") BigInteger topLevelAbieId,
                                    @PathVariable("manifestId") BigInteger manifestId,
                                    @RequestParam("hashPath") String hashPath) {
        return service.getAsbieDetail(user, topLevelAbieId, manifestId, hashPath);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAbieId}/bbie/{manifestId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BbieNode getBbieDetail(@AuthenticationPrincipal User user,
                                  @PathVariable("topLevelAbieId") BigInteger topLevelAbieId,
                                  @PathVariable("manifestId") BigInteger manifestId,
                                  @RequestParam("hashPath") String hashPath) {
        return service.getBbieDetail(user, topLevelAbieId, manifestId, hashPath);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAbieId}/asbiep/{manifestId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public AsbiepNode getAsbiepDetail(@AuthenticationPrincipal User user,
                                      @PathVariable("topLevelAbieId") BigInteger topLevelAbieId,
                                      @PathVariable("manifestId") BigInteger manifestId,
                                      @RequestParam("hashPath") String hashPath) {
        return service.getAsbiepDetail(user, topLevelAbieId, manifestId, hashPath);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAbieId}/bbiep/{manifestId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BbiepNode getBbiepDetail(@AuthenticationPrincipal User user,
                                    @PathVariable("topLevelAbieId") BigInteger topLevelAbieId,
                                    @PathVariable("manifestId") BigInteger manifestId,
                                    @RequestParam("hashPath") String hashPath) {
        return service.getBbiepDetail(user, topLevelAbieId, manifestId, hashPath);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAbieId}/bbiep/{manifestId}/bdt_pri_restri",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AvailableBdtPriRestri> availableBdtPriRestriListByBccpManifestId(
            @AuthenticationPrincipal User user,
            @PathVariable("topLevelAbieId") BigInteger topLevelAbieId,
            @PathVariable("manifestId") BigInteger manifestId) {
        return service.availableBdtPriRestriListByBccpManifestId(user, topLevelAbieId, manifestId);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAbieId}/bbiep/{manifestId}/code_list",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AvailableCodeList> availableCodeListListByBccpManifestId(
            @AuthenticationPrincipal User user,
            @PathVariable("topLevelAbieId") BigInteger topLevelAbieId,
            @PathVariable("manifestId") BigInteger manifestId) {
        return service.availableCodeListListByBccpManifestId(user, topLevelAbieId, manifestId);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAbieId}/bbiep/{manifestId}/agency_id_list",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AvailableAgencyIdList> availableAgencyIdListListByBccpManifestId(
            @AuthenticationPrincipal User user,
            @PathVariable("topLevelAbieId") BigInteger topLevelAbieId,
            @PathVariable("manifestId") BigInteger manifestId) {
        return service.availableAgencyIdListListByBccpManifestId(user, topLevelAbieId, manifestId);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAbieId}/bbie_sc/{manifestId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BbieScNode getBbieScDetail(@AuthenticationPrincipal User user,
                                      @PathVariable("topLevelAbieId") BigInteger topLevelAbieId,
                                      @PathVariable("manifestId") BigInteger manifestId,
                                      @RequestParam("hashPath") String hashPath) {
        return service.getBbieScDetail(user, topLevelAbieId, manifestId, hashPath);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAbieId}/bbie_sc/{manifestId}/bdt_sc_pri_restri",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AvailableBdtScPriRestri> availableBdtScPriRestriListByBdtScManifestId(
            @AuthenticationPrincipal User user,
            @PathVariable("topLevelAbieId") BigInteger topLevelAbieId,
            @PathVariable("manifestId") BigInteger manifestId) {
        return service.availableBdtScPriRestriListByBdtScManifestId(user, topLevelAbieId, manifestId);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAbieId}/bbie_sc/{manifestId}/code_list",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AvailableCodeList> availableCodeListListByBdtScManifestId(
            @AuthenticationPrincipal User user,
            @PathVariable("topLevelAbieId") BigInteger topLevelAbieId,
            @PathVariable("manifestId") BigInteger manifestId) {
        return service.availableCodeListListByBdtScManifestId(user, topLevelAbieId, manifestId);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAbieId}/bbie_sc/{manifestId}/agency_id_list",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AvailableAgencyIdList> availableAgencyIdListListByBdtScManifestId(
            @AuthenticationPrincipal User user,
            @PathVariable("topLevelAbieId") BigInteger topLevelAbieId,
            @PathVariable("manifestId") BigInteger manifestId) {
        return service.availableAgencyIdListListByBdtScManifestId(user, topLevelAbieId, manifestId);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAbieId}/bdt/{manifestId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BdtNode getBdtDetail(@AuthenticationPrincipal User user,
                                @PathVariable("manifestId") BigInteger manifestId,
                                @PathVariable("topLevelAbieId") BigInteger topLevelAbieId) {
        return service.getBdtDetail(user, topLevelAbieId, manifestId);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAbieId}/used_list",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, BieEditUsed> getUsedAbieList(@AuthenticationPrincipal User user,
                                             @PathVariable("topLevelAbieId") BigInteger topLevelAbieId) {
        return service.getBieUsedList(user, topLevelAbieId);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAbieId}/ref_list",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, BieEditRef> getRefAbieList(@AuthenticationPrincipal User user,
                                                  @PathVariable("topLevelAbieId") BigInteger topLevelAbieId) {
        return service.getBieRefList(user, topLevelAbieId);
    }

    @RequestMapping(value = "/profile_bie/node/root/{id}/state", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateState(@AuthenticationPrincipal User user,
                            @PathVariable("id") BigInteger topLevelAbieId,
                            @RequestBody Map<String, Object> body) {
        BieState state = BieState.valueOf((String) body.get("state"));
        service.updateState(user, topLevelAbieId, state);
    }

    private <T> T convertValue(String data, Class<T> clazz) {
        Map<String, Object> params = new HashMap();
        Arrays.stream(new String(Base64.getDecoder().decode(data)).split("&")).forEach(e -> {
            String[] keyValue = e.split("=");
            if (keyValue.length > 1) {
                params.put(keyValue[0], keyValue[1]);
            } else {
                params.put(keyValue[0], "");
            }

        });
        return objectMapper.convertValue(params, clazz);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAbieId}/detail", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BieEditUpdateDetailResponse updateDetails(@AuthenticationPrincipal User user,
                                                     @PathVariable("topLevelAbieId") BigInteger topLevelAbieId,
                                                     @RequestBody BieEditUpdateDetailRequest request) {
        request.setTopLevelAbieId(topLevelAbieId);
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

    @RequestMapping(value = "/profile_bie/{topLevelAbieId}/asbiep/{manifestId}/override",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void overrideBIE(@AuthenticationPrincipal User user,
                            @PathVariable("topLevelAbieId") BigInteger topLevelAbieId,
                            @PathVariable("manifestId") BigInteger manifestId,
                            @RequestBody OverrideBIERequest overrideBIERequest) {

        overrideBIERequest.setTopLevelAbieId(topLevelAbieId);
        overrideBIERequest.setAsccpManifestId(manifestId);
        service.overrideBIE(user, overrideBIERequest);
    }
}
