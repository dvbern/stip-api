package ch.dvbern.stip.fall.service;

import ch.dvbern.stip.fall.entity.Fall;
import ch.dvbern.stip.fall.repo.FallRepository;
import ch.dvbern.stip.generated.dto.FallDto;
import jakarta.enterprise.context.RequestScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@RequestScoped
@RequiredArgsConstructor
public class FallService {

    private final FallMapper fallMapper;
    private final FallRepository fallRepository;

    @Transactional
    public FallDto createFall() {
        var fall = new Fall();
        fallRepository.persist(fall);
        return fallMapper.toDto(fall);
    }

    public Optional<FallDto> getFall(UUID id) {
        var optionalFall = fallRepository.findByIdOptional(id);
        return optionalFall.map(fallMapper::toDto);
    }
}
