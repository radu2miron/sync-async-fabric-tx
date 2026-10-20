package edu.tucn.dppver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author <a href="mailto:radu.miro@aut.utcluj.ro">Radu Miron</a>
 */

public class Parser {
    public static void main(String[] args) throws IOException {
        Files.lines(Path.of("logs/syncasync-api.log"))
                .filter(l->l.contains("[TOTAL_TIME]"))
                .map(l->l.substring(l.indexOf("[TOTAL_TIME]")+12))
                .forEach(System.out::println);
    }
}
