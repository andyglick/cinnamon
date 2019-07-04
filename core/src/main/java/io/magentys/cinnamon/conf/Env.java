package io.magentys.cinnamon.conf;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import java.util.stream.Stream;

/**
 * Env is a cinnamon wrapper for typesafe config.
 * User can optionally utilise this functionality by injecting Env in his code.
 * This class expects the system property `env` to be passed representing a profile
 * in the configuration file.
 * The configuration file should live in the classpath and named `env.conf`
 * <p>
 * Example, where `default` and `st` are the config profiles user my choose ie. `-Denv=default`:
 * <p>
 * default : {
 * local-pages-url : "http://localhost:8080"
 * }
 * <p>
 * dev : {
 * local-pages-url : "http://st:8080"
 * }
 */
@SuppressWarnings("WeakerAccess")
public class Env {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final Env INSTANCE = new Env();

    private String environment;

    public Config config;

    public static Env env() {
        return INSTANCE;
    }

    public Env() {
        this(ConfigConstants.ENV_PROPERTY);
    }

    public Env(String environment) {
        Optional param = Optional.ofNullable(environment);
        if (param.isPresent()) {
            this.environment = environment;
            try {
                config = initConfig();
            } catch (IOException e) {
                LOG.warn("in Env CTOR with String env as a parameter", e);
            }
        } else {
            throw new RuntimeException("Cannot initialise Env. Please provide env profile parameter (-Denv=myProfile)");
        }
    }

    private Config initConfig() throws IOException {
        Config systemConfig = ConfigFactory.systemProperties();
        File envConfig = searchConfigFileInClasspath(ConfigConstants.ENV_CONF_FILE);
        if (envConfig.getName().contains(".yml")) {
            return getConfigFromYml(envConfig);
        }
        if (envConfig.getName().contains(".conf")) {
            return systemConfig.withFallback(ConfigFactory.parseFile(envConfig)).resolve().getConfig(environment);
        }
        else {
            throw new Error("The file [" + envConfig.getAbsolutePath() + "] is not an environment config file.");
        }
    }

    private Config getConfigFromYml(File file) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Yaml yaml = new Yaml();
        Map<String, Map<String, Object>> data = ((Map<String, Map<String, Object>>) yaml
                .load(inputStream));
        Config systemConfig = ConfigFactory.systemProperties();
        return systemConfig.withFallback(ConfigFactory.parseMap(data)).resolve().getConfig(environment);
    }

    File searchConfigFileInClasspath(String filename) {
        final List<File> files;
        try (Stream<Path> paths = Files.walk(new File(ConfigConstants.PROJECT_DIR).toPath())) {
            files = paths.filter(p -> p.endsWith(filename)).filter(p -> !p.toString().contains(ConfigConstants.TARGET_DIR)).map(Path::toFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new Error(e);
        }

        if (files.size() == 0)
            throw new Error("Config file with name [" + filename + "] could not be found in your classpath.");
        if (files.size() > 1)
            throw new Error("More than one file found for this environment with name [" + filename + "]");
        if (!files.get(0).isFile())
            throw new Error("The file [" + files.get(0).getAbsolutePath() + "] is not a normal file.");
        return files.get(0);
    }
}
