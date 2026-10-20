package edu.tucn.dppver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author <a href="mailto:radu.miro@aut.utcluj.ro">Radu Miron</a>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
    private TxType txType;
    private String function;
    private String payload;
    private long receivedAt;
    private String id;
}
