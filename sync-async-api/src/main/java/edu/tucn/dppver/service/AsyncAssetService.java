package edu.tucn.dppver.service;

import edu.tucn.dppver.model.Asset;
import edu.tucn.dppver.model.SubmitResponse;
import edu.tucn.dppver.model.TransactionRequest;
import edu.tucn.dppver.model.TxType;
import edu.tucn.dppver.queue.BlockchainMessagePublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author <a href="mailto:radu.miro@aut.utcluj.ro">Radu Miron</a>
 */

@Service
public class AsyncAssetService {
    private final BlockchainMessagePublisher blockchainMessagePublisher;

    public AsyncAssetService(BlockchainMessagePublisher blockchainMessagePublisher) {
        this.blockchainMessagePublisher = blockchainMessagePublisher;
    }

    public SubmitResponse create(Asset asset) {
        String id = UUID.randomUUID().toString();
        asset.setId(id);
        blockchainMessagePublisher.sendTransactionRequest( // send message to the blockchain queue
                new TransactionRequest(TxType.SUBMIT, "createAsset", JsonMapper.toJson(asset), System.currentTimeMillis(), id));
        return new SubmitResponse("Asset creation submitted successfully", id);
    }

    public SubmitResponse update(String id, Asset asset) {
        asset.setId(id);
        blockchainMessagePublisher.sendTransactionRequest( // send message to the blockchain queue
                new TransactionRequest(TxType.SUBMIT, "updateAsset", JsonMapper.toJson(asset), System.currentTimeMillis(), id));
        return new SubmitResponse("Asset update submitted successfully", id);
    }
}
