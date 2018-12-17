package no.difi.vefa.validator.build;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.validator.build.model.Build;
import no.difi.vefa.validator.build.module.BuildModule;
import no.difi.vefa.validator.build.module.SchematronModule;
import no.difi.vefa.validator.build.task.BuildTask;
import no.difi.vefa.validator.build.task.TestTask;
import no.difi.vefa.validator.module.SaxonModule;
import org.apache.commons.cli.*;

import javax.xml.bind.JAXBException;
import java.io.IOException;

@Slf4j
public class Cli {

    @Inject
    private Provider<BuildTask> buildTask;

    @Inject
    private Provider<TestTask> testTask;

    public static void main(String... args) throws Exception {
        System.exit(getInjector()
                .getInstance(Cli.class)
                .perform(args));
    }

    protected static Injector getInjector() {
        return Guice.createInjector(new SaxonModule(), new BuildModule(), new SchematronModule());
    }

    public int perform(String... args) throws IOException, JAXBException, ParseException {
        Options options = new Options();
        options.addOption("c", "config", true, "Config file");
        options.addOption("t", "test", false, "Run tests");
        options.addOption("b", "build", true, "Build identifier");
        options.addOption("n", "name", true, "Name");
        options.addOption("w", "weight", true, "Weight");
        options.addOption("x", "exitcode", false, "Status in exit code - DEPRECATED");
        options.addOption("p", "profile", true, "Buildconfig profile");
        options.addOption("a", "source", true, "Source folder");
        options.addOption("s", "site", true, "Create site - DEPRECATED");
        options.addOption(Option.builder("target").desc("Target folder").hasArg(true).build());

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        int result = 0;

        for (String arg : cmd.getArgs().length > 0 ? cmd.getArgs() : new String[]{"."}) {
            Build build = Build.of(arg, cmd);

            buildTask.get().build(build);

            if (cmd.hasOption("test"))
                result += testTask.get().perform(build) ? 0 : 1;
        }

        return result;
    }
}
