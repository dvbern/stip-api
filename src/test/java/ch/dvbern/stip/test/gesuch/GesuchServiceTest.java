package ch.dvbern.stip.test.gesuch;

import ch.dvbern.stip.api.eltern.entity.Eltern;
import ch.dvbern.stip.api.eltern.type.ElternTyp;
import ch.dvbern.stip.api.familiensituation.type.ElternAbwesenheitsGrund;
import java.util.UUID;

import ch.dvbern.stip.api.eltern.entity.Eltern;
import ch.dvbern.stip.api.eltern.service.ElternMapper;
import ch.dvbern.stip.api.familiensituation.type.Elternschaftsteilung;
import ch.dvbern.stip.api.gesuch.entity.Gesuch;
import ch.dvbern.stip.api.gesuch.entity.GesuchFormular;
import ch.dvbern.stip.api.gesuch.repo.GesuchRepository;
import ch.dvbern.stip.api.gesuch.service.GesuchMapper;
import ch.dvbern.stip.api.gesuch.service.GesuchService;
import ch.dvbern.stip.api.personinausbildung.type.Zivilstand;
import ch.dvbern.stip.generated.dto.GesuchUpdateDto;
import ch.dvbern.stip.generated.dto.PartnerUpdateDto;
import ch.dvbern.stip.test.generator.entities.GesuchGenerator;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static ch.dvbern.stip.api.personinausbildung.type.Zivilstand.*;
import static ch.dvbern.stip.test.generator.entities.GesuchGenerator.initGesuch;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
@Slf4j
class GesuchServiceTest {

	@Inject
	GesuchService gesuchService;

	@Inject
	GesuchMapper gesuchMapper;

	@Inject
	ElternMapper elternMapper;

	@InjectMock
	GesuchRepository gesuchRepository;

	@Test
	void testVerheiratetToLedigShouldResetPartner() {
		final GesuchUpdateDto gesuchUpdateDto = GesuchGenerator.createGesuch();
		Gesuch gesuch = updateFromZivilstandToZivilstand(gesuchUpdateDto, VERHEIRATET, LEDIG);

		MatcherAssert.assertThat(gesuch.getGesuchFormularToWorkWith().getPartner(), Matchers.nullValue());
	}

	@Test
	void testLedigToEveryOtherZivilstandShouldNotResetPartner() {
		for (var zivilstand : Zivilstand.values()) {
			if (zivilstand != LEDIG) {
				final GesuchUpdateDto gesuchUpdateDto = GesuchGenerator.createGesuch();
				Gesuch gesuch = updateFromZivilstandToZivilstand(gesuchUpdateDto, LEDIG, zivilstand);
				MatcherAssert.assertThat(gesuch.getGesuchFormularToWorkWith().getPartner(), Matchers.notNullValue());
			}
		}
	}

	@Test
	void testVerwitwetToEveryOtherZivilstandShouldNotResetPartner() {
		for (var zivilstand : Zivilstand.values()) {
			if (zivilstand != VERWITWET) {
				final GesuchUpdateDto gesuchUpdateDto = GesuchGenerator.createGesuch();
				Gesuch gesuch = updateFromZivilstandToZivilstand(gesuchUpdateDto, VERWITWET, zivilstand);
				MatcherAssert.assertThat(gesuch.getGesuchFormularToWorkWith().getPartner(), Matchers.notNullValue());
			}
		}
	}

	@Test
	void testGeschiedenToEveryOtherZivilstandShouldNotResetPartner() {
		for (var zivilstand : Zivilstand.values()) {
			if (zivilstand != GESCHIEDEN_GERICHTLICH) {
				final GesuchUpdateDto gesuchUpdateDto = GesuchGenerator.createGesuch();
				Gesuch gesuch = updateFromZivilstandToZivilstand(gesuchUpdateDto, GESCHIEDEN_GERICHTLICH, zivilstand);
				MatcherAssert.assertThat(gesuch.getGesuchFormularToWorkWith().getPartner(), Matchers.notNullValue());
			}
		}
	}

	@Test
	void testVerheiratetToKonkubinatEingetragenePartnerschaftShouldNotResetPartner() {
		for (var zivilstand : new Zivilstand[] { KONKUBINAT, EINGETRAGENE_PARTNERSCHAFT }) {
			final GesuchUpdateDto gesuchUpdateDto = GesuchGenerator.createGesuch();
			Gesuch gesuch = updateFromZivilstandToZivilstand(gesuchUpdateDto, VERHEIRATET, zivilstand);
			MatcherAssert.assertThat(gesuch.getGesuchFormularToWorkWith().getPartner(), Matchers.notNullValue());
		}
	}

	@Test
	void testVerheiratetToGeschiedenAufgeloestVerwitwetShouldResetPartner() {
		for (var zivilstand : new Zivilstand[] { GESCHIEDEN_GERICHTLICH, AUFGELOESTE_PARTNERSCHAFT, VERWITWET }) {
			final GesuchUpdateDto gesuchUpdateDto = GesuchGenerator.createGesuch();
			Gesuch gesuch = updateFromZivilstandToZivilstand(gesuchUpdateDto, VERHEIRATET, zivilstand);
			MatcherAssert.assertThat(gesuch.getGesuchFormularToWorkWith().getPartner(), Matchers.nullValue());
		}
	}

	@Test
	void testEingetragenePartnerschaftToKonkubinatVerheiratetShouldNotResetPartner() {
		for (var zivilstand : new Zivilstand[] { KONKUBINAT, VERHEIRATET }) {
			final GesuchUpdateDto gesuchUpdateDto = GesuchGenerator.createGesuch();
			Gesuch gesuch = updateFromZivilstandToZivilstand(gesuchUpdateDto, EINGETRAGENE_PARTNERSCHAFT, zivilstand);
			MatcherAssert.assertThat(gesuch.getGesuchFormularToWorkWith().getPartner(), Matchers.notNullValue());
		}
	}

	@Test
	void testEingetragenePartnerschaftToGeschiedenAufgeloestVerwitwetShouldResetPartner() {
		for (var zivilstand : new Zivilstand[] { GESCHIEDEN_GERICHTLICH, AUFGELOESTE_PARTNERSCHAFT, VERWITWET }) {
			final GesuchUpdateDto gesuchUpdateDto = GesuchGenerator.createGesuch();
			Gesuch gesuch = updateFromZivilstandToZivilstand(gesuchUpdateDto, EINGETRAGENE_PARTNERSCHAFT, zivilstand);
			MatcherAssert.assertThat(gesuch.getGesuchFormularToWorkWith().getPartner(), Matchers.nullValue());
		}
	}

	@Test
	void testkonkubinatToVerheiratetEingetragenePartnerschaftShouldNotResetPartner() {
		for (var zivilstand : new Zivilstand[] { VERHEIRATET, EINGETRAGENE_PARTNERSCHAFT }) {
			final GesuchUpdateDto gesuchUpdateDto = GesuchGenerator.createGesuch();
			Gesuch gesuch = updateFromZivilstandToZivilstand(gesuchUpdateDto, KONKUBINAT, zivilstand);
			MatcherAssert.assertThat(gesuch.getGesuchFormularToWorkWith().getPartner(), Matchers.notNullValue());
		}
	}

	@Test
	void testKonkubinatToGeschiedenAufgeloestVerwitwetShouldResetPartner() {
		for (var zivilstand : new Zivilstand[] { GESCHIEDEN_GERICHTLICH, AUFGELOESTE_PARTNERSCHAFT, VERWITWET }) {
			final GesuchUpdateDto gesuchUpdateDto = GesuchGenerator.createGesuch();
			Gesuch gesuch = updateFromZivilstandToZivilstand(gesuchUpdateDto, KONKUBINAT, zivilstand);
			MatcherAssert.assertThat(gesuch.getGesuchFormularToWorkWith().getPartner(), Matchers.nullValue());
		}
	}

	@Test
	void testAufgeloestePartnerschaftToEveryOtherZivilstandShouldNotResetPartner() {
		for (var zivilstand : Zivilstand.values()) {
			if (zivilstand != AUFGELOESTE_PARTNERSCHAFT) {
				final GesuchUpdateDto gesuchUpdateDto = GesuchGenerator.createGesuch();
				Gesuch gesuch =
						updateFromZivilstandToZivilstand(gesuchUpdateDto, AUFGELOESTE_PARTNERSCHAFT, zivilstand);
				MatcherAssert.assertThat(gesuch.getGesuchFormularToWorkWith().getPartner(), Matchers.notNullValue());
			}
		}
	}

	@Test
	void resetElternDataIfChangeFromMutterToGemeinsam() {
		GesuchUpdateDto gesuchUpdateDto = GesuchGenerator.createGesuch();
		MatcherAssert.assertThat(gesuchUpdateDto.getGesuchFormularToWorkWith().getElterns().size(), Matchers.not(0));
		Gesuch gesuch =
				updateWerZahltAlimente(gesuchUpdateDto, Elternschaftsteilung.MUTTER, Elternschaftsteilung.GEMEINSAM);
		MatcherAssert.assertThat(gesuch.getGesuchFormularToWorkWith().getElterns().size(), Matchers.is(0));
	}

	@Test
	void resetElternDataIfChangeFromVaterToGemeinsam() {
		GesuchUpdateDto gesuchUpdateDto = GesuchGenerator.createGesuch();
		MatcherAssert.assertThat(gesuchUpdateDto.getGesuchFormularToWorkWith().getElterns().size(), Matchers.not(0));
		Gesuch gesuch =
				updateWerZahltAlimente(gesuchUpdateDto, Elternschaftsteilung.VATER, Elternschaftsteilung.GEMEINSAM);
		MatcherAssert.assertThat(gesuch.getGesuchFormularToWorkWith().getElterns().size(), Matchers.is(0));
	}

	@Test
	void noResetElternDataIfNoChangeGemeinsam() {
		GesuchUpdateDto gesuchUpdateDto = GesuchGenerator.createGesuch();
		MatcherAssert.assertThat(gesuchUpdateDto.getGesuchFormularToWorkWith().getElterns().size(), Matchers.not(0));
		var anzahlElternBevoreUpdate = gesuchUpdateDto.getGesuchFormularToWorkWith().getElterns().size();

		Gesuch gesuch =
				updateWerZahltAlimente(gesuchUpdateDto, Elternschaftsteilung.GEMEINSAM,
						Elternschaftsteilung.GEMEINSAM);
		MatcherAssert.assertThat(
				gesuch.getGesuchFormularToWorkWith().getElterns().size(),
				Matchers.is(anzahlElternBevoreUpdate));
	}

	@Test
	void noResetElternDataIfChangeToEltrenschaftAufteilungNotGemeinsam() {
		for (var elternschaftsteilung : Elternschaftsteilung.values()) {
			GesuchUpdateDto gesuchUpdateDto = GesuchGenerator.createGesuch();
			MatcherAssert.assertThat(
					gesuchUpdateDto.getGesuchFormularToWorkWith().getElterns().size(),
					Matchers.not(0));
			var anzahlElternBevoreUpdate = gesuchUpdateDto.getGesuchFormularToWorkWith().getElterns().size();

			Gesuch gesuch = updateWerZahltAlimente(gesuchUpdateDto, elternschaftsteilung, Elternschaftsteilung.MUTTER);
			MatcherAssert.assertThat(
					gesuch.getGesuchFormularToWorkWith().getElterns().size(),
					Matchers.is(anzahlElternBevoreUpdate));
		}
	}

	@Test
	void noResetIfElternStayZusammen() {
		Gesuch gesuch = GesuchGenerator.createGesuch();
		gesuch.getGesuchFormularToWorkWith().getFamiliensituation().setElternVerheiratetZusammen(true);
		MatcherAssert.assertThat(gesuch.getGesuchFormularToWorkWith().getElterns().size(), Matchers.not(0));
		var anzahlElternBevoreUpdate = gesuch.getGesuchFormularToWorkWith().getElterns().size();

		GesuchUpdateDto gesuchUpdateDto = GesuchTestMapper.mapper.toGesuchUpdateDto(gesuch);

		when(gesuchRepository.requireById(any())).thenReturn(gesuch);
		gesuchService.updateGesuch(any(), gesuchUpdateDto);

		MatcherAssert.assertThat(gesuch.getGesuchFormularToWorkWith().getElterns().size(), Matchers.is(anzahlElternBevoreUpdate));
	}

	@Test
	void noResetIfNotUnbekanntOrVerstorben() {
		Gesuch gesuch = GesuchGenerator.createGesuch();
		gesuch.getGesuchFormularToWorkWith().getFamiliensituation().setElternVerheiratetZusammen(true);

		MatcherAssert.assertThat(hasMutter(gesuch.getGesuchFormularToWorkWith().getElterns()), Matchers.is(true));
		MatcherAssert.assertThat(hasVater(gesuch.getGesuchFormularToWorkWith().getElterns()), Matchers.is(true));

		GesuchUpdateDto gesuchUpdateDto = GesuchTestMapper.mapper.toGesuchUpdateDto(gesuch);
		gesuchUpdateDto.getGesuchFormularToWorkWith().getFamiliensituation().setElternVerheiratetZusammen(false);
		gesuchUpdateDto.getGesuchFormularToWorkWith().getFamiliensituation().setElternteilUnbekanntVerstorben(false);

		when(gesuchRepository.requireById(any())).thenReturn(gesuch);
		gesuchService.updateGesuch(any(), gesuchUpdateDto);
		MatcherAssert.assertThat(hasMutter(gesuch.getGesuchFormularToWorkWith().getElterns()), Matchers.is(true));
		MatcherAssert.assertThat(hasVater(gesuch.getGesuchFormularToWorkWith().getElterns()), Matchers.is(true));
	}

	@Test
	void resetMutterIfUnbekannt() {
		Gesuch gesuch = GesuchGenerator.createGesuch();
		gesuch.getGesuchFormularToWorkWith().getFamiliensituation().setElternVerheiratetZusammen(true);

		MatcherAssert.assertThat(hasMutter(gesuch.getGesuchFormularToWorkWith().getElterns()), Matchers.is(true));
		MatcherAssert.assertThat(hasVater(gesuch.getGesuchFormularToWorkWith().getElterns()), Matchers.is(true));

		updateElternteilUnbekanntVerstorben(gesuch, ElternAbwesenheitsGrund.UNBEKANNT, ElternAbwesenheitsGrund.WEDER_NOCH);

		MatcherAssert.assertThat(hasMutter(gesuch.getGesuchFormularToWorkWith().getElterns()), Matchers.is(false));
		MatcherAssert.assertThat(hasVater(gesuch.getGesuchFormularToWorkWith().getElterns()), Matchers.is(true));
	}

	@Test
	void resetMutterIfVerstorben() {
		Gesuch gesuch = GesuchGenerator.createGesuch();
		gesuch.getGesuchFormularToWorkWith().getFamiliensituation().setElternVerheiratetZusammen(true);

		MatcherAssert.assertThat(hasMutter(gesuch.getGesuchFormularToWorkWith().getElterns()), Matchers.is(true));
		MatcherAssert.assertThat(hasVater(gesuch.getGesuchFormularToWorkWith().getElterns()), Matchers.is(true));

		updateElternteilUnbekanntVerstorben(gesuch, ElternAbwesenheitsGrund.VERSTORBEN, ElternAbwesenheitsGrund.WEDER_NOCH);

		MatcherAssert.assertThat(hasMutter(gesuch.getGesuchFormularToWorkWith().getElterns()), Matchers.is(false));
		MatcherAssert.assertThat(hasVater(gesuch.getGesuchFormularToWorkWith().getElterns()), Matchers.is(true));
	}

	@Test
	void resetVaterIfUnbekannt() {
		Gesuch gesuch = GesuchGenerator.createGesuch();
		gesuch.getGesuchFormularToWorkWith().getFamiliensituation().setElternVerheiratetZusammen(true);

		MatcherAssert.assertThat(hasMutter(gesuch.getGesuchFormularToWorkWith().getElterns()), Matchers.is(true));
		MatcherAssert.assertThat(hasVater(gesuch.getGesuchFormularToWorkWith().getElterns()), Matchers.is(true));

		updateElternteilUnbekanntVerstorben(gesuch, ElternAbwesenheitsGrund.WEDER_NOCH, ElternAbwesenheitsGrund.UNBEKANNT);

		MatcherAssert.assertThat(hasMutter(gesuch.getGesuchFormularToWorkWith().getElterns()), Matchers.is(true));
		MatcherAssert.assertThat(hasVater(gesuch.getGesuchFormularToWorkWith().getElterns()), Matchers.is(false));
	}

	@Test
	void resetVaterIfVerstorben() {
		Gesuch gesuch = GesuchGenerator.createGesuch();
		gesuch.getGesuchFormularToWorkWith().getFamiliensituation().setElternVerheiratetZusammen(true);

		MatcherAssert.assertThat(hasMutter(gesuch.getGesuchFormularToWorkWith().getElterns()), Matchers.is(true));
		MatcherAssert.assertThat(hasVater(gesuch.getGesuchFormularToWorkWith().getElterns()), Matchers.is(true));

		updateElternteilUnbekanntVerstorben(gesuch, ElternAbwesenheitsGrund.WEDER_NOCH, ElternAbwesenheitsGrund.VERSTORBEN);

		MatcherAssert.assertThat(hasMutter(gesuch.getGesuchFormularToWorkWith().getElterns()), Matchers.is(true));
		MatcherAssert.assertThat(hasVater(gesuch.getGesuchFormularToWorkWith().getElterns()), Matchers.is(false));
	}

	@Test
	void resetBothIfVerstorben() {
		Gesuch gesuch = GesuchGenerator.createGesuch();
		gesuch.getGesuchFormularToWorkWith().getFamiliensituation().setElternVerheiratetZusammen(true);

		MatcherAssert.assertThat(hasMutter(gesuch.getGesuchFormularToWorkWith().getElterns()), Matchers.is(true));
		MatcherAssert.assertThat(hasVater(gesuch.getGesuchFormularToWorkWith().getElterns()), Matchers.is(true));

		updateElternteilUnbekanntVerstorben(gesuch, ElternAbwesenheitsGrund.VERSTORBEN, ElternAbwesenheitsGrund.VERSTORBEN);

		MatcherAssert.assertThat(hasMutter(gesuch.getGesuchFormularToWorkWith().getElterns()), Matchers.is(false));
		MatcherAssert.assertThat(hasVater(gesuch.getGesuchFormularToWorkWith().getElterns()), Matchers.is(false));
	}

	@Test
	void resetBothIfUnbekannt() {
		Gesuch gesuch = GesuchGenerator.createGesuch();
		gesuch.getGesuchFormularToWorkWith().getFamiliensituation().setElternVerheiratetZusammen(true);

		MatcherAssert.assertThat(hasMutter(gesuch.getGesuchFormularToWorkWith().getElterns()), Matchers.is(true));
		MatcherAssert.assertThat(hasVater(gesuch.getGesuchFormularToWorkWith().getElterns()), Matchers.is(true));

		updateElternteilUnbekanntVerstorben(gesuch, ElternAbwesenheitsGrund.UNBEKANNT, ElternAbwesenheitsGrund.UNBEKANNT);

		MatcherAssert.assertThat(hasMutter(gesuch.getGesuchFormularToWorkWith().getElterns()), Matchers.is(false));
		MatcherAssert.assertThat(hasVater(gesuch.getGesuchFormularToWorkWith().getElterns()), Matchers.is(false));
	}


	private boolean hasMutter(Set<Eltern> elterns) {
		return getElternFromElternsByElternTyp(elterns, ElternTyp.MUTTER).isPresent();
	}

	private boolean hasVater(Set<Eltern> elterns) {
		return getElternFromElternsByElternTyp(elterns, ElternTyp.VATER).isPresent();
	}

	private Optional<Eltern> getElternFromElternsByElternTyp(Set<Eltern> elterns, ElternTyp elternTyp) {
		return elterns.stream()
				.filter(eltern -> eltern.getElternTyp() == elternTyp)
				.findFirst();
	}

	private void updateElternteilUnbekanntVerstorben(Gesuch gesuch, ElternAbwesenheitsGrund grundMutter, ElternAbwesenheitsGrund grundVater) {
		GesuchUpdateDto gesuchUpdateDto = GesuchTestMapper.mapper.toGesuchUpdateDto(gesuch);
		gesuchUpdateDto.getGesuchFormularToWorkWith().getFamiliensituation().setElternVerheiratetZusammen(false);
		gesuchUpdateDto.getGesuchFormularToWorkWith().getFamiliensituation().setElternteilUnbekanntVerstorben(true);
		gesuchUpdateDto.getGesuchFormularToWorkWith().getFamiliensituation().setMutterUnbekanntVerstorben(grundMutter);
		gesuchUpdateDto.getGesuchFormularToWorkWith().getFamiliensituation().setVaterUnbekanntVerstorben(grundVater);

		when(gesuchRepository.requireById(any())).thenReturn(gesuch);
		gesuchService.updateGesuch(any(), gesuchUpdateDto);
	}

	private Gesuch updateWerZahltAlimente(
			GesuchUpdateDto gesuchUpdateDto,
			Elternschaftsteilung from,
			Elternschaftsteilung to) {
		Gesuch gesuch = prepareGesuchsformularMitElternId(gesuchUpdateDto);
		gesuchUpdateDto.getGesuchFormularToWorkWith().getFamiliensituation().setGerichtlicheAlimentenregelung(true);
		gesuchUpdateDto.getGesuchFormularToWorkWith().getFamiliensituation().setWerZahltAlimente(from);
		gesuchMapper.partialUpdate(gesuchUpdateDto, gesuch);

		gesuchUpdateDto.getGesuchFormularToWorkWith().getFamiliensituation().setWerZahltAlimente(to);
		when(gesuchRepository.requireById(any())).thenReturn(gesuch);
		gesuchService.updateGesuch(any(), gesuchUpdateDto);
		return gesuch;
	}

	private Gesuch updateFromZivilstandToZivilstand(GesuchUpdateDto gesuchUpdateDto, Zivilstand from, Zivilstand to) {
		Gesuch gesuch = prepareGesuchsformularMitElternId(gesuchUpdateDto);
		gesuchUpdateDto.getGesuchFormularToWorkWith().setPartner(new PartnerUpdateDto());
		gesuchUpdateDto.getGesuchFormularToWorkWith().getPersonInAusbildung().setZivilstand(from);
		gesuchMapper.partialUpdate(gesuchUpdateDto, gesuch);
		gesuchUpdateDto.getGesuchFormularToWorkWith().getPersonInAusbildung().setZivilstand(to);
		when(gesuchRepository.requireById(any())).thenReturn(gesuch);
		gesuchService.updateGesuch(any(), gesuchUpdateDto);
		return gesuch;
	}

	private Gesuch prepareGesuchsformularMitElternId(GesuchUpdateDto gesuchUpdateDto){
		Gesuch gesuch = initGesuch();
		GesuchFormular gesuchFormular = new GesuchFormular();
		gesuchUpdateDto.getGesuchFormularToWorkWith().setPartner(new PartnerUpdateDto());
		gesuchUpdateDto.getGesuchFormularToWorkWith().getElterns().stream().forEach(elternUpdateDto -> elternUpdateDto.setId(UUID.randomUUID()));
		gesuchUpdateDto.getGesuchFormularToWorkWith().getElterns().stream().forEach(elternUpdateDto -> gesuchFormular.getElterns().add(elternMapper.partialUpdate(elternUpdateDto, new Eltern())));
		gesuch.setGesuchFormularToWorkWith(gesuchFormular);
		return gesuch;
	}
}
