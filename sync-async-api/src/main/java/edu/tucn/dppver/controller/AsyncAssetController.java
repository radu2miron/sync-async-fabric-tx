package edu.tucn.dppver.controller;

import edu.tucn.dppver.model.Asset;
import edu.tucn.dppver.model.SubmitResponse;
import edu.tucn.dppver.service.AsyncAssetService;
import edu.tucn.dppver.service.SyncAssetService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

/**
 * @author <a href="mailto:radu.miro@aut.utcluj.ro">Radu Miron</a>
 */

@RestController
@RequestMapping("/async")
public class AsyncAssetController {
    private final AsyncAssetService asyncAssetService;

    public AsyncAssetController(AsyncAssetService asyncAssetService) {
        this.asyncAssetService = asyncAssetService;
    }

    @PostMapping
    @SecurityRequirement(name = "Bearer Authentication") // swagger auth
    public SubmitResponse create(@RequestBody Asset asset) {
        return asyncAssetService.create(asset);
    }

    @PutMapping
    @SecurityRequirement(name = "Bearer Authentication") // swagger auth
    public SubmitResponse update(@RequestParam String id, @RequestBody Asset asset) {
        return asyncAssetService.update(id, asset);
    }
}
