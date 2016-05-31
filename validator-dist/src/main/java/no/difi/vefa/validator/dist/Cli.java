package no.difi.vefa.validator.dist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cli {

    private static Logger logger = LoggerFactory.getLogger(Cli.class);

    public static void main(String... args) throws Exception {
        if (args.length == 0) {
            logger.error("No command specified.");
            System.exit(1);
        }

        String[] realArgs = new String[args.length - 1];
        for (int i = 1; i < args.length; i++)
            realArgs[i - 1] = args[i];

        switch (args[0]) {
            case "build":
                no.difi.vefa.validator.build.Cli.main(realArgs);
                break;
            case "repo":
                no.difi.vefa.validator.Cli.main(realArgs);
                break;
            case "tester":
                no.difi.vefa.validator.tester.Cli.main(realArgs);
                break;
            default:
                logger.error(String.format("Unknown command: '%s'", args[0]));
                System.exit(1);
        }
    }
}
