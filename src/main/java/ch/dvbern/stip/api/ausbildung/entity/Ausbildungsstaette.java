package ch.dvbern.stip.api.ausbildung.entity;

import ch.dvbern.stip.api.ausbildung.type.Ausbildungsland;
import ch.dvbern.stip.api.common.entity.AbstractEntity;
import ch.dvbern.stip.api.common.util.Constants;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.util.List;

@Audited
@Entity
@Table(indexes = {
		@Index(name = "IX_ausbildungsstaette_mandant", columnList = "mandant")
})
@Getter
@Setter
public class Ausbildungsstaette extends AbstractEntity {

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "ausbildungsstaette")
	private List<Ausbildungsgang> ausbildungsgaenge;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Ausbildungsland ausbildungsland;

	@NotNull
	@Size(max = Constants.DB_DEFAULT_MAX_LENGTH)
	@Column(nullable = false)
	private String name;
}
