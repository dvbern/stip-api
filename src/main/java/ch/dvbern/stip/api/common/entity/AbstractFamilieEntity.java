package ch.dvbern.stip.api.common.entity;

import ch.dvbern.stip.api.common.type.Wohnsitz;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;

@MappedSuperclass
@Audited
@Getter
@Setter
public abstract class AbstractFamilieEntity extends AbstractPerson {

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Wohnsitz wohnsitz;

	@Column(nullable = true)
	private BigDecimal wohnsitzAnteilMutter;

	@Column(nullable = true)
	private BigDecimal wohnsitzAnteilVater;
}
