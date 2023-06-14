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

package ch.dvbern.stip.gesuch.dto;

import ch.dvbern.stip.fall.dto.FallDTO;
import ch.dvbern.stip.gesuch.model.Gesuch;
import ch.dvbern.stip.gesuch.model.Gesuchstatus;
import ch.dvbern.stip.gesuchsperiode.dto.GesuchsperiodeDTO;
import ch.dvbern.stip.personinausbildung.dto.PersonInAusbildungContainerDTO;
import ch.dvbern.stip.personinausbildung.model.PersonInAusbildungContainer;
import lombok.Value;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Value
public class GesuchDTO {

    private UUID id;

    @NotNull
    private FallDTO fall;

    @NotNull
    private GesuchsperiodeDTO gesuchsperiode;

    private PersonInAusbildungContainerDTO personInAusbildungContainer;

    @NotNull
    private Gesuchstatus gesuchStatus;

    @NotNull
    private int gesuchNummer;

    public static GesuchDTO from(Gesuch changed) {
        return new GesuchDTO(changed.getId(), FallDTO.from(changed.getFall()), GesuchsperiodeDTO.from(changed.getGesuchsperiode()), PersonInAusbildungContainerDTO.from(changed.getPersonInAusbildungContainer()), changed.getGesuchStatus(), changed.getGesuchNummer());
    }

    public void apply(Gesuch gesuch) {
        if (this.personInAusbildungContainer != null) {
            PersonInAusbildungContainer personInAusbildungContainer1 = gesuch.getPersonInAusbildungContainer() != null ? gesuch.getPersonInAusbildungContainer() : new PersonInAusbildungContainer();
            personInAusbildungContainer.apply(personInAusbildungContainer1);
            gesuch.setPersonInAusbildungContainer(personInAusbildungContainer1);
        }
    }
}
