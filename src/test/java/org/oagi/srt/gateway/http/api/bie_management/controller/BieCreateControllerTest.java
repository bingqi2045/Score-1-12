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
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.oagi.srt.entity.jooq.Tables.BIZ_CTX;
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

    private MockHttpSession session;
    private ULong testBizCtxId = ULong.valueOf(1L);

    @Before
    public void prepareRequirements() {
        session = new MockHttpSession();

        User user = new User("oagis", null,
                Arrays.asList(new SimpleGrantedAuthority("developer")));
        session.setAttribute("user", user);

        boolean exists = dslContext.selectCount()
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
    public void shouldCreateBie() throws Exception {
        BieCreateRequest request = new BieCreateRequest();
        request.setAsccpManifestId(1L);
        request.setBizCtxIds(Arrays.asList(testBizCtxId.longValue()));

        BieCreateResponse response =
                mapper.readValue(
                        this.mockMvc.perform(
                                put("/profile_bie/create")
                                        .session(session)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .content(mapper.writeValueAsString(request)))
                                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(),
                        BieCreateResponse.class
                );

        assertNotNull(response);
        assertTrue(response.getTopLevelAbieId() > 0L);
    }
}
