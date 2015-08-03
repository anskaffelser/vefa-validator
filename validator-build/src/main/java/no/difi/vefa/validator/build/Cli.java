package no.difi.vefa.validator.build;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import java.nio.file.Paths;
import java.util.UUID;

public class Cli {

    public static void main(String... args) throws Exception {
        Options options = new Options();
        options.addOption("t", "test", false, "Run tests");
        options.addOption("s", "site", false, "Create site");
        options.addOption("b", "build", true, "Build identifier");
        options.addOption("n", "name", true, "Name");
        options.addOption("w", "weight", true, "Weight");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        for (String arg : cmd.getArgs()) {
            Builder builder = new Builder(Paths.get(arg));

            builder.build(
                    cmd.getOptionValue("name", "rules"),
                    cmd.getOptionValue("build", UUID.randomUUID().toString()),
                    Long.parseLong(cmd.getOptionValue("weight", "0")),
                    null
            );

            if (cmd.hasOption("test"))
                builder.test();

            if (cmd.hasOption("site"))
                builder.site();
        }
    }
}
