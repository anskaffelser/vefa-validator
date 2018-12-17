package no.difi.vefa.validator.dist;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Cli {

    public static void main(String... args) throws Exception {
        if (args.length == 0) {
            log.error("No command specified.");
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
                no.difi.vefa.validator.repo.Cli.main(realArgs);
                break;
            case "tester":
                no.difi.vefa.validator.tester.Cli.main(realArgs);
                break;
            default:
                log.error(String.format("Unknown command: '%s'", args[0]));
                System.exit(1);
        }
    }
}
