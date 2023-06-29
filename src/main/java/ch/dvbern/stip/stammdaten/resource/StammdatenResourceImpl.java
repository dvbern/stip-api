package ch.dvbern.stip.stammdaten.resource;

import ch.dvbern.stip.generated.api.StammdatenResource;
import ch.dvbern.stip.stammdaten.service.LandService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@RequestScoped
@RequiredArgsConstructor
public class StammdatenResourceImpl implements StammdatenResource {

    private final LandService landService;

    @Override
    public Response getLaender() {
        var laender = landService.getAllLaender();
        return Response.ok(laender).build();
    }
}