package ch.dvbern.stip.test.gesuch;

import ch.dvbern.stip.api.adresse.entity.Adresse;
import ch.dvbern.stip.api.ausbildung.entity.Ausbildung;
import ch.dvbern.stip.api.ausbildung.entity.Ausbildungsgang;
import ch.dvbern.stip.api.common.type.Bildungsart;
import ch.dvbern.stip.api.common.type.Wohnsitz;
import ch.dvbern.stip.api.einnahmen_kosten.entity.EinnahmenKosten;
import ch.dvbern.stip.api.eltern.entity.Eltern;
import ch.dvbern.stip.api.eltern.type.ElternTyp;
import ch.dvbern.stip.api.fall.entity.Fall;
import ch.dvbern.stip.api.familiensituation.entity.Familiensituation;
import ch.dvbern.stip.api.familiensituation.type.ElternAbwesenheitsGrund;
import ch.dvbern.stip.api.familiensituation.type.Elternschaftsteilung;
import ch.dvbern.stip.api.geschwister.entity.Geschwister;
import ch.dvbern.stip.api.gesuch.entity.Gesuch;
import ch.dvbern.stip.api.gesuch.entity.GesuchEinreichenValidationGroup;
import ch.dvbern.stip.api.gesuch.entity.GesuchFormular;
import ch.dvbern.stip.api.gesuchsperioden.entity.Gesuchsperiode;
import ch.dvbern.stip.api.kind.entity.Kind;
import ch.dvbern.stip.api.lebenslauf.entity.LebenslaufItem;
import ch.dvbern.stip.api.lebenslauf.type.Taetigskeitsart;
import ch.dvbern.stip.api.personinausbildung.entity.PersonInAusbildung;
import ch.dvbern.stip.api.personinausbildung.type.Niederlassungsstatus;
import ch.dvbern.stip.api.personinausbildung.type.Zivilstand;
import ch.dvbern.stip.api.stammdaten.type.Land;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static ch.dvbern.stip.api.common.validation.ValidationsConstant.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@RequiredArgsConstructor
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GesuchValidatorTest {

	private final Validator validator;

	@Test
	void testFieldValidationErrorForPersonInAusbildung() {
		String[] constraintMessages = { VALIDATION_NACHNAME_NOTBLANK_MESSAGE, VALIDATION_VORNAME_NOTBLANK_MESSAGE,
				VALIDATION_IZW_FIELD_REQUIRED_MESSAGE, VALIDATION_HEIMATORT_FIELD_REQUIRED_MESSAGE,
				VALIDATION_WOHNSITZ_ANTEIL_FIELD_REQUIRED_MESSAGE, VALIDATION_AHV_MESSAGE,
				VALIDATION_NIEDERLASSUNGSSTATUS_FIELD_REQUIRED_NULL_MESSAGE};
		PersonInAusbildung personInAusbildung = new PersonInAusbildung();
		personInAusbildung.setAdresse(new Adresse());
		// Beim Land CH muss der Heimatort nicht leer sein
		personInAusbildung.getAdresse().setLand(Land.CH);
		// Beim nicht IZV muessen die IZV PLZ und Ort nicht leer sein
		personInAusbildung.setIdentischerZivilrechtlicherWohnsitz(false);
		// Beim Nationalitaet CH muesst die Niederlassungsstatus nicht gegeben werden
		personInAusbildung.setNationalitaet(Land.CH);
		personInAusbildung.setNiederlassungsstatus(Niederlassungsstatus.NIEDERLASSUNGSBEWILLIGUNG_C);
		// Beim Wohnsitz MUTTER_VATER muessen die Anteile Feldern nicht null sein
		personInAusbildung.setWohnsitz(Wohnsitz.MUTTER_VATER);
		Gesuch gesuch = prepareDummyGesuch();
		gesuch.getGesuchFormularToWorkWith().setPersonInAusbildung(personInAusbildung);
		assertAllMessagesPresent(constraintMessages, gesuch);

		// Die Anteil muessen wenn gegeben einen 100% Pensum im Total entsprechend, groessere oder kleiner Angaben
		// sind rejektiert
		gesuch.getGesuchFormularToWorkWith().getPersonInAusbildung().setWohnsitzAnteilMutter(new BigDecimal(40.00));
		gesuch.getGesuchFormularToWorkWith().getPersonInAusbildung().setWohnsitzAnteilVater(new BigDecimal(50.00));
		Set<ConstraintViolation<Gesuch>> violations = validator.validate(gesuch);
		assertThat(violations.stream()
				.anyMatch(gesuchConstraintViolation -> gesuchConstraintViolation.getMessageTemplate()
						.equals(VALIDATION_WOHNSITZ_ANTEIL_BERECHNUNG_MESSAGE)), is(true));
		gesuch.getGesuchFormularToWorkWith().getPersonInAusbildung().setWohnsitzAnteilVater(new BigDecimal(60.00));
		violations = validator.validate(gesuch);
		assertThat(violations.stream()
				.anyMatch(gesuchConstraintViolation -> gesuchConstraintViolation.getMessageTemplate()
						.equals(VALIDATION_WOHNSITZ_ANTEIL_BERECHNUNG_MESSAGE)), is(false));
	}

	@Test
	void testNullFieldValidationErrorForPersonInAusbildung() {
		String[] constraintMessages =
				{ VALIDATION_IZW_FIELD_REQUIRED_NULL_MESSAGE, VALIDATION_WOHNSITZ_ANTEIL_FIELD_REQUIRED_NULL_MESSAGE,
						VALIDATION_HEIMATORT_FIELD_REQUIRED_NULL_MESSAGE, VALIDATION_NIEDERLASSUNGSSTATUS_FIELD_REQUIRED_MESSAGE};
		PersonInAusbildung personInAusbildung = new PersonInAusbildung();
		// Wohnsitz Anteil muessen leer sein beim Wohnsitz != MUTTER_VATER
		personInAusbildung.setWohnsitz(Wohnsitz.FAMILIE);
		personInAusbildung.setWohnsitzAnteilVater(BigDecimal.ONE);
		// Beim IZV muessen die IZV Ort und PLZ leer sein
		personInAusbildung.setIdentischerZivilrechtlicherWohnsitz(true);
		personInAusbildung.setIdentischerZivilrechtlicherWohnsitzOrt("Test");
		// Beim Nationalitaet != CH duerfen keinen Heimatort erfasst werden
		personInAusbildung.setNationalitaet(Land.FR);
		personInAusbildung.setHeimatort("");
		Gesuch gesuch = prepareDummyGesuch();
		gesuch.getGesuchFormularToWorkWith().setPersonInAusbildung(personInAusbildung);
		assertAllMessagesPresent(constraintMessages, gesuch);
	}

	@Test
	void testNullFieldValidationErrorForAusbildung() {
		Ausbildung ausbildung = new Ausbildung();
		Gesuch gesuch = prepareDummyGesuch();
		gesuch.getGesuchFormularToWorkWith().setAusbildung(ausbildung);
		// Die Ausbildungsgang und Staette muessen bei keine alternative Ausbildung gegeben werden
		assertAllMessagesPresent(new String[] { VALIDATION_AUSBILDUNG_FIELD_REQUIRED_MESSAGE }, gesuch);
		assertAllMessagesNotPresent(new String[] { VALIDATION_ALTERNATIVE_AUSBILDUNG_FIELD_REQUIRED_MESSAGE }, gesuch);
		// Die alternative Ausbildungsgang und Staette muessen bei alternative Ausbildung gegeben werden
		gesuch.getGesuchFormularToWorkWith().getAusbildung().setAusbildungNichtGefunden(true);
		assertAllMessagesPresent(new String[] { VALIDATION_ALTERNATIVE_AUSBILDUNG_FIELD_REQUIRED_MESSAGE }, gesuch);
		assertAllMessagesNotPresent(new String[] { VALIDATION_AUSBILDUNG_FIELD_REQUIRED_MESSAGE }, gesuch);
	}

	@Test
	void testFieldValidationErrorForAusbildung() {
		Ausbildung ausbildung = new Ausbildung();
		ausbildung.setAlternativeAusbildungsgang("ausbildungsgang alt");
		Gesuch gesuch = prepareDummyGesuch();
		gesuch.getGesuchFormularToWorkWith().setAusbildung(ausbildung);
		// Die alternative Ausbildungsgang und Staette muessen bei keine alternative Ausbildung null sein
		assertAllMessagesPresent(
				new String[] { VALIDATION_ALTERNATIVE_AUSBILDUNG_FIELD_REQUIRED_NULL_MESSAGE },
				gesuch);
		assertAllMessagesNotPresent(new String[] { VALIDATION_AUSBILDUNG_FIELD_REQUIRED_NULL_MESSAGE }, gesuch);
		// Die Ausbildungsgang und Staette muessen bei alternative Ausbildung null sein
		gesuch.getGesuchFormularToWorkWith().getAusbildung().setAusbildungNichtGefunden(true);
		gesuch.getGesuchFormularToWorkWith().getAusbildung().setAusbildungsgang(new Ausbildungsgang());
		assertAllMessagesPresent(new String[] { VALIDATION_AUSBILDUNG_FIELD_REQUIRED_NULL_MESSAGE }, gesuch);
		assertAllMessagesNotPresent(
				new String[] { VALIDATION_ALTERNATIVE_AUSBILDUNG_FIELD_REQUIRED_NULL_MESSAGE },
				gesuch);
	}

	@Test
	void testFieldValidationErrorFamiliensituation() {
		String[] constraintMessages = {
				VALIDATION_WER_ZAHLT_ALIMENTE_FIELD_REQUIRED_MESSAGE, VALIDATION_OBHUT_GEMEINSAM_FIELD_REQUIRED_MESSAGE
		};
		Familiensituation familiensituation = new Familiensituation();
		// beim Obhut gemeinsam muessen die Obhut Mutter und Vater Feldern nicht null sein
		familiensituation.setObhut(Elternschaftsteilung.GEMEINSAM);
		// beim gerichtliche Alimentregelung muesst die Wer Zahlt Alimente ausgewaehlt werden
		familiensituation.setGerichtlicheAlimentenregelung(true);
		Gesuch gesuch = prepareDummyGesuch();
		gesuch.getGesuchFormularToWorkWith().setFamiliensituation(familiensituation);
		assertAllMessagesPresent(constraintMessages, gesuch);

		// Test die Obhut Berechnung:
		gesuch.getGesuchFormularToWorkWith().getFamiliensituation().setObhutVater(new BigDecimal(40.00));
		gesuch.getGesuchFormularToWorkWith().getFamiliensituation().setObhutMutter(new BigDecimal(50.00));
		assertAllMessagesPresent(new String[] { VALIDATION_OBHUT_GEMEINSAM_BERECHNUNG_MESSAGE }, gesuch);
		// korrekte Werten Meldung soll weg
		gesuch.getGesuchFormularToWorkWith().getFamiliensituation().setObhutMutter(new BigDecimal(60.00));
		assertAllMessagesNotPresent(new String[] { VALIDATION_OBHUT_GEMEINSAM_BERECHNUNG_MESSAGE }, gesuch);
	}

	@Test
	void testNullFieldValidationErrorFamiliensituation() {
		String[] constraintMessages = {
				VALIDATION_WER_ZAHLT_ALIMENTE_FIELD_REQUIRED_NULL_MESSAGE,
				VALIDATION_OBHUT_GEMEINSAM_FIELD_REQUIRED_NULL_MESSAGE
		};
		Familiensituation familiensituation = new Familiensituation();
		// beim Obhut != gemeinsam muessen die Obhut Mutter und Vater Feldern null sein
		familiensituation.setObhut(Elternschaftsteilung.VATER);
		familiensituation.setObhutVater(BigDecimal.ONE);
		// beim keine gerichtliche Alimentregelung muesst die Wer Zahlt Alimente nicht ausgewaehlt werden
		familiensituation.setGerichtlicheAlimentenregelung(false);
		familiensituation.setWerZahltAlimente(Elternschaftsteilung.GEMEINSAM);
		Gesuch gesuch = prepareDummyGesuch();
		gesuch.getGesuchFormularToWorkWith().setFamiliensituation(familiensituation);
		assertAllMessagesPresent(constraintMessages, gesuch);
	}

	@Test
	void testFieldValidationErrorEltern() {
		String[] constraintMessages = {
				VALIDATION_IZW_FIELD_REQUIRED_MESSAGE, VALIDATION_AHV_MESSAGE
		};
		Eltern eltern = new Eltern();
		eltern.setSozialversicherungsnummer("123.456.789.101112");
		eltern.setIdentischerZivilrechtlicherWohnsitz(false);
		Gesuch gesuch = prepareDummyGesuch();
		Set<Eltern> elternSet = new HashSet<>();
		elternSet.add(eltern);
		gesuch.getGesuchFormularToWorkWith().setElterns(elternSet);
		assertAllMessagesPresent(constraintMessages, gesuch);
	}

	@Test
	void testNullFieldValidationErrorEltern() {
		String[] constraintMessages = {
				VALIDATION_AHV_MESSAGE, VALIDATION_IZW_FIELD_REQUIRED_NULL_MESSAGE
		};
		Eltern eltern = new Eltern();
		eltern.setIdentischerZivilrechtlicherWohnsitz(true);
		eltern.setIdentischerZivilrechtlicherWohnsitzPLZ("1234");
		Gesuch gesuch = prepareDummyGesuch();
		Set<Eltern> elternSet = new HashSet<>();
		elternSet.add(eltern);
		gesuch.getGesuchFormularToWorkWith().setElterns(elternSet);
		assertAllMessagesPresent(constraintMessages, gesuch);
	}

	@Test
	void testFieldValidationErrorGeschwister() {
		Geschwister geschwister = new Geschwister();
		// beim Wohnsitz MUTTER_VATER muessen die Anteil Mutter und Vater Feldern nicht null sein
		geschwister.setWohnsitz(Wohnsitz.MUTTER_VATER);
		Set<Geschwister> geschwisterSet = new HashSet<>();
		geschwisterSet.add(geschwister);
		Gesuch gesuch = prepareDummyGesuch();
		gesuch.getGesuchFormularToWorkWith().setGeschwisters(geschwisterSet);
		assertAllMessagesPresent(new String[] { VALIDATION_WOHNSITZ_ANTEIL_FIELD_REQUIRED_MESSAGE }, gesuch);

		// Test die Wohnsitzanteil Berechnung:
		geschwister.setWohnsitzAnteilVater(new BigDecimal(55.00));
		geschwister.setWohnsitzAnteilMutter(new BigDecimal(55.00));
		geschwisterSet = new HashSet<>();
		geschwisterSet.add(geschwister);
		gesuch.getGesuchFormularToWorkWith().setGeschwisters(geschwisterSet);
		assertAllMessagesPresent(new String[] { VALIDATION_WOHNSITZ_ANTEIL_BERECHNUNG_MESSAGE }, gesuch);

		geschwister.setWohnsitzAnteilMutter(new BigDecimal(45.00));
		geschwisterSet = new HashSet<>();
		geschwisterSet.add(geschwister);
		gesuch.getGesuchFormularToWorkWith().setGeschwisters(geschwisterSet);
		assertAllMessagesNotPresent(new String[] { VALIDATION_WOHNSITZ_ANTEIL_BERECHNUNG_MESSAGE }, gesuch);
	}

	@Test
	void testNullFieldValidationErrorGeschwister() {
		Geschwister geschwister = new Geschwister();
		// beim Wohnsitz != MUTTER_VATER muessen die Anteil Mutter und Vater Feldern null sein
		geschwister.setWohnsitz(Wohnsitz.FAMILIE);
		geschwister.setWohnsitzAnteilMutter(BigDecimal.TEN);
		Set<Geschwister> geschwisterSet = new HashSet<>();
		geschwisterSet.add(geschwister);
		Gesuch gesuch = prepareDummyGesuch();
		gesuch.getGesuchFormularToWorkWith().setGeschwisters(geschwisterSet);
		assertAllMessagesPresent(new String[] { VALIDATION_WOHNSITZ_ANTEIL_FIELD_REQUIRED_NULL_MESSAGE }, gesuch);
	}

	@Test
	void testFieldValidationErrorKind() {
		Kind kind = new Kind();
		// beim Wohnsitz MUTTER_VATER muessen die Anteil Mutter und Vater Feldern nicht null sein
		kind.setWohnsitz(Wohnsitz.MUTTER_VATER);
		Set<Kind> kindSet = new HashSet<>();
		kindSet.add(kind);
		Gesuch gesuch = prepareDummyGesuch();
		gesuch.getGesuchFormularToWorkWith().setKinds(kindSet);
		assertAllMessagesPresent(new String[] { VALIDATION_WOHNSITZ_ANTEIL_FIELD_REQUIRED_MESSAGE }, gesuch);
		// Test die Wohnsitzanteil Berechnung:
		kind.setWohnsitzAnteilVater(new BigDecimal(55.00));
		kind.setWohnsitzAnteilMutter(new BigDecimal(55.00));
		kindSet = new HashSet<>();
		kindSet.add(kind);
		gesuch.getGesuchFormularToWorkWith().setKinds(kindSet);
		assertAllMessagesPresent(new String[] { VALIDATION_WOHNSITZ_ANTEIL_BERECHNUNG_MESSAGE }, gesuch);
		kind.setWohnsitzAnteilMutter(new BigDecimal(45.00));
		kindSet = new HashSet<>();
		kindSet.add(kind);
		gesuch.getGesuchFormularToWorkWith().setKinds(kindSet);
		assertAllMessagesNotPresent(new String[] { VALIDATION_WOHNSITZ_ANTEIL_BERECHNUNG_MESSAGE }, gesuch);
	}

	@Test
	void testNullFieldValidationErrorKind() {
		Kind kind = new Kind();
		// beim Wohnsitz != MUTTER_VATER muessen die Anteil Mutter und Vater Feldern null sein
		kind.setWohnsitz(Wohnsitz.FAMILIE);
		kind.setWohnsitzAnteilMutter(BigDecimal.TEN);
		Set<Kind> kindSet = new HashSet<>();
		kindSet.add(kind);
		Gesuch gesuch = prepareDummyGesuch();
		gesuch.getGesuchFormularToWorkWith().setKinds(kindSet);
		assertAllMessagesPresent(new String[] { VALIDATION_WOHNSITZ_ANTEIL_FIELD_REQUIRED_NULL_MESSAGE }, gesuch);
	}

	@Test
	void testNullLebenslaufItemArtValidationError() {
		LebenslaufItem lebenslaufItem = new LebenslaufItem();
		Set<LebenslaufItem> lebenslaufItemSet = new HashSet<>();
		lebenslaufItemSet.add(lebenslaufItem);
		Gesuch gesuch = prepareDummyGesuch();
		gesuch.getGesuchFormularToWorkWith().setLebenslaufItems(lebenslaufItemSet);
		assertAllMessagesPresent(new String[] { VALIDATION_LEBENSLAUFITEM_ART_FIELD_REQUIRED_MESSAGE }, gesuch);
	}

	@Test
	void testLebenslaufItemArtValidationError() {

		LebenslaufItem lebenslaufItem = new LebenslaufItem();
		lebenslaufItem.setBildungsart(Bildungsart.FACHHOCHSCHULEN);
		lebenslaufItem.setTaetigskeitsart(Taetigskeitsart.ERWERBSTAETIGKEIT);
		Set<LebenslaufItem> lebenslaufItemSet = new HashSet<>();
		lebenslaufItemSet.add(lebenslaufItem);
		Gesuch gesuch = prepareDummyGesuch();
		gesuch.getGesuchFormularToWorkWith().setLebenslaufItems(lebenslaufItemSet);
		assertAllMessagesPresent(new String[] { VALIDATION_LEBENSLAUFITEM_ART_FIELD_REQUIRED_NULL_MESSAGE }, gesuch);
	}

	@Test
	void testGesuchEinreichenValidationEltern() {
		Familiensituation familiensituation = new Familiensituation();
		familiensituation.setElternteilUnbekanntVerstorben(true);
		familiensituation.setVaterUnbekanntVerstorben(ElternAbwesenheitsGrund.VERSTORBEN);
		familiensituation.setMutterUnbekanntVerstorben(ElternAbwesenheitsGrund.WEDER_NOCH);
		Gesuch gesuch = prepareDummyGesuch();
		gesuch.getGesuchFormularToWorkWith().setFamiliensituation(familiensituation);
		Set<ConstraintViolation<Gesuch>> violations = validator.validate(gesuch,
				GesuchEinreichenValidationGroup.class);
		assertThat(violations.stream()
				.anyMatch(gesuchConstraintViolation -> gesuchConstraintViolation.getMessageTemplate()
						.equals(VALIDATION_FAMILIENSITUATION_ELTERN_ENTITY_REQUIRED_MESSAGE)), is(true));
		Eltern eltern = new Eltern();
		eltern.setElternTyp(ElternTyp.MUTTER);
		Set<Eltern> elternSet = new HashSet<>();
		elternSet.add(eltern);
		gesuch.getGesuchFormularToWorkWith().setElterns(elternSet);
		violations = validator.validate(gesuch, GesuchEinreichenValidationGroup.class);
		assertThat(violations.stream()
				.anyMatch(gesuchConstraintViolation -> gesuchConstraintViolation.getMessageTemplate()
						.equals(VALIDATION_FAMILIENSITUATION_ELTERN_ENTITY_REQUIRED_MESSAGE)), is(false));
	}

	@Test
	void testGesuchEinreichenValidationLebenslauf() {
		LebenslaufItem lebenslaufItem = new LebenslaufItem();
		lebenslaufItem.setBildungsart(Bildungsart.FACHHOCHSCHULEN);
		lebenslaufItem.setTaetigskeitsart(Taetigskeitsart.ERWERBSTAETIGKEIT);
		lebenslaufItem.setVon(LocalDate.of(2020, 10, 1));
		lebenslaufItem.setBis(LocalDate.of(2020, 12, 1));
		Set<LebenslaufItem> lebenslaufItemSet = new HashSet<>();
		lebenslaufItemSet.add(lebenslaufItem);
		Gesuch gesuch = prepareDummyGesuch();
		gesuch.getGesuchFormularToWorkWith().setLebenslaufItems(lebenslaufItemSet);
		PersonInAusbildung personInAusbildung = new PersonInAusbildung();
		personInAusbildung.setGeburtsdatum(LocalDate.of(2000, 5, 12));
		personInAusbildung.setZivilstand(Zivilstand.LEDIG);
		gesuch.getGesuchFormularToWorkWith().setPersonInAusbildung(personInAusbildung);
		Ausbildung ausbildung = new Ausbildung();
		ausbildung.setAusbildungBegin(LocalDate.of(2024, 01, 01));
		gesuch.getGesuchFormularToWorkWith().setAusbildung(ausbildung);
		Set<ConstraintViolation<Gesuch>> violations = validator.validate(gesuch,
				GesuchEinreichenValidationGroup.class);
		assertThat(violations.stream()
				.anyMatch(gesuchConstraintViolation -> gesuchConstraintViolation.getMessageTemplate()
						.equals(VALIDATION_LEBENSLAUF_LUCKENLOS_MESSAGE)), is(true));
	}

	@Test
	void testGesuchEinreichenValidationEinnahmenKostenEltern() {
		Familiensituation familiensituation = new Familiensituation();
		familiensituation.setElternteilUnbekanntVerstorben(true);
		familiensituation.setVaterUnbekanntVerstorben(ElternAbwesenheitsGrund.VERSTORBEN);
		familiensituation.setMutterUnbekanntVerstorben(ElternAbwesenheitsGrund.WEDER_NOCH);
		familiensituation.setGerichtlicheAlimentenregelung(true);
		Gesuch gesuch = prepareDummyGesuch();
		gesuch.getGesuchFormularToWorkWith().setFamiliensituation(familiensituation);
		EinnahmenKosten einnahmenKosten = new EinnahmenKosten();
		gesuch.getGesuchFormularToWorkWith().setEinnahmenKosten(einnahmenKosten);
		Set<ConstraintViolation<Gesuch>> violations = validator.validate(gesuch, GesuchEinreichenValidationGroup.class);
		assertThat(violations.stream()
				.anyMatch(gesuchConstraintViolation -> gesuchConstraintViolation.getMessageTemplate()
						.equals(VALIDATION_EINNAHMEN_KOSTEN_ALIMENTE_REQUIRED_MESSAGE)), is(true));
		assertThat(violations.stream()
				.anyMatch(gesuchConstraintViolation -> gesuchConstraintViolation.getMessageTemplate()
						.equals(VALIDATION_EINNAHMEN_KOSTEN_RENTEN_REQUIRED_MESSAGE)), is(true));
	}

	@Test
	void testGesuchEinreichenValidationEinnahmenKostenPersonInAusbildung() {
		PersonInAusbildung personInAusbildung = new PersonInAusbildung();
		personInAusbildung.setKinder(true);
		personInAusbildung.setGeburtsdatum(LocalDate.of(2000, 5, 12));
		Gesuch gesuch = prepareDummyGesuch();
		gesuch.getGesuchFormularToWorkWith().setPersonInAusbildung(personInAusbildung);
		personInAusbildung.setZivilstand(Zivilstand.LEDIG);
		EinnahmenKosten einnahmenKosten = new EinnahmenKosten();
		gesuch.getGesuchFormularToWorkWith().setEinnahmenKosten(einnahmenKosten);
		Set<ConstraintViolation<Gesuch>> violations = validator.validate(gesuch, GesuchEinreichenValidationGroup.class);
		assertThat(violations.stream()
				.anyMatch(gesuchConstraintViolation -> gesuchConstraintViolation.getMessageTemplate()
						.equals(VALIDATION_EINNAHMEN_KOSTEN_ZULAGEN_REQUIRED_MESSAGE)), is(true));
		assertThat(violations.stream()
				.anyMatch(gesuchConstraintViolation -> gesuchConstraintViolation.getMessageTemplate()
						.equals(VALIDATION_EINNAHMEN_KOSTEN_DARLEHEN_REQUIRED_MESSAGE)), is(true));
	}

	void testGesuchEinreichenValidationEinnahmenKostenAusbildung() {
		Ausbildung ausbildung = new Ausbildung();
		ausbildung.setAusbildungsgang(new Ausbildungsgang());
		ausbildung.getAusbildungsgang().setAusbildungsrichtung(Bildungsart.BERUFSMATURITAET_NACH_LEHRE);
		Gesuch gesuch = prepareDummyGesuch();
		gesuch.getGesuchFormularToWorkWith().setAusbildung(ausbildung);
		EinnahmenKosten einnahmenKosten = new EinnahmenKosten();
		gesuch.getGesuchFormularToWorkWith().setEinnahmenKosten(einnahmenKosten);
		Set<ConstraintViolation<Gesuch>> violations = validator.validate(gesuch, GesuchEinreichenValidationGroup.class);
		assertThat(violations.stream()
				.anyMatch(gesuchConstraintViolation -> gesuchConstraintViolation.getMessageTemplate()
						.equals(VALIDATION_EINNAHMEN_KOSTEN_AUSBILDUNGSKOSTEN_STUFE2_REQUIRED_MESSAGE)), is(true));
	}

	private void assertAllMessagesPresent(String[] messages, Gesuch gesuch) {
		assertAll(messages, gesuch, true);
	}

	private void assertAllMessagesNotPresent(String[] messages, Gesuch gesuch) {
		assertAll(messages, gesuch, false);
	}

	private void assertAll(String[] messages, Gesuch gesuch, boolean expected) {
		Set<ConstraintViolation<Gesuch>> violations = validator.validate(gesuch);
		if (expected) {
			assertThat(violations.size() >= messages.length, is(true));
		}
		for (String message : messages) {
			assertThat(violations.stream()
					.anyMatch(gesuchConstraintViolation -> gesuchConstraintViolation.getMessageTemplate()
							.equals(message)), is(expected));
		}
	}

	private Gesuch prepareDummyGesuch() {
		Gesuch gesuch = new Gesuch();
		gesuch.setGesuchFormularToWorkWith(new GesuchFormular());
		gesuch.setFall(new Fall());
		gesuch.setGesuchsperiode(new Gesuchsperiode());
		return gesuch;
	}
}
