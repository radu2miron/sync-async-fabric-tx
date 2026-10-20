package edu.tucn.syncasync.model;

import lombok.Data;

/**
 * @author <a href="mailto:radu.miro@aut.utcluj.ro">Radu Miron</a>
 */

@Data
public class Asset {
    private String id;
    private String name;
    private String owner;
    private Status status;
}
