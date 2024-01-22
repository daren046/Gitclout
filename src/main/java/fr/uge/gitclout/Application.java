package fr.uge.gitclout;

import io.micronaut.context.annotation.Factory;
import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;


@OpenAPIDefinition(
    info = @Info(
        title = "GitClout",
        version = "0.0",
        description = "GitClout API"
    )
)

@Factory
/**
 * Main class of the application.
 * It starts the Micronaut server.
 */
public class Application {
    /**
     * constructor of the class.
     */
    public Application() {}
    
    /**
     * Main method of the application.
     * @param args arguments of the main method.
     */
    public static void main(String[] args) {Micronaut.run(Application.class, args);}
}
