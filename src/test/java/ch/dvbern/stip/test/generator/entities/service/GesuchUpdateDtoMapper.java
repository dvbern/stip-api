package ch.dvbern.stip.test.generator.entities.service;

import ch.dvbern.oss.stip.contract.test.dto.GesuchUpdateDtoSpec;
import ch.dvbern.stip.api.common.service.MappingConfig;
import ch.dvbern.stip.generated.dto.GesuchUpdateDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = MappingConfig.class)
public interface GesuchUpdateDtoMapper {
	GesuchUpdateDto toEntity(GesuchUpdateDtoSpec gesuchUpdateDtoSpec);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	GesuchUpdateDto partialUpdate(
			GesuchUpdateDtoSpec gesuchUpdateDtoSpec,
			@MappingTarget GesuchUpdateDto gesuchUpdateDto);
}