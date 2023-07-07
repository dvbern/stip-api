package ch.dvbern.stip.api.gesuch.service;

import ch.dvbern.stip.api.adresse.service.AdresseMapper;
import ch.dvbern.stip.api.ausbildung.service.AusbildungMapper;
import ch.dvbern.stip.api.familiensituation.service.FamiliensituationMapper;
import ch.dvbern.stip.api.gesuch.entity.GesuchFormular;
import ch.dvbern.stip.api.common.service.MappingConfig;
import ch.dvbern.stip.api.personinausbildung.service.PersonInAusbildungMapper;
import ch.dvbern.stip.generated.dto.GesuchFormularDto;
import ch.dvbern.stip.generated.dto.GesuchFormularUpdateDto;
import org.mapstruct.*;

@Mapper(config = MappingConfig.class,
        uses =
                {
                        AdresseMapper.class,
                        PersonInAusbildungMapper.class,
                        FamiliensituationMapper.class,
                        AusbildungMapper.class})
public interface GesuchFormularMapper {
    GesuchFormular toEntity(GesuchFormularDto gesuchFormularDto);
    GesuchFormularDto toDto(GesuchFormular gesuchFormular);

    /**
     * partial update mapper for the Gesuchssteller
     * @param gesuchFormularUpdateDto
     * @param gesuchFormular
     * @return
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    GesuchFormular partialUpdate(GesuchFormularUpdateDto gesuchFormularUpdateDto, @MappingTarget GesuchFormular gesuchFormular);
}