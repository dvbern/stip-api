package ch.dvbern.stip.test.gesuch;

import ch.dvbern.stip.api.gesuch.entity.GesuchFormular;
import ch.dvbern.stip.api.gesuch.entity.PartnerNullRequiredWhenAlleinstehendConstraintValidator;
import ch.dvbern.stip.api.partner.entity.Partner;
import ch.dvbern.stip.api.personinausbildung.entity.PersonInAusbildung;
import ch.dvbern.stip.api.personinausbildung.type.Zivilstand;
import ch.dvbern.stip.test.util.TestUtil;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.hibernate.validator.messageinterpolation.ExpressionLanguageFeatureLevel;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class PartnerNullRequiredWhenAllinstehendConstraintValidatorTest {
	PartnerNullRequiredWhenAlleinstehendConstraintValidator validator =
			new PartnerNullRequiredWhenAlleinstehendConstraintValidator();

	@Test
	void personInAusbildungLedigGeschiedenAufgeloestOrVerwittwetAndPartnerNotNullShouldNotBeValid() {
		Zivilstand.getZvilstandsNoPartnerschaft().forEach(zivilstand -> {
			GesuchFormular gesuchFormular = preapreGesuchFormularWithZivilstand(zivilstand, new Partner());
			assertThat(validator.isValid(gesuchFormular, null), is(false));
		});
	}

	@Test
	void personInAusbildungLedigGeschiedenAufgeloestOrVerwittwetAndPartnerNullShouldBeValid() {
		Zivilstand.getZvilstandsNoPartnerschaft().forEach(zivilstand -> {
			GesuchFormular gesuchFormular = preapreGesuchFormularWithZivilstand(zivilstand, null);
			assertThat(validator.isValid(gesuchFormular, null), is(true));
		});
	}

	@Test
	void personInAusbildungVerheiratetKonkubinatPartnerschaftAndPartnerNullShouldNotBeValid() {
		Zivilstand.getZvilstandsWithPartnerschaft().forEach(zivilstand -> {
			GesuchFormular gesuchFormular = preapreGesuchFormularWithZivilstand(zivilstand, null);
			assertThat(validator.isValid(gesuchFormular, TestUtil.initValidatorContext()), is(false));
		});
	}

	@Test
	void personInAusbildungVerheiratetKonkubinatPartnerschaftAndPartnerNotNullShouldBeValid() {
		Zivilstand.getZvilstandsWithPartnerschaft().forEach(zivilstand ->  {
			GesuchFormular gesuchFormular = preapreGesuchFormularWithZivilstand(zivilstand, new Partner());
			assertThat(validator.isValid(gesuchFormular, null), is(true));
		});
	}

	@NotNull
	private static GesuchFormular preapreGesuchFormularWithZivilstand(Zivilstand zivilstand, @Nullable Partner partner) {
		GesuchFormular gesuchFormular = new GesuchFormular()
				.setPartner(partner)
				.setPersonInAusbildung(new PersonInAusbildung().setZivilstand(zivilstand));
		return gesuchFormular;
	}

}
