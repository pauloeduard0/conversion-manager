package br.inatel.conversionmanager.runner;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class KarateTestRunner {

    @Test
    void runConversionControllerKarate() {
        Results results = Runner.path("classpath:controller/tests").tags("~@ignore").parallel(1);
        assertEquals(0, results.getFailCount(), results.getErrorMessages());
    }
}
