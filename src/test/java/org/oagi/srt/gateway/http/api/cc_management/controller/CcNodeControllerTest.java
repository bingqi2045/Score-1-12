package org.oagi.srt.gateway.http.api.cc_management.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oagi.srt.gateway.http.api.cc_management.data.*;
import org.oagi.srt.gateway.http.api.cc_management.data.node.CcAccNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigInteger;

import static junit.framework.TestCase.fail;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CcNodeControllerTest {
    final String USERNAME = "oagis";
    final String PASSWORD = "oagis";
    final String ROLE = "developer";
    static String METHOD_POST = "post";
    static String METHOD_GET = "get";

    private BigInteger releaseId = BigInteger.valueOf(1L);
    private BigInteger bdtId = BigInteger.valueOf(1L);

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(documentationConfiguration(this.restDocumentation))
                .alwaysDo(document(getClass().getSimpleName()))
                .build();
    }

    @Test
    @WithMockUser(username = USERNAME, password = PASSWORD, roles = ROLE)
    public void createCc() throws Exception {
        // create Acc
        CcAccCreateRequest ccAccCreateRequest = new CcAccCreateRequest();
        ccAccCreateRequest.setReleaseId(releaseId);
        MvcResult accMvcResult = this.callApi("/core_component/acc", ccAccCreateRequest, METHOD_POST);
        CcCreateResponse ccAccResponse = objectMapping(accMvcResult, CcCreateResponse.class);
        if (ccAccResponse.getManifestId().compareTo(BigInteger.ZERO) <= -1) {
            fail("Create Acc fail: " + ccAccResponse.getManifestId());
        }
        // create Asccp
        CcAsccpCreateRequest ccAsccpCreateRequest = new CcAsccpCreateRequest();
        ccAsccpCreateRequest.setReleaseId(releaseId);
        ccAsccpCreateRequest.setRoleOfAccManifestId(ccAccResponse.getManifestId());
        MvcResult asccpMvcResult = this.callApi("/core_component/asccp", ccAsccpCreateRequest, METHOD_POST);
        CcCreateResponse ccAsccpResponse = objectMapping(asccpMvcResult, CcCreateResponse.class);
        if (ccAsccpResponse.getManifestId().compareTo(BigInteger.ZERO) <= -1) {
            fail("Create Asccp fail: " + ccAsccpResponse.getManifestId());
        }
        // create bccp
        CcBccpCreateRequest ccBccpCreateRequest = new CcBccpCreateRequest();
        ccBccpCreateRequest.setReleaseId(this.releaseId);
        ccBccpCreateRequest.setBdtManifestId(this.bdtId);
        MvcResult bccpMvcResult = this.callApi("/core_component/bccp", ccBccpCreateRequest, METHOD_POST);
        CcCreateResponse ccBccpResponse = objectMapping(bccpMvcResult, CcCreateResponse.class);
        if (ccBccpResponse.getManifestId().compareTo(BigInteger.ZERO) <= -1) {
            fail("Create Bccp fail: " + ccBccpResponse.getManifestId());
        }
    }

    @Test
    @WithMockUser(username = USERNAME, password = PASSWORD, roles = ROLE)
    public void accTest() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        // create Acc
        CcAccCreateRequest ccAccCreateRequest = new CcAccCreateRequest();
        ccAccCreateRequest.setReleaseId(releaseId);
        MvcResult accMvcResult = this.callApi("/core_component/acc", ccAccCreateRequest, METHOD_POST);
        CcCreateResponse ccAccResponse = objectMapping(accMvcResult, CcCreateResponse.class);
        BigInteger accManifestId = ccAccResponse.getManifestId();
        if (accManifestId.compareTo(BigInteger.ZERO) <= -1) {
            fail("Create Acc fail: " + accManifestId);
        }

        MvcResult baseAccMvcResult = this.callApi("/core_component/acc", ccAccCreateRequest, METHOD_POST);
        CcCreateResponse baseCcAccResponse = objectMapping(baseAccMvcResult, CcCreateResponse.class);
        BigInteger baseAccManifestId = baseCcAccResponse.getManifestId();
        if (baseAccManifestId.compareTo(BigInteger.ZERO) <= -1) {
            fail("Create Base Acc fail: " + accManifestId);
        }

        // get Acc node
        LinkedMultiValueMap params = new LinkedMultiValueMap();
        MvcResult currentAccMvcResult = this.callApi("/core_component/node/acc/" + accManifestId, params, METHOD_GET);
        CcAccNode currentAccNode = objectMapping(currentAccMvcResult, CcAccNode.class);
        BigInteger lastAccId = currentAccNode.getAccId();

        // set baseAcc
        CcAccRequest ccAccRequest = new CcAccRequest();
        ccAccRequest.setBasedAccManifestId(baseAccManifestId);
        MvcResult setBaseAccMvcResult = this.callApi("/core_component/node/acc/" + accManifestId + "/base", ccAccRequest, METHOD_POST);
        CcAccNode accNode = objectMapping(setBaseAccMvcResult, CcAccNode.class);

        if (currentAccNode.getManifestId() != accNode.getManifestId()) {
            fail("Acc ManifestId changed");
        }
        checkCcIdStack(lastAccId, accNode.getAccId());
        if (accNode.getBasedAccManifestId().equals(currentAccNode.getBasedAccManifestId())) {
            fail("BasedAccId not changed");
        }
        lastAccId = accNode.getAccId();

        // create Asccp
        CcAsccpCreateRequest ccAsccpCreateRequest = new CcAsccpCreateRequest();
        ccAsccpCreateRequest.setReleaseId(releaseId);
        ccAsccpCreateRequest.setRoleOfAccManifestId(ccAccResponse.getManifestId());
        MvcResult asccpMvcResult = this.callApi("/core_component/asccp", ccAsccpCreateRequest, METHOD_POST);
        CcCreateResponse ccAsccpResponse = objectMapping(asccpMvcResult, CcCreateResponse.class);

        // append Asccp
        CcAppendRequest ccAppendAsccpRequest = new CcAppendRequest();
        ccAppendAsccpRequest.setAccManifestId(accManifestId);
        ccAppendAsccpRequest.setAsccpManifestId(ccAsccpResponse.getManifestId());
        this.callApi("/core_component/node/append", ccAppendAsccpRequest, METHOD_POST);
        MvcResult afterAppendAsccpMvcResult = this.callApi("/core_component/node/acc/" + accManifestId, params, METHOD_GET);
        CcAccNode afterAppendAsccpNode = objectMapping(afterAppendAsccpMvcResult, CcAccNode.class);
        //checkCcIdStack(afterAppendAsccpNode.getAccId(), lastAccId);
        lastAccId = afterAppendAsccpNode.getAccId();

        // create bccp
        CcBccpCreateRequest ccBccpCreateRequest = new CcBccpCreateRequest();
        ccBccpCreateRequest.setReleaseId(this.releaseId);
        ccBccpCreateRequest.setBdtManifestId(this.bdtId);
        MvcResult bccpMvcResult = this.callApi("/core_component/bccp", ccBccpCreateRequest, METHOD_POST);
        CcCreateResponse ccBccpResponse = objectMapping(bccpMvcResult, CcCreateResponse.class);

        // append bccp
        CcAppendRequest ccAppendBccpRequest = new CcAppendRequest();
        ccAppendAsccpRequest.setAccManifestId(accManifestId);
        ccAppendAsccpRequest.setBccpManifestId(ccBccpResponse.getManifestId());
        this.callApi("/core_component/node/append", ccAppendBccpRequest, METHOD_POST);
        MvcResult afterAppendBccpMvcResult = this.callApi("/core_component/node/acc/" + accManifestId, params, METHOD_GET);
        CcAccNode afterAppendBccpAccNode = objectMapping(afterAppendBccpMvcResult, CcAccNode.class);
        //checkCcIdStack(afterAppendBccpAccNode.getAccId(), lastAccId);
        lastAccId = afterAppendBccpAccNode.getAccId();
    }

    private MvcResult callApi(String uri, Object params, String method) throws Exception {
        if (method.equals(METHOD_POST)) {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(params);
            return this.mockMvc.perform(post(uri).content(json).contentType(MediaType.APPLICATION_JSON)).andExpect(status().is2xxSuccessful())
                    .andReturn();
        } else {
            return this.mockMvc.perform(get(uri).params((MultiValueMap<String, String>) params).contentType(MediaType.APPLICATION_JSON)).andExpect(status().is2xxSuccessful())
                    .andReturn();
        }
    }

    private <T> T objectMapping(MvcResult mvcResult, Class<T> valueType) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        String response = mvcResult.getResponse().getContentAsString();
        return objectMapper.readValue(response, valueType);
    }

    private void checkCcIdStack(BigInteger originCcId, BigInteger newCcId) {
        if (originCcId.equals(newCcId)) {
            fail("CcId not changed (given: " + originCcId + ", " + newCcId + ")");
        }
    }
}
