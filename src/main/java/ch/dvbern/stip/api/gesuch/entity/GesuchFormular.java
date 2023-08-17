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

package ch.dvbern.stip.api.gesuch.entity;

import ch.dvbern.stip.api.ausbildung.entity.Ausbildung;
import ch.dvbern.stip.api.auszahlung.entity.Auszahlung;
import ch.dvbern.stip.api.common.entity.AbstractEntity;
import ch.dvbern.stip.api.eltern.entity.Eltern;
import ch.dvbern.stip.api.familiensituation.entity.Familiensituation;
import ch.dvbern.stip.api.geschwister.entity.Geschwister;
import ch.dvbern.stip.api.kind.entity.Kind;
import ch.dvbern.stip.api.lebenslauf.entity.LebenslaufItem;
import ch.dvbern.stip.api.partner.entity.Partner;
import ch.dvbern.stip.api.personinausbildung.entity.PersonInAusbildung;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.util.LinkedHashSet;
import java.util.Set;

@Audited
@FamiliensituationElternEntityRequiredConstraint(groups = GesuchEinreichenValidationGroup.class)
@LebenslaufLuckenlosConstraint(groups = GesuchEinreichenValidationGroup.class)
@Entity
@Table(indexes = {
        @Index(name = "IX_gesuch_formular_person_in_ausbildung_id", columnList = "person_in_ausbildung_id"),
        @Index(name = "IX_gesuch_formular_ausbildung_id", columnList = "ausbildung_id"),
        @Index(name = "IX_gesuch_formular_familiensituation_id", columnList = "familiensituation_id"),
        @Index(name = "IX_gesuch_formular_partner_id", columnList = "partner_id"),
        @Index(name = "FK_gesuch_formular_auszahlung_id", columnList = "auszahlung_id"),
})
@Getter
@Setter
public class GesuchFormular extends AbstractEntity {

    @OneToOne(optional = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_gesuch_formular_person_in_ausbildung_id"), nullable = true)
    private @Valid PersonInAusbildung personInAusbildung;

    @OneToOne(optional = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_gesuch_formular_ausbildung_id"), nullable = true)
    private @Valid Ausbildung ausbildung;

    @OneToOne(optional = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_gesuch_formular_familiensituation_id"), nullable = true)
    private @Valid Familiensituation familiensituation;

    @OneToOne(optional = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_gesuch_formular_partner_id"), nullable = true)
    private Partner partner;

    @OneToOne(optional = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_gesuch_formular_auszahlung_id"), nullable = true)
    private Auszahlung auszahlung;

    @Valid
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "gesuch_formular_id", referencedColumnName = "id", nullable = false)
    @OrderBy("von")
    private Set<LebenslaufItem> lebenslaufItems = new LinkedHashSet<>();

    @Valid
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "gesuch_formular_id", referencedColumnName = "id", nullable = false)
    @OrderBy("geburtsdatum")
    private Set<Geschwister> geschwisters = new LinkedHashSet<>();

    @Valid
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "gesuch_formular_id", referencedColumnName = "id", nullable = false)
    private Set<Eltern> elterns = new LinkedHashSet<>();

    @Valid
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "gesuch_formular_id", referencedColumnName = "id", nullable = false)
    @OrderBy("geburtsdatum")
    private Set<Kind> kinds = new LinkedHashSet<>();
}
