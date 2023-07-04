package ch.dvbern.stip.api.familiensituation.entity;

import ch.dvbern.stip.api.common.entity.AbstractEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

@Audited
@Entity
@Getter
@Setter
public class FamiliensituationContainer extends AbstractEntity {

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = true)
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_familiensituation_container_familiensituation_gs_id"), nullable = true)
    private Familiensituation familiensituationGS;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = true)
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_familiensituation_container_familiensituation_sb_id"), nullable = true)
    private Familiensituation familiensituationSB;
}