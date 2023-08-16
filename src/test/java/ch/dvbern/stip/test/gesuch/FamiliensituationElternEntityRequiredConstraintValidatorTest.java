package ch.dvbern.stip.test.gesuch;

import java.util.HashSet;
import java.util.Set;

import ch.dvbern.stip.api.eltern.entity.Eltern;
import ch.dvbern.stip.api.eltern.type.ElternTyp;
import ch.dvbern.stip.api.familiensituation.entity.Familiensituation;
import ch.dvbern.stip.api.familiensituation.type.ElternAbwesenheitsGrund;
import ch.dvbern.stip.api.gesuch.entity.FamiliensituationElternEntityRequiredConstraintValidator;
import ch.dvbern.stip.api.gesuch.entity.GesuchFormular;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class FamiliensituationElternEntityRequiredConstraintValidatorTest {

	@Test
	void isValidTest() {
		FamiliensituationElternEntityRequiredConstraintValidator
				familiensituationElternEntityRequiredConstraintValidator =
				new FamiliensituationElternEntityRequiredConstraintValidator();

		//Empty FinSit
		assertThat(familiensituationElternEntityRequiredConstraintValidator.isValid(new GesuchFormular(), null)
				, is(false));
		// Beide Eltern sollten vorhanden sein:
		GesuchFormular gesuchFormular = new GesuchFormular();
		Familiensituation familiensituation = new Familiensituation();
		familiensituation.setElternteilUnbekanntVerstorben(false);
		gesuchFormular.setFamiliensituation(familiensituation);
		assertThat(familiensituationElternEntityRequiredConstraintValidator.isValid(gesuchFormular, null)
				, is(false));
		// Add Mutter
		Eltern mutter = new Eltern();
		mutter.setElternTyp(ElternTyp.MUTTER);
		Set<Eltern> elternSet = new HashSet<>();
		elternSet.add(mutter);
		gesuchFormular.setElterns(elternSet);
		assertThat(familiensituationElternEntityRequiredConstraintValidator.isValid(gesuchFormular, null)
				, is(false));
		// Add Vater
		Eltern vater = new Eltern();
		vater.setElternTyp(ElternTyp.VATER);
		elternSet.add(mutter);
		elternSet.add(vater);
		gesuchFormular.setElterns(elternSet);
		assertThat(familiensituationElternEntityRequiredConstraintValidator.isValid(gesuchFormular, null)
				, is(true));

		// Elternteil unbekannt, Vater und Mutter pflichitg
		familiensituation.setElternteilUnbekanntVerstorben(true);
		familiensituation.setVaterUnbekanntVerstorben(ElternAbwesenheitsGrund.WEDER_NOCH);
		familiensituation.setMutterUnbekanntVerstorben(ElternAbwesenheitsGrund.WEDER_NOCH);
		gesuchFormular.setFamiliensituation(familiensituation);
		assertThat(familiensituationElternEntityRequiredConstraintValidator.isValid(gesuchFormular, null)
				, is(true));

		// Elternteil unbekannt, Vater verstorben, Mutter Pflichtig:
		familiensituation.setVaterUnbekanntVerstorben(ElternAbwesenheitsGrund.VERSTORBEN);
		gesuchFormular.setFamiliensituation(familiensituation);
		assertThat(familiensituationElternEntityRequiredConstraintValidator.isValid(gesuchFormular, null)
				, is(false));
		elternSet.clear();
		elternSet.add(mutter);
		gesuchFormular.setElterns(elternSet);
		assertThat(familiensituationElternEntityRequiredConstraintValidator.isValid(gesuchFormular, null)
				, is(true));

		// Elternteil unbekannt, Vater verstorben, Mutter Verstorben:
		familiensituation.setMutterUnbekanntVerstorben(ElternAbwesenheitsGrund.VERSTORBEN);
		gesuchFormular.setFamiliensituation(familiensituation);
		assertThat(familiensituationElternEntityRequiredConstraintValidator.isValid(gesuchFormular, null)
				, is(false));
		gesuchFormular.setElterns(new HashSet<>());
		assertThat(familiensituationElternEntityRequiredConstraintValidator.isValid(gesuchFormular, null)
				, is(true));
	}
}
