package ch.dvbern.stip.test.benutzer;

import java.util.UUID;

import ch.dvbern.stip.generated.test.api.BenutzerApiSpec;
import ch.dvbern.stip.generated.test.dto.BenutzerDtoSpec;
import ch.dvbern.stip.test.util.RequestSpecUtil;
import ch.dvbern.stip.test.utils.TestConstants;
import ch.dvbern.stip.test.utils.TestDatabaseEnvironment;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ResponseBody;
import jakarta.ws.rs.core.Response.Status;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@QuarkusTestResource(TestDatabaseEnvironment.class)
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@RequiredArgsConstructor
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BenutzerResourceTest {

	public final BenutzerApiSpec benutzerApiSpec = BenutzerApiSpec.benutzer(RequestSpecUtil.quarkusSpec());

	@Test
	@Order(1)
	void testFindGesuchEndpoint() {
		var benutzende = benutzerApiSpec.getBenutzende().execute(ResponseBody::prettyPeek)
				.then()
				.extract()
				.body()
				.as(BenutzerDtoSpec[].class);

		assertThat(benutzende.length, is(1));
		assertThat(benutzende[0].getId(), is(UUID.fromString(TestConstants.GESUCHSTELLER_TEST_ID)));

	}

	@Test
	@Order(2)
	void testGetBenutzer() {
		var benutzer = benutzerApiSpec.getBenutzer()
				.benutzerIdPath(TestConstants.GESUCHSTELLER_TEST_ID)
				.execute(ResponseBody::prettyPeek)
				.then()
				.extract()
				.body()
				.as(BenutzerDtoSpec.class);
		assertThat(benutzer.getId(), is(UUID.fromString(TestConstants.GESUCHSTELLER_TEST_ID)));
	}

	@Test
	@Order(3)
	void testGetBenutzerNotFound() {
		benutzerApiSpec.getBenutzer()
				.benutzerIdPath(UUID.randomUUID())
				.execute(ResponseBody::prettyPeek)
				.then()
				.assertThat()
				.statusCode(Status.NOT_FOUND.getStatusCode());
	}
}
