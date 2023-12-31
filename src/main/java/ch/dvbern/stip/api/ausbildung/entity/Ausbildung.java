package ch.dvbern.stip.api.ausbildung.entity;

import ch.dvbern.stip.api.ausbildung.type.AusbildungsPensum;
import ch.dvbern.stip.api.common.entity.AbstractMandantEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.time.LocalDate;

import static ch.dvbern.stip.api.common.util.Constants.DB_DEFAULT_MAX_LENGTH;

@Audited
@AusbildungNichtGefundenRequiredFieldsConstraint
@AusbildungNichtGefundenRequiredNullFieldsConstraint
@Entity
@Table(indexes = {
		@Index(name = "IX_ausbildung_ausbildungsgang_id", columnList = "ausbildungsgang_id"),
		@Index(name = "IX_ausbildung_mandant", columnList = "mandant")
})
@Getter
@Setter
public class Ausbildung extends AbstractMandantEntity {

	@ManyToOne
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_ausbildung_ausbildungsgang_id"))
	private Ausbildungsgang ausbildungsgang;

	@Size(max = DB_DEFAULT_MAX_LENGTH)
	@Column
	private String alternativeAusbildungsgang;

	@Size(max = DB_DEFAULT_MAX_LENGTH)
	@Column
	private String alternativeAusbildungsstaette;

	@NotNull
	@Size(max = DB_DEFAULT_MAX_LENGTH)
	@Column(nullable = false)
	private String fachrichtung;

	@Column(nullable = false)
	private boolean ausbildungNichtGefunden = false;

	@NotNull
	@Column(nullable = false)
	private LocalDate ausbildungBegin;

	@NotNull
	@Column(nullable = false)
	private LocalDate ausbildungEnd;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AusbildungsPensum pensum;
}
