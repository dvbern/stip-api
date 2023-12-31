package ch.dvbern.stip.test.ausbildung;

import ch.dvbern.oss.stip.contract.test.api.AusbildungsstaetteApiSpec;
import ch.dvbern.oss.stip.contract.test.dto.AusbildungsstaetteDtoSpec;
import ch.dvbern.stip.test.benutzer.util.TestAsGesuchsteller;
import ch.dvbern.stip.test.util.RequestSpecUtil;
import ch.dvbern.stip.test.util.TestConstants;
import ch.dvbern.stip.test.util.TestDatabaseEnvironment;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ResponseBody;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@QuarkusTestResource(TestDatabaseEnvironment.class)
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@RequiredArgsConstructor
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AusbildungsstaetteResourceTest {

    private final AusbildungsstaetteApiSpec api = AusbildungsstaetteApiSpec.ausbildungsstaette(RequestSpecUtil.quarkusSpec());

    @Test
    @TestAsGesuchsteller
    void test_get_ausbildungsstaetten() {
        var res = api.getAusbildungsstaetten().execute(ResponseBody::prettyPeek)
                .then()
                .assertThat()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .as(AusbildungsstaetteDtoSpec[].class);

        assertThat(res.length, greaterThanOrEqualTo(1));
    }

    @Test
    @TestAsGesuchsteller
    void getausbildungsstaetten() {
        api.getAusbildungsstaette().ausbildungsstaetteIdPath(TestConstants.AUSBILDUNGSSTAETTE_ID)
                .execute(ResponseBody::prettyPeek)
                .then()
                .assertThat()
                .statusCode(Response.Status.OK.getStatusCode());
    }

}
