package no.difi.vefa.validator.build;

import freemarker.template.Configuration;
import freemarker.template.Template;
import no.difi.vefa.validator.Validation;
import no.difi.xsd.vefa.validator._1.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

class Site {

    private static Logger logger = LoggerFactory.getLogger(Site.class);

    private File rootFolder;
    private File siteFolder;

    private Configurations configurations;
    private List<Validation> validations;

    private Map<String, String> filenames = new HashMap<>();
    private List<String> types = new ArrayList<>();

    private Configuration configuration;

    public Site(File rootFolder, File siteFolder) throws Exception {
        this.rootFolder = rootFolder;
        this.siteFolder = siteFolder;

        configuration = new Configuration(Configuration.VERSION_2_3_22);
        configuration.setDefaultEncoding("UTF-8");
        configuration.setClassForTemplateLoading(getClass(), "/site");
    }

    public void setConfigurations(Configurations configurations) {
        this.configurations = configurations;

        Collections.sort(configurations.getConfiguration(), new Comparator<ConfigurationType>() {
            @Override
            public int compare(ConfigurationType o1, ConfigurationType o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });

        for (ConfigurationType configurationType : configurations.getConfiguration())
            if (!types.contains(getType(configurationType.getIdentifier())))
                types.add(getType(configurationType.getIdentifier()));
        Collections.sort(types);
    }

    public void setValidations(List<Validation> validations) {
        this.validations = validations;

        for (Validation validation : validations) {
            validation.getReport().setFilename(validation.getReport().getFilename().substring(rootFolder.toString().length() + 1));
            filenames.put(validation.getReport().getFilename(), DigestUtils.md5Hex(String.valueOf(validation.getReport().getFilename())));
        }
    }

    public void build() throws Exception {
        if (siteFolder.isDirectory())
            FileUtils.deleteDirectory(siteFolder);
        if (!siteFolder.mkdir())
            throw new Exception("Unable to make site directory.");

        createFrontpage();
        createConfigurations();
        createValidations();
        createTypes();
        createStatus();
    }

    protected void createFrontpage() throws Exception {
        Template template = configuration.getTemplate("index.ftl");
        Holder holder = new Holder();
        write(template, holder, "index.html");
    }

    protected void createConfigurations() throws Exception {
        Template template = configuration.getTemplate("configurations.ftl");
        Holder holder = new Holder();
        write(template, holder, "configuration.html");

        template = configuration.getTemplate("configuration.ftl");
        for (ConfigurationType configurationType : configurations.getConfiguration()) {
            holder = new Holder();
            holder.put("configuration", configurationType);
            holder.put("type", getType(configurationType.getIdentifier()));

            List<Validation> validations = new ArrayList<>();
            for (Validation validation : this.validations)
                if (configurationType.getIdentifier().equals(validation.getReport().getConfiguration()))
                    validations.add(validation);
            holder.put("vals", validations);

            write(template, holder, "configuration-" + configurationType.getIdentifier() + ".html");
        }
    }

    protected void createValidations() throws Exception {
        Template template = configuration.getTemplate("validations.ftl");
        Holder holder = new Holder();
        write(template, holder, "test.html");

        template = configuration.getTemplate("validation.ftl");
        for (Validation validation : validations) {
            holder = new Holder();
            holder.put("validation", validation);
            holder.put("type", getType(validation.getReport().getConfiguration()));
            write(template, holder, "test-" + filenames.get(validation.getReport().getFilename()) + ".html");
        }
    }

    protected void createTypes() throws Exception {
        Template template = configuration.getTemplate("type.ftl");
        for (String type : types) {
            Holder holder = new Holder();
            holder.put("type", type);

            List<ConfigurationType> confs = new ArrayList<>();
            for (ConfigurationType configurationType : configurations.getConfiguration())
                if (type.equals(configurationType.getIdentifier().split("\\-")[1].toUpperCase()))
                    confs.add(configurationType);
            holder.put("confs", confs);

            List<Validation> vals = new ArrayList<>();
            for (Validation validation : validations)
                if (type.equals(getType(validation.getReport().getConfiguration())))
                    vals.add(validation);
            Collections.sort(vals, new Comparator<Validation>() {
                @Override
                public int compare(Validation o1, Validation o2) {
                    return o2.getReport().getFlag().compareTo(o1.getReport().getFlag());
                }
            });
            holder.put("vals", vals);

            Map<String, Rule> rulesMap = new HashMap<>();
            for (Validation validation : vals) {
                for (SectionType section : validation.getReport().getSection()) {
                    for (AssertionType assertion : section.getAssertion()) {
                        if (!rulesMap.containsKey(assertion.getIdentifier()))
                            rulesMap.put(assertion.getIdentifier(), new Rule(assertion.getIdentifier()));
                        rulesMap.get(assertion.getIdentifier()).add(assertion.getFlag());
                    }
                }
            }
            List<Rule> rules = new ArrayList<>(rulesMap.values());
            Collections.sort(rules);
            holder.put("rules", rules);

            write(template, holder, "type-" + type + ".html");
        }
    }

    private void createStatus() throws Exception {
        Template template = configuration.getTemplate("status.ftl");
        for (FlagType status : FlagType.values()) {
            Holder holder = new Holder();
            holder.put("status", status);

            List<Validation> vals = new ArrayList<>();
            for (Validation validation : validations)
                if (status.equals(validation.getReport().getFlag()))
                    vals.add(validation);
            holder.put("vals", vals);

            write(template, holder, "status-" + status.toString() + ".html");
        }
    }

    private void write(Template template, Holder holder, String filename) {
        try {
            holder.put("configurations", configurations);
            holder.put("validations", validations);
            holder.put("filenames", filenames);
            holder.put("types", types);

            logger.debug("Process: " + filename);
            FileWriter writer = new FileWriter(new File(siteFolder, filename));
            template.process(holder, writer);
            writer.close();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

    private String getType(String identifier) {
        return identifier == null ? null : identifier.split("\\-")[1].toUpperCase();
    }

    private class Holder extends HashMap<String, Object> {
    }

    public class Rule implements Comparable<Rule> {
        private String name;
        private int success = 0, expected = 0, unexpected = 0;

        public Rule(String name) {
            this.name = name;
        }

        public void add(FlagType flagType) {
            if (flagType.equals(FlagType.EXPECTED))
                expected++;
            else if (flagType.equals(FlagType.OK))
                success++;
            else
                unexpected++;
        }

        @SuppressWarnings("unused")
        public String getName() {
            return name;
        }

        @SuppressWarnings("unused")
        public int getSuccess() {
            return success;
        }

        @SuppressWarnings("unused")
        public int getExpected() {
            return expected;
        }

        @SuppressWarnings("unused")
        public int getUnexpected() {
            return unexpected;
        }

        @SuppressWarnings("all")
        @Override
        public int compareTo(Rule o) {
            return name.compareTo(o.name);
        }

        @Override
        public String toString() {
            return "Rule{" +
                    "name='" + name + '\'' +
                    ", success=" + success +
                    ", expected=" + expected +
                    ", unexpected=" + unexpected +
                    '}';
        }
    }

}
