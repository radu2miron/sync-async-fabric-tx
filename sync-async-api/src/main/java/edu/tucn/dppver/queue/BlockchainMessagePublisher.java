package edu.tucn.dppver.queue;
import edu.tucn.dppver.model.TransactionRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:radu.miro@aut.utcluj.ro">Radu Miron</a>
 */
@Service
@Log4j2
public class BlockchainMessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    public BlockchainMessagePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendTransactionRequest(TransactionRequest request) {
        log.info("Sending transaction request: {}", request);
        rabbitTemplate.convertAndSend(RabbitConfig.BLOCKCHAIN_QUEUE, request);
    }
}

