package ch.dvbern.stip.test.gesuch;

import ch.dvbern.oss.stip.contract.test.api.GesuchApiSpec;
import ch.dvbern.oss.stip.contract.test.dto.GesuchCreateDtoSpec;
import ch.dvbern.oss.stip.contract.test.dto.GesuchDtoSpec;
import ch.dvbern.oss.stip.contract.test.dto.GesuchstatusDtoSpec;
import ch.dvbern.stip.test.benutzer.util.TestAsGesuchsteller;
import ch.dvbern.stip.test.util.RequestSpecUtil;
import ch.dvbern.stip.test.util.TestConstants;
import ch.dvbern.stip.test.util.TestDatabaseEnvironment;
import ch.dvbern.stip.test.util.TestUtil;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ResponseBody;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;

import java.util.UUID;

import static ch.dvbern.stip.test.util.DTOGenerator.prepareGesuchUpdateVollstaendigt;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTestResource(TestDatabaseEnvironment.class)
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@RequiredArgsConstructor
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GesuchStatusTest {

	public final GesuchApiSpec gesuchApiSpec = GesuchApiSpec.gesuch(RequestSpecUtil.quarkusSpec());
	private UUID gesuchId;
	@Test
	@TestAsGesuchsteller
	@Order(1)
	void testCreateGesuchStatusNeu() {
		var gesuchDTO = new GesuchCreateDtoSpec();
		gesuchDTO.setFallId(UUID.fromString(TestConstants.FALL_TEST_ID));
		gesuchDTO.setGesuchsperiodeId(TestConstants.GESUCHSPERIODE_TEST_ID);

		var response = gesuchApiSpec.createGesuch().body(gesuchDTO).execute(ResponseBody::prettyPeek).then();
		response.assertThat()
				.statusCode(Response.Status.CREATED.getStatusCode());
		gesuchId = TestUtil.extractIdFromResponse(response);

		var gesuch = gesuchApiSpec.getGesuch().gesuchIdPath(gesuchId).execute(ResponseBody::prettyPeek)
				.then()
				.extract()
				.body()
				.as(GesuchDtoSpec.class);

		assertThat(gesuch.getGesuchStatus()).isEqualTo(GesuchstatusDtoSpec.OFFEN);
	}

	@Test
	@TestAsGesuchsteller
	@Order(2)
	void testFillGesuchUndEinreichenDokumentFehlt() {
		var gesuchUpdatDTO = prepareGesuchUpdateVollstaendigt();
		gesuchApiSpec.updateGesuch().gesuchIdPath(gesuchId).body(gesuchUpdatDTO).execute(ResponseBody::prettyPeek)
				.then()
				.assertThat()
				.statusCode(Response.Status.ACCEPTED.getStatusCode());

		gesuchApiSpec.gesuchEinreichen().gesuchIdPath(gesuchId)
				.execute(ResponseBody::prettyPeek)
				.then()
				.assertThat()
				.statusCode(Status.ACCEPTED.getStatusCode());
		var gesuch =
				gesuchApiSpec.getGesuch().gesuchIdPath(gesuchId).execute(ResponseBody::prettyPeek).then().extract()
						.body()
						.as(GesuchDtoSpec.class);
		assertThat(gesuch.getGesuchStatus()).isEqualTo(GesuchstatusDtoSpec.NICHT_KOMPLETT_EINGEREICHT);
	}

	@Test
	@TestAsGesuchsteller
	@Order(3)
	void testFillGesuchUndEinreichenNachfristVerlangen() {
		gesuchApiSpec.gesuchNachfrist().gesuchIdPath(gesuchId)
				.execute(ResponseBody::prettyPeek)
				.then()
				.assertThat()
				.statusCode(Status.ACCEPTED.getStatusCode());
		var gesuch =
				gesuchApiSpec.getGesuch().gesuchIdPath(gesuchId).execute(ResponseBody::prettyPeek).then().extract()
						.body()
						.as(GesuchDtoSpec.class);
		assertThat(gesuch.getGesuchStatus()).isEqualTo(GesuchstatusDtoSpec.NICHT_KOMPLETT_EINGEREICHT_NACHFRIST);
	}
}
