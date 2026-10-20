package edu.tucn.syncasync.contract;

import edu.tucn.syncasync.util.JsonMapper;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Transaction;

/**
 * This contract is responsible for managing Asset on the Hyperledger Fabric blockchain.
 *
 * @author <a href="mailto:radu.miro@aut.utcluj.ro">Radu Miron</a>
 */

@Contract(name = "SyncAsyncContract") //
@Default
public class SyncAsyncContract implements ContractInterface {
    private final AssetService assetService = new AssetService();

    /**
     * Creates a new asset entry on the ledger.
     *
     * @param ctx         the transaction context
     * @param assetJson the JSON representation of the asset
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void createAsset(Context ctx, String assetJson) {
        assetService.create(ctx, assetJson);
    }

    /**
     * Updates an existing asset entry on the ledger.
     *
     * @param ctx                the transaction context
     * @param updatedAssetJson the updated JSON representation of the asset
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void updateAsset(Context ctx, String updatedAssetJson) {
        assetService.update(ctx, updatedAssetJson);
    }

    /**
     * Reads an asset entry from the ledger.
     *
     * @param ctx       the transaction context
     * @param assetId the ID of the asset
     * @return the JSON representation of the asset
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String readAsset(Context ctx, String assetId) {
        return JsonMapper.toJson(assetService.read(ctx, assetId));
    }

}
