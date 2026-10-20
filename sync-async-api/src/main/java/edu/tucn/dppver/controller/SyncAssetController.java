package edu.tucn.dppver.controller;

import edu.tucn.dppver.model.Asset;
import edu.tucn.dppver.model.SubmitResponse;
import edu.tucn.dppver.service.SyncAssetService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

/**
 * @author <a href="mailto:radu.miro@aut.utcluj.ro">Radu Miron</a>
 */

@RestController
@RequestMapping("/sync")
public class SyncAssetController {
    private final SyncAssetService syncAssetService;

    public SyncAssetController(SyncAssetService syncAssetService) {
        this.syncAssetService = syncAssetService;
    }

    @PostMapping
    @SecurityRequirement(name = "Bearer Authentication") // swagger auth
    public SubmitResponse create(@RequestBody Asset asset) {
        return syncAssetService.create(asset);
    }

    @PutMapping
    @SecurityRequirement(name = "Bearer Authentication") // swagger auth
    public SubmitResponse update(@RequestParam String id, @RequestBody Asset asset) {
        return syncAssetService.update(id, asset);
    }

    @GetMapping
    @SecurityRequirement(name = "Bearer Authentication") // swagger auth
    public Asset read(@RequestParam String id) {
        return syncAssetService.read(id);
    }
}
