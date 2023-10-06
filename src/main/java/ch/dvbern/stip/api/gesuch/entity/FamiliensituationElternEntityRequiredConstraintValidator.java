package ch.dvbern.stip.api.gesuch.entity;

import ch.dvbern.stip.api.common.type.Anrede;
import ch.dvbern.stip.api.eltern.type.ElternTyp;
import ch.dvbern.stip.api.familiensituation.entity.Familiensituation;
import ch.dvbern.stip.api.familiensituation.type.ElternAbwesenheitsGrund;
import ch.dvbern.stip.api.familiensituation.type.Elternschaftsteilung;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FamiliensituationElternEntityRequiredConstraintValidator
		implements ConstraintValidator<FamiliensituationElternEntityRequiredConstraint, GesuchFormular> {
	@Override
	public boolean isValid(
			GesuchFormular gesuchFormular,
			ConstraintValidatorContext constraintValidatorContext) {
		if (gesuchFormular.getFamiliensituation() == null) {
			return false;
		}
		if (isElternTeilRequired(Anrede.FRAU, gesuchFormular.getFamiliensituation())) {
			if (gesuchFormular.getElterns()
					.stream()
					.filter(eltern -> eltern.getElternTyp() == ElternTyp.MUTTER)
					.findAny()
					.isEmpty()) {
				return false;
			}
		} else if (gesuchFormular.getElterns()
				.stream()
				.filter(eltern -> eltern.getElternTyp() == ElternTyp.MUTTER)
				.findAny()
				.isPresent()) {
			return false;
		}
		if (isElternTeilRequired(Anrede.HERR, gesuchFormular.getFamiliensituation())) {
			if (gesuchFormular.getElterns()
					.stream()
					.filter(eltern -> eltern.getElternTyp() == ElternTyp.VATER)
					.findAny()
					.isEmpty()) {
				return false;
			}
		} else if (gesuchFormular.getElterns()
				.stream()
				.filter(eltern -> eltern.getElternTyp() == ElternTyp.VATER)
				.findAny()
				.isPresent()) {
			return false;
		}
		return true;
	}

	private boolean isElternTeilRequired(Anrede geschlecht, Familiensituation familiensituation) {
		boolean elternteilLebt = true;
		if (familiensituation.getElternteilUnbekanntVerstorben()) {
			elternteilLebt = geschlecht == Anrede.HERR ?
					familiensituation.getVaterUnbekanntVerstorben() == ElternAbwesenheitsGrund.WEDER_NOCH
					: familiensituation.getMutterUnbekanntVerstorben() == ElternAbwesenheitsGrund.WEDER_NOCH;
		}
		boolean elternteilKeineAlimente = true;
		if (familiensituation.getWerZahltAlimente() == Elternschaftsteilung.VATER && geschlecht == Anrede.HERR
				|| familiensituation.getWerZahltAlimente() == Elternschaftsteilung.MUTTER && geschlecht == Anrede.FRAU) {
			elternteilKeineAlimente = false;
		}
		return elternteilLebt && elternteilKeineAlimente;
	}
}