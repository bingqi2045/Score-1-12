package org.oagi.srt.gateway.http.api.bie_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oagi.srt.gateway.http.ScoreHttpApplication;
import org.oagi.srt.gateway.http.api.bie_management.data.BieCreateRequest;
import org.oagi.srt.gateway.http.api.bie_management.data.BieCreateResponse;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.jooq.impl.DSL.and;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.oagi.srt.entity.jooq.Tables.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = ScoreHttpApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@AutoConfigureMockMvc
public class BieCreateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DSLContext dslContext;

    private ObjectMapper mapper = new ObjectMapper();

    private ULong testAsccpManifestId;
    private ULong testBizCtxId = ULong.valueOf(1L);

    @Before
    public void prepareRequirements() {
        testAsccpManifestId = dslContext.select(ASCCP_MANIFEST.ASCCP_MANIFEST_ID)
                .from(ASCCP_MANIFEST)
                .join(ASCCP).on(ASCCP_MANIFEST.ASCCP_ID.eq(ASCCP.ASCCP_ID))
                .join(RELEASE).on(ASCCP_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(and(
                        ASCCP.PROPERTY_TERM.eq("Sync Purchase Order"),
                        RELEASE.RELEASE_NUM.eq("10.6")
                ))
                .fetchOneInto(ULong.class);


        boolean exists = dslContext.selectCount()
                .from(BIZ_CTX)
                .where(BIZ_CTX.BIZ_CTX_ID.eq(testBizCtxId))
                .fetchOptionalInto(Integer.class).orElse(0) == 1;
        if (!exists) {
            ULong userId = ULong.valueOf(1L);
            LocalDateTime timestamp = LocalDateTime.now();

            testBizCtxId = dslContext.insertInto(BIZ_CTX)
                    .set(BIZ_CTX.NAME, "Test Business Context")
                    .set(BIZ_CTX.GUID, SrtGuid.randomGuid())
                    .set(BIZ_CTX.CREATED_BY, userId)
                    .set(BIZ_CTX.LAST_UPDATED_BY, userId)
                    .set(BIZ_CTX.CREATION_TIMESTAMP, timestamp)
                    .set(BIZ_CTX.LAST_UPDATE_TIMESTAMP, timestamp)
                    .returning(BIZ_CTX.BIZ_CTX_ID).fetchOne().getBizCtxId();
        }
    }

    @Test
    @WithMockUser(username = "oagis")
    public void shouldCreateBie() throws Exception {
        BieCreateRequest request = new BieCreateRequest();
        request.setAsccpManifestId(testAsccpManifestId.longValue());
        request.setBizCtxIds(Arrays.asList(testBizCtxId.longValue()));

        BieCreateResponse response =
                mapper.readValue(
                        this.mockMvc.perform(
                                put("/profile_bie/create")
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .content(mapper.writeValueAsString(request)))
                                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(),
                        BieCreateResponse.class
                );

        assertNotNull(response);
        assertTrue(response.getTopLevelAbieId() > 0L);
    }
}
