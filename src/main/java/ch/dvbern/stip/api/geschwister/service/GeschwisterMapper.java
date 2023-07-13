package ch.dvbern.stip.api.geschwister.service;

import ch.dvbern.stip.api.common.service.MappingConfig;
import ch.dvbern.stip.api.geschwister.entity.Geschwister;
import ch.dvbern.stip.generated.dto.GeschwisterDto;
import ch.dvbern.stip.generated.dto.GeschwisterUpdateDto;
import jakarta.ws.rs.NotFoundException;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

@Mapper(config = MappingConfig.class)
public interface GeschwisterMapper {
    Geschwister toEntity(GeschwisterDto geschwisterDto);

    GeschwisterDto toDto(Geschwister geschwister);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Geschwister partialUpdate(GeschwisterUpdateDto geschwisterUpdateDto, @MappingTarget Geschwister geschwister);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    default Set<Geschwister> map(List<GeschwisterUpdateDto> geschwisterUpdateDtos, @MappingTarget Set<Geschwister> geschwisterSet) {
        for (GeschwisterUpdateDto geschwisterUpdateDto : geschwisterUpdateDtos) {
            if (geschwisterUpdateDto.getId() != null) {
                Geschwister found = geschwisterSet.stream().filter(geschwister -> geschwister.getId().equals(geschwisterUpdateDto.getId())).findFirst().orElseThrow(
                        () -> new NotFoundException("geschwister Not FOUND")
                );
                geschwisterSet.remove(found);
                geschwisterSet.add(partialUpdate(geschwisterUpdateDto, found));
            }
            else {
                geschwisterSet.add(partialUpdate(geschwisterUpdateDto, new Geschwister()));
            }
        }
        return geschwisterSet;
    }
}