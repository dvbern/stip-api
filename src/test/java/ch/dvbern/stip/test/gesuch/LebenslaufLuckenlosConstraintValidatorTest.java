package ch.dvbern.stip.test.gesuch;

import ch.dvbern.stip.api.ausbildung.entity.Ausbildung;
import ch.dvbern.stip.api.gesuch.entity.GesuchFormular;
import ch.dvbern.stip.api.gesuch.entity.LebenslaufLuckenlosConstraintValidator;
import ch.dvbern.stip.api.lebenslauf.entity.LebenslaufItem;
import ch.dvbern.stip.api.personinausbildung.entity.PersonInAusbildung;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class LebenslaufLuckenlosConstraintValidatorTest {
	LebenslaufLuckenlosConstraintValidator lebenslaufLuckenlosConstraintValidator =
			new LebenslaufLuckenlosConstraintValidator();

	@Test
	void isLebenslaufLuckenlosOkTest() {
		GesuchFormular gesuchFormular = initFormular();
		LebenslaufItem lebenslaufItem = createLebenslaufItem(
				LocalDate.of(2016, 8, 1),
				LocalDate.of(2023, 12, 31));
		Set<LebenslaufItem> lebenslaufItemSet = new HashSet<>();
		lebenslaufItemSet.add(lebenslaufItem);
		gesuchFormular.setLebenslaufItems(lebenslaufItemSet);
		assertThat(lebenslaufLuckenlosConstraintValidator.isValid(gesuchFormular, null)
				, is(true));
		lebenslaufItem.setBis(LocalDate.of(2022, 11, 30));
		LebenslaufItem lebenslaufItemZwei = createLebenslaufItem(
				LocalDate.of(2022, 8, 1),
				LocalDate.of(2023, 12, 31));
		lebenslaufItemSet.clear();
		lebenslaufItemSet.add(lebenslaufItem);
		lebenslaufItemSet.add(lebenslaufItemZwei);
		gesuchFormular.setLebenslaufItems(lebenslaufItemSet);
		assertThat(lebenslaufLuckenlosConstraintValidator.isValid(gesuchFormular, null), is(true));
	}

	@Test
	void lebenslaufLuckenlosStartZuFruehTest() {
		GesuchFormular gesuchFormular = initFormularWithLebenslaufItem(
				LocalDate.of(2015, 10, 1),
				LocalDate.of(2023, 12, 31));
		assertThat(lebenslaufLuckenlosConstraintValidator.isValid(gesuchFormular, null), is(false));
	}

	@Test
	void lebenslaufLuckenlosStartZuSpaetTest() {
		GesuchFormular gesuchFormular = initFormularWithLebenslaufItem(
				LocalDate.of(2025, 10, 1),
				LocalDate.of(2027, 12, 31));
		assertThat(lebenslaufLuckenlosConstraintValidator.isValid(gesuchFormular, null), is(false));
	}

	@Test
	void lebenslaufLuckenlosEndZuSpaetTest() {
		GesuchFormular gesuchFormular = initFormularWithLebenslaufItem(
				LocalDate.of(2017, 10, 1),
				LocalDate.of(2023, 12, 31));
		assertThat(lebenslaufLuckenlosConstraintValidator.isValid(gesuchFormular, null), is(false));
	}

	@Test
	void lebenslaufLuckenlosStopZuSpaetTest() {
		GesuchFormular gesuchFormular = initFormularWithLebenslaufItem(
				LocalDate.of(2016, 8, 1),
				LocalDate.of(2024, 12, 31));
		assertThat(lebenslaufLuckenlosConstraintValidator.isValid(gesuchFormular, null), is(false));
	}

	@Test
	void lebenslaufItemStopZuFruehTest() {
		GesuchFormular gesuchFormular = initFormularWithLebenslaufItem(
				LocalDate.of(2016, 8, 1),
				LocalDate.of(2022, 6, 30));

		LebenslaufItem lebenslaufItemZwei = createLebenslaufItem(
				LocalDate.of(2022, 7, 1),
				LocalDate.of(2023, 11, 30));
		gesuchFormular.getLebenslaufItems().add(lebenslaufItemZwei);

		assertThat(lebenslaufLuckenlosConstraintValidator.isValid(gesuchFormular, null), is(false));
	}

	@Test
	void isLebenslaufLuckenlosKoTest() {
		GesuchFormular gesuchFormular = initFormularWithLebenslaufItem(
				LocalDate.of(2016, 8, 1),
				LocalDate.of(2022, 6, 30));

		LebenslaufItem lebenslaufItemZwei =
				createLebenslaufItem(
					LocalDate.of(2022, 8, 1),
					LocalDate.of(2023, 12, 31));
		gesuchFormular.getLebenslaufItems().add(lebenslaufItemZwei);

		assertThat(lebenslaufLuckenlosConstraintValidator.isValid(gesuchFormular, null), is(false));
	}

	@NotNull
	private GesuchFormular initFormularWithLebenslaufItem(LocalDate von, LocalDate bis) {
		Set<LebenslaufItem> lebenslaufItemSet = new HashSet<>();
		lebenslaufItemSet.add(createLebenslaufItem(von, bis));

		return initFormular().setLebenslaufItems(lebenslaufItemSet);
	}

	@NotNull
	private static LebenslaufItem createLebenslaufItem(LocalDate von, LocalDate bis) {
		return new LebenslaufItem()
				.setVon(von)
				.setBis(bis);
	}

	private GesuchFormular initFormular() {
		GesuchFormular gesuchFormular = new GesuchFormular();
		PersonInAusbildung personInAusbildung = new PersonInAusbildung();
		personInAusbildung.setGeburtsdatum(LocalDate.of(2000, 5, 12));
		gesuchFormular.setPersonInAusbildung(personInAusbildung);
		Ausbildung ausbildung = new Ausbildung();
		ausbildung.setAusbildungBegin(LocalDate.of(2024, 01, 01));
		gesuchFormular.setAusbildung(ausbildung);
		return gesuchFormular;
	}
}
