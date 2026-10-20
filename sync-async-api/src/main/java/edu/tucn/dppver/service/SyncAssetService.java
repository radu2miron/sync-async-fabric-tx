package edu.tucn.dppver.service;

import edu.tucn.dppver.model.*;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author <a href="mailto:radu.miro@aut.utcluj.ro">Radu Miron</a>
 */

@Service
public class SyncAssetService {
    private final BlockchainProxy blockchainProxy;

    public SyncAssetService(BlockchainProxy blockchainProxy) {
        this.blockchainProxy = blockchainProxy;
    }

    public SubmitResponse create(Asset asset) {
        String id = UUID.randomUUID().toString();
        asset.setId(id);
        blockchainProxy.execute(// execute transaction in the blockchain
                new TransactionRequest(TxType.SUBMIT, "createAsset", JsonMapper.toJson(asset),0, ""));
        return new SubmitResponse("Asset creation was successful", id);
    }

    public SubmitResponse update(String id, Asset asset) {
        asset.setId(id);
        blockchainProxy.execute(// execute transaction in the blockchain
                new TransactionRequest(TxType.SUBMIT, "updateAsset", JsonMapper.toJson(asset), 0, ""));
        return new SubmitResponse("Asset update was successful", id);
    }

    public Asset read(String id) {
        String response = blockchainProxy.execute(new TransactionRequest(TxType.READ, "readAsset", id, 0, ""));
        return JsonMapper.fromJson(response, Asset.class);
    }
}
