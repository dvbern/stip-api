package ch.dvbern.stip.api.lebenslauf.service;

import ch.dvbern.stip.api.common.service.*;
import ch.dvbern.stip.api.lebenslauf.entity.LebenslaufItem;
import ch.dvbern.stip.generated.dto.LebenslaufItemDto;
import ch.dvbern.stip.generated.dto.LebenslaufItemUpdateDto;
import jakarta.ws.rs.NotFoundException;
import org.mapstruct.*;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Mapper(config = MappingConfig.class)
public interface LebenslaufItemMapper {

    @Mapping(source = "von", target = "von", qualifiedBy = {DateMapper.class, MonthYearToBeginOfMonth.class})
    @Mapping(source = "bis", target = "bis", qualifiedBy = {DateMapper.class, MonthYearToEndOfMonth.class})
    LebenslaufItem toEntity(LebenslaufItemDto lebenslaufItemDto);

    @Mapping(source = "von", target = "von", qualifiedBy = {DateMapper.class, DateToMonthYear.class})
    @Mapping(source = "bis", target = "bis", qualifiedBy = {DateMapper.class, DateToMonthYear.class})
    LebenslaufItemDto toDto(LebenslaufItem lebenslaufItem);

    @Mapping(source = "von", target = "von", qualifiedBy = {DateMapper.class, MonthYearToBeginOfMonth.class})
    @Mapping(source = "bis", target = "bis", qualifiedBy = {DateMapper.class, MonthYearToEndOfMonth.class})
    LebenslaufItem partialUpdate(LebenslaufItemUpdateDto lebenslaufItemUpdateDto, @MappingTarget LebenslaufItem lebenslaufItem);

    default Set<LebenslaufItem> map(List<LebenslaufItemUpdateDto> lebenslaufItemUpdateDtos, @MappingTarget Set<LebenslaufItem> lebenslaufItemSet) {
        if(lebenslaufItemUpdateDtos.isEmpty()) lebenslaufItemSet.clear();
        Iterator<LebenslaufItem> iterator = lebenslaufItemSet.iterator();
        while (iterator.hasNext()) {
            LebenslaufItem lebenslaufItem = iterator.next();
            if (lebenslaufItemUpdateDtos.stream().noneMatch(lebenslaufItemUpdateDto -> lebenslaufItem.getId().equals(lebenslaufItemUpdateDto.getId()))) {
                iterator.remove();
            }
        }
        for (LebenslaufItemUpdateDto lebenslaufItemUpdateDto : lebenslaufItemUpdateDtos) {
            if (lebenslaufItemUpdateDto.getId() != null) {
                LebenslaufItem found = lebenslaufItemSet.stream().filter(lebenslaufItem -> lebenslaufItem.getId().equals(lebenslaufItemUpdateDto.getId())).findFirst().orElseThrow(
                        () -> new NotFoundException("LebenslaufItem Not FOUND")
                );
                lebenslaufItemSet.remove(found);
                lebenslaufItemSet.add(partialUpdate(lebenslaufItemUpdateDto, found));
            }
            else {
                lebenslaufItemSet.add(partialUpdate(lebenslaufItemUpdateDto, new LebenslaufItem()));
            }
        }
        return lebenslaufItemSet;
    }
}