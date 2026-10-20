package edu.tucn.dppver.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author <a href="mailto:radu.miro@aut.utcluj.ro">Radu Miron</a>
 */

@Data
public class Asset {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String id;

    private String name;
    private String owner;
    private Status status;
}
