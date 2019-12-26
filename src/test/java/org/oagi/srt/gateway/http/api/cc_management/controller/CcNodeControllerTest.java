package org.oagi.srt.gateway.http.api.cc_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.oagi.srt.gateway.http.api.cc_management.data.*;
import org.oagi.srt.gateway.http.api.cc_management.data.node.CcNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.MultiValueMap;

import static junit.framework.TestCase.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CcNodeControllerTest {
    final String USERNAME = "oagis";
    final String PASSWORD = "oagis";
    final String ROLE = "developer";
    static String METHOD_POST = "post";

    private long releaseId = 1;
    private long bdtId = 1;

    MvcResult callApi(MockMvc mvc, String uri, Object params, String method) throws Exception {
        if (method.equals(METHOD_POST)) {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(params);
            return mvc.perform(post(uri).content(json).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                    .andReturn();
        } else {
            return mvc.perform(get(uri).params((MultiValueMap<String, String>)params).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                    .andReturn();
        }
    }

    <T> T objectMapping(MvcResult mvcResult, Class<T> valueType) throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        String response = mvcResult.getResponse().getContentAsString();
        return objectMapper.readValue(response, valueType);
    }


    @Test
    @WithMockUser(username = USERNAME, password = PASSWORD, roles = ROLE)
    void createCc(@Autowired MockMvc mvc) throws Exception {
        // create Acc
        CcAccCreateRequest ccAccCreateRequest = new CcAccCreateRequest();
        ccAccCreateRequest.setReleaseId(releaseId);
        MvcResult accMvcResult = this.callApi(mvc, "/core_component/acc", ccAccCreateRequest, METHOD_POST);
        CcCreateResponse ccAccResponse = objectMapping(accMvcResult, CcCreateResponse.class);
        if (ccAccResponse.getManifestId() <= 0L) {
            fail("Create Acc fail: " + ccAccResponse.getManifestId());
        }
        // create Asccp
        CcAsccpCreateRequest ccAsccpCreateRequest = new CcAsccpCreateRequest();
        ccAsccpCreateRequest.setReleaseId(releaseId);
        ccAsccpCreateRequest.setRoleOfAccManifestId(ccAccResponse.getManifestId());
        MvcResult asccpMvcResult = this.callApi(mvc, "/core_component/asccp", ccAsccpCreateRequest, METHOD_POST);
        CcCreateResponse ccAsccpResponse = objectMapping(asccpMvcResult, CcCreateResponse.class);
        if (ccAsccpResponse.getManifestId() <= 0L) {
            fail("Create Asccp fail: " + ccAsccpResponse.getManifestId());
        }
        // create bccp
        CcBccpCreateRequest ccBccpCreateRequest = new CcBccpCreateRequest();
        ccBccpCreateRequest.setReleaseId(this.releaseId);
        ccBccpCreateRequest.setBdtManifestId(this.bdtId);
        MvcResult bccpMvcResult = this.callApi(mvc, "/core_component/bccp", ccAsccpCreateRequest, METHOD_POST);
        CcCreateResponse ccBccpResponse = objectMapping(bccpMvcResult, CcCreateResponse.class);
        if (ccBccpResponse.getManifestId() <= 0L) {
            fail("Create Bccp fail: " + ccBccpResponse.getManifestId());
        }
    }

    @Test
    @WithMockUser(username = USERNAME, password = PASSWORD, roles = ROLE)
    void accTest(@Autowired MockMvc mvc) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        // create Acc
        CcAccCreateRequest ccAccCreateRequest = new CcAccCreateRequest();
        ccAccCreateRequest.setReleaseId(releaseId);
        MvcResult accMvcResult = this.callApi(mvc, "/core_component/acc", ccAccCreateRequest, METHOD_POST);
        CcCreateResponse ccAccResponse = objectMapping(accMvcResult, CcCreateResponse.class);
        long accManifestId = ccAccResponse.getManifestId();
        if (accManifestId <= 0L) {
            fail("Create Acc fail: " + accManifestId);
        }

        MvcResult baseAccMvcResult = this.callApi(mvc, "/core_component/acc", ccAccCreateRequest, METHOD_POST);
        CcCreateResponse baseCcAccResponse = objectMapping(baseAccMvcResult, CcCreateResponse.class);
        long baseAccManifestId = baseCcAccResponse.getManifestId();
        if (baseAccManifestId <= 0L) {
            fail("Create Base Acc fail: " + accManifestId);
        }
        CcAccRequest ccAccRequest = new CcAccRequest();
        ccAccRequest.setBasedAccManifestId(baseAccManifestId);
        MvcResult setBaseAccMvcResult = this.callApi(mvc, "/core_component/node/acc/" + accManifestId + "/base", ccAccRequest, METHOD_POST);
        // CcNode accNode = objectMapping(setBaseAccMvcResult, CcNode.class);
    }
}