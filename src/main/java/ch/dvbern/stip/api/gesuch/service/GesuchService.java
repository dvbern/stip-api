/*
 * Copyright (C) 2023 DV Bern AG, Switzerland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ch.dvbern.stip.api.gesuch.service;

import ch.dvbern.stip.api.common.exception.ValidationsException;
import ch.dvbern.stip.api.common.type.Wohnsitz;
import ch.dvbern.stip.api.dokument.repo.GesuchDokumentRepository;
import ch.dvbern.stip.api.eltern.type.ElternTyp;
import ch.dvbern.stip.api.familiensituation.type.ElternAbwesenheitsGrund;
import ch.dvbern.stip.api.familiensituation.type.Elternschaftsteilung;
import ch.dvbern.stip.api.gesuch.entity.Gesuch;
import ch.dvbern.stip.api.gesuch.entity.GesuchEinreichenValidationGroup;
import ch.dvbern.stip.api.gesuch.entity.GesuchFormular;
import ch.dvbern.stip.api.gesuch.repo.GesuchRepository;
import ch.dvbern.stip.api.gesuch.type.GesuchStatusChangeEvent;
import ch.dvbern.stip.api.gesuch.type.Gesuchstatus;
import ch.dvbern.stip.generated.dto.GesuchCreateDto;
import ch.dvbern.stip.generated.dto.GesuchDto;
import ch.dvbern.stip.generated.dto.GesuchFormularUpdateDto;
import ch.dvbern.stip.generated.dto.GesuchUpdateDto;
import jakarta.enterprise.context.RequestScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

import java.util.*;

@RequestScoped
@RequiredArgsConstructor
public class GesuchService {

	private final GesuchRepository gesuchRepository;

	private final GesuchMapper gesuchMapper;

	private final Validator validator;

	private final GesuchStatusService gesuchStatusService;

	private final GesuchDokumentRepository gesuchDokumentRepository;

	public Optional<GesuchDto> findGesuch(UUID id) {
		return gesuchRepository.findByIdOptional(id).map(gesuchMapper::toDto);
	}

	@Transactional
	public void updateGesuch(UUID gesuchId, GesuchUpdateDto gesuchUpdateDto) throws ValidationsException {
		var gesuch = gesuchRepository.requireById(gesuchId);
		resetFieldsOnUpdate(gesuch.getGesuchFormularToWorkWith(), gesuchUpdateDto.getGesuchFormularToWorkWith());
		gesuchMapper.partialUpdate(gesuchUpdateDto, gesuch);
		preventUpdateVonGesuchIfReadOnyl(gesuch);
		Set<ConstraintViolation<Gesuch>> violations = validator.validate(gesuch);
		if (!violations.isEmpty()) {
			throw new ValidationsException("Die Entität ist nicht valid", violations);
		}
	}

	public List<GesuchDto> findAllWithPersonInAusbildung() {
		return gesuchRepository.findAllWithFormularToWorkWith()
				.filter(gesuch -> gesuch.getGesuchFormularToWorkWith().getPersonInAusbildung() != null)
				.map(gesuchMapper::toDto)
				.toList();
	}

	@Transactional
	public GesuchDto createGesuch(GesuchCreateDto gesuchCreateDto) {
		Gesuch gesuch = gesuchMapper.toNewEntity(gesuchCreateDto);
		gesuchRepository.persist(gesuch);
		return gesuchMapper.toDto(gesuch);
	}

	public List<GesuchDto> findAllForBenutzer(UUID benutzerId) {
		return gesuchRepository.findAllForBenutzer(benutzerId).map(gesuchMapper::toDto).toList();
	}

	public List<GesuchDto> findAllForFall(UUID fallId) {
		return gesuchRepository.findAllForFall(fallId).map(gesuchMapper::toDto).toList();
	}

	@Transactional
	public void deleteGesuch(UUID gesuchId) {
		Gesuch gesuch = gesuchRepository.requireById(gesuchId);
		preventUpdateVonGesuchIfReadOnyl(gesuch);
		gesuchRepository.delete(gesuch);
	}

	@Transactional
	public void gesuchEinreichen(UUID gesuchId) {
		Gesuch gesuch = gesuchRepository.requireById(gesuchId);
		if (gesuch.getGesuchFormularToWorkWith().getFamiliensituation() == null) {
			throw new ValidationsException("Es fehlt Formular Teilen um das Gesuch einreichen zu koennen", null);
		}
		Set<ConstraintViolation<Gesuch>> violations = validator.validate(gesuch);
		Set<ConstraintViolation<Gesuch>> violationsEinreichen =
				validator.validate(gesuch, GesuchEinreichenValidationGroup.class);
		if (!violations.isEmpty() || !violationsEinreichen.isEmpty()) {
			Set<ConstraintViolation<Gesuch>> concatenatedViolations = new HashSet<>(violations);
			concatenatedViolations.addAll(violationsEinreichen);
			throw new ValidationsException(
					"Die Entität ist nicht valid und kann damit nicht eingereicht werden: ", concatenatedViolations);
		}
		if (gesuchDokumentRepository.findAllForGesuch(gesuchId).count() == 0) {
			gesuchStatusService.triggerStateMachineEvent(gesuch, GesuchStatusChangeEvent.DOKUMENT_FEHLT_EVENT);
		} else {
			gesuchStatusService.triggerStateMachineEvent(gesuch, GesuchStatusChangeEvent.GESUCH_EINREICHEN_EVENT);
		}
	}

	@Transactional
	public void setDokumentNachfrist(UUID gesuchId) {
		Gesuch gesuch = gesuchRepository.requireById(gesuchId);
		gesuchStatusService.triggerStateMachineEvent(gesuch, GesuchStatusChangeEvent.DOKUMENT_FEHLT_NACHFRIST_EVENT);
	}

	private void resetFieldsOnUpdate(GesuchFormular toUpdate, GesuchFormularUpdateDto update) {
		if (toUpdate == null || update == null) {
			return;
		}

		if (hasGeburtsdatumOfPersonInAusbildungChanged(toUpdate, update)) {
			update.setLebenslaufItems(new ArrayList<>());
		}

		if (hasZivilstandChangedToOnePerson(toUpdate, update)) {
			toUpdate.setPartner(null);
			update.setPartner(null);
		}
		resetAuswaertigesMittagessen(toUpdate, update);
		resetEltern(toUpdate, update);
		resetAlimente(toUpdate, update);
	}

	private void resetAlimente(GesuchFormular toUpdate, GesuchFormularUpdateDto update) {
		if (hasGerichtlicheAlimenteregelungChanged(toUpdate, update)) {
			if (toUpdate.getEinnahmenKosten() != null) {
				toUpdate.getEinnahmenKosten().setAlimente(null);
			}

			if (update.getEinnahmenKosten() != null) {
				update.getEinnahmenKosten().setAlimente(null);
			}
		}
	}

	private boolean hasGerichtlicheAlimenteregelungChanged(GesuchFormular toUpdate, GesuchFormularUpdateDto update) {
		if (update.getFamiliensituation() == null) {
			return false;
		}

		if (toUpdate.getFamiliensituation() == null) {
			return update.getFamiliensituation().getGerichtlicheAlimentenregelung() != null;
		}

		if (toUpdate.getFamiliensituation().getGerichtlicheAlimentenregelung() == null) {
			return update.getFamiliensituation().getGerichtlicheAlimentenregelung() != null;
		}

		return !toUpdate.getFamiliensituation().getGerichtlicheAlimentenregelung()
				.equals(update.getFamiliensituation().getGerichtlicheAlimentenregelung());
	}

	private void resetEltern(GesuchFormular toUpdate, GesuchFormularUpdateDto update) {
		if (update.getElterns() == null) {
			return;
		}

		if (hasAlimenteAufteilungChangedToBoth(toUpdate, update)) {
			update.setElterns(new ArrayList<>());
			return;
		}

		if (isElternteilVerstorbenOrUnbekannt(update)) {
			resetVerstorbenOrUnbekannteElternteile(update);
		}
	}

	private boolean isElternteilVerstorbenOrUnbekannt(GesuchFormularUpdateDto update) {
		return update.getFamiliensituation() != null
				&& Boolean.TRUE.equals(update.getFamiliensituation().getElternteilUnbekanntVerstorben());
	}

	private void resetVerstorbenOrUnbekannteElternteile(GesuchFormularUpdateDto update) {
		if (update.getFamiliensituation().getMutterUnbekanntVerstorben() != ElternAbwesenheitsGrund.WEDER_NOCH) {
			update.getElterns().removeIf(eltern -> eltern.getElternTyp() == ElternTyp.MUTTER);
		}

		if (update.getFamiliensituation().getVaterUnbekanntVerstorben() != ElternAbwesenheitsGrund.WEDER_NOCH) {
			update.getElterns().removeIf(eltern -> eltern.getElternTyp() == ElternTyp.VATER);
		}
	}

	private void resetAuswaertigesMittagessen(GesuchFormular toUpdate, GesuchFormularUpdateDto update) {
		if (!isUpdateToEigenerHaushalt(update)) {
			return;
		}

		if (toUpdate.getEinnahmenKosten() != null) {
			toUpdate.getEinnahmenKosten().setAuswaertigeMittagessenProWoche(null);
		}

		if (update.getEinnahmenKosten() != null) {
			update.getEinnahmenKosten().setAuswaertigeMittagessenProWoche(null);
		}
	}

	private boolean hasAlimenteAufteilungChangedToBoth(GesuchFormular toUpdate, GesuchFormularUpdateDto update) {
		if (toUpdate.getFamiliensituation() == null || update.getFamiliensituation() == null) {
			return false;
		}

		return toUpdate.getFamiliensituation().getWerZahltAlimente() != Elternschaftsteilung.GEMEINSAM &&
				update.getFamiliensituation().getWerZahltAlimente() == Elternschaftsteilung.GEMEINSAM;
	}

	private boolean hasGeburtsdatumOfPersonInAusbildungChanged(
			GesuchFormular toUpdate,
			GesuchFormularUpdateDto update) {
		if (toUpdate.getPersonInAusbildung() == null
				|| toUpdate.getPersonInAusbildung().getGeburtsdatum() == null
				|| update.getPersonInAusbildung() == null) {
			return false;
		}

		return !toUpdate.getPersonInAusbildung()
				.getGeburtsdatum()
				.equals(update.getPersonInAusbildung().getGeburtsdatum());
	}

	private boolean isUpdateToEigenerHaushalt(GesuchFormularUpdateDto update) {
		if (update.getPersonInAusbildung() == null) {
			return false;
		}

		return update.getPersonInAusbildung().getWohnsitz() == Wohnsitz.EIGENER_HAUSHALT;
	}

	private boolean hasZivilstandChangedToOnePerson(GesuchFormular toUpdate, GesuchFormularUpdateDto update) {
		if (toUpdate.getPersonInAusbildung() == null
				|| toUpdate.getPersonInAusbildung().getZivilstand() == null
				|| update.getPersonInAusbildung() == null
				|| update.getPersonInAusbildung().getZivilstand() == null) {
			return false;
		}

		return toUpdate.getPersonInAusbildung().getZivilstand().hasPartnerschaft() &&
				!update.getPersonInAusbildung().getZivilstand().hasPartnerschaft();
	}

	private void preventUpdateVonGesuchIfReadOnyl(Gesuch gesuch) {
		if (Gesuchstatus.readonlyGesuchStatusList.contains(gesuch.getGesuchStatus())) {
			throw new IllegalStateException("Cannot update or delete das Gesuchsformular when parent status is: "
					+ gesuch.getGesuchStatus());
		}
	}
}
