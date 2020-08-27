package org.oagi.score.repo;

import org.junit.runner.RunWith;
import org.oagi.score.gateway.http.ScoreHttpApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = ScoreHttpApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
public class BusinessContextRepositoryTest {

}
