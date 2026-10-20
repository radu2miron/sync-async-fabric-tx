package edu.tucn.syncasync.contract;

import edu.tucn.syncasync.model.Asset;
import edu.tucn.syncasync.util.JsonMapper;
import org.hyperledger.fabric.contract.Context;

import java.util.List;
import java.util.Random;

/**
 * @author <a href="mailto:radu.miro@aut.utcluj.ro">Radu Miron</a>
 */

public class AssetService {
    /**
     * Creates a new asset record in the blockchain.
     *
     * @param ctx       the context of the transaction
     * @param assetJson the JSON representation of the event
     */
    public void create(Context ctx, String assetJson) {
        Asset asset = JsonMapper.fromJson(assetJson, Asset.class);
        String existing = ctx.getStub().getStringState(asset.getId());

        if (existing != null && !existing.isEmpty()) {
            throw new RuntimeException("Asset with ID " + asset.getId() + " already exists.");
        }

        ctx.getStub().putStringState(asset.getId(), JsonMapper.toJson(asset));
    }

    /**
     * Updates an existing asset record in the blockchain.
     *
     * @param ctx              the context of the transaction
     * @param updatedAssetJson the updated JSON representation of the asset
     */
    public void update(Context ctx, String updatedAssetJson) {
        Asset asset = JsonMapper.fromJson(updatedAssetJson, Asset.class);
        String eventJson = ctx.getStub().getStringState(asset.getId());

        if (eventJson == null || eventJson.isEmpty()) {
            throw new RuntimeException("Asset with ID " + asset.getId() + " does not exist.");
        }

        ctx.getStub().putStringState(asset.getId(), updatedAssetJson);
    }

    /**
     * Reads an asset record from the blockchain.
     *
     * @param ctx       the context of the transaction
     * @param assetId the ID of the freight
     * @return the fully populated asset
     */
    public Asset read(Context ctx, String assetId) {
        String assetJson = ctx.getStub().getStringState(assetId);

        if (assetJson == null || assetJson.isEmpty()) {
            throw new RuntimeException("Asset with ID " + assetId + " does not exist.");
        }

        // Deserialize the asset JSON to an Asset object
        return JsonMapper.fromJson(assetJson, Asset.class);
    }
}
