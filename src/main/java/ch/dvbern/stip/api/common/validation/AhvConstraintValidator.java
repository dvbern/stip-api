package ch.dvbern.stip.api.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AhvConstraintValidator implements ConstraintValidator<AhvConstraint, String> {
	private static final String START_DIGITS = "756";

	boolean optional = false;

	@Override
	public void initialize(AhvConstraint constraintAnnotation) {
		optional = constraintAnnotation.optional();
	}

	@Override
	public boolean isValid(String ahvNummer, ConstraintValidatorContext constraintValidatorContext) {

		if (ahvNummer == null && !optional) return false;
		if (ahvNummer == null) return true;

		String cleanedAhv = ahvNummer.replace(".", "");
		char[] digitsArray = cleanedAhv.toCharArray();

		if (digitsArray.length != 13) {
			return false;
		}

		int[] relevantDigits = new int[12];
		for (int i = 0; i < 12; i++) {
			relevantDigits[i] = Character.getNumericValue(digitsArray[11 - i]);
		}

		int relevantDigitsSum = 0;
		for (int i = 0; i < relevantDigits.length; i++) {
			int multiplier = i % 2 == 0 ? 3 : 1;
			relevantDigitsSum += relevantDigits[i] * multiplier;
		}

		int relevantDigitsRounded = (int) Math.ceil(relevantDigitsSum / 10.0) * 10;
		int calculatedCheckDigit = relevantDigitsRounded - relevantDigitsSum;
		int checkDigit = Character.getNumericValue(digitsArray[12]);

		String startDigits = ahvNummer.substring(0, 3);

		return checkDigit == calculatedCheckDigit && startDigits.equals(START_DIGITS);
	}
}
