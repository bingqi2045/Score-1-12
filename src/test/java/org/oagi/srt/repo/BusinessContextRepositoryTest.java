package org.oagi.srt.repo;

import org.junit.runner.RunWith;
import org.oagi.srt.gateway.http.ScoreHttpApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = ScoreHttpApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
public class BusinessContextRepositoryTest {

}
