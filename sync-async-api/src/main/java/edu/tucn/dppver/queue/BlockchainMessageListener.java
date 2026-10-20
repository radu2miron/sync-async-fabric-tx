package edu.tucn.dppver.queue;

import edu.tucn.dppver.model.TransactionRequest;
import edu.tucn.dppver.service.BlockchainProxy;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class BlockchainMessageListener {
    private final BlockchainProxy blockchainProxy;

    public BlockchainMessageListener(BlockchainProxy blockchainProxy) {
        this.blockchainProxy = blockchainProxy;
    }

    @RabbitListener(queues = RabbitConfig.BLOCKCHAIN_QUEUE,
            concurrency = "50")
    public void receiveTransactionRequest(TransactionRequest request) {
        log.info("Received transaction request: {}", request);

        try {
            blockchainProxy.execute(request);
            long commitedAt = System.currentTimeMillis();
            long endToEndLatency = commitedAt - request.getReceivedAt();
            log.info("[TOTAL_TIME]" + request.getId() + ";" + request.getReceivedAt() + ";" + commitedAt + ";" + endToEndLatency + ";COMMITED");
        } catch (Exception e) {
            log.error("Error processing blockchain transaction: {}", e.getMessage(), e);
            long commitedAt = System.currentTimeMillis();
            long endToEndLatency = commitedAt - request.getReceivedAt();
            log.info("[TOTAL_TIME]" + request.getId() + ";" + request.getReceivedAt() + ";" + commitedAt + ";" + endToEndLatency + ";FAILED");
            // todo: evaluate retry or compensation logic here
        }
    }
}
