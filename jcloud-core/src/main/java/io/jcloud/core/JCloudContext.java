package io.jcloud.core;

import static io.jcloud.logging.Log.LOG_SUFFIX;

import java.lang.annotation.Annotation;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.junit.jupiter.api.extension.ExtensionContext;

import io.jcloud.configuration.BaseConfigurationBuilder;
import io.jcloud.configuration.ConfigurationLoader;
import io.jcloud.configuration.JCloudConfiguration;
import io.jcloud.configuration.JCloudConfigurationBuilder;

public final class JCloudContext {

    private static final String JCLOUD = "jcloud";
    private static final String LOG_FILE_PATH = System.getProperty("log.file.path", "target/logs");
    private static final int JCLOUD_ID_MAX_SIZE = 60;

    private final ExtensionContext testContext;
    private final String id;
    private final ExtensionContext.Namespace testNamespace;
    private final Map<String, Object> customConfigurationByTarget = new HashMap<>();
    private final Map<Class<?>, List<Annotation>> annsForConfiguration = new HashMap<>();

    private ExtensionContext methodTestContext;
    private boolean failed;
    private boolean debug;

    protected JCloudContext(ExtensionContext testContext) {
        this.testContext = testContext;
        this.id = generateContextId(testContext);
        this.testNamespace = ExtensionContext.Namespace.create(JCloudContext.class);

        loadCustomConfiguration(JCLOUD, new JCloudConfigurationBuilder());
    }

    public String getId() {
        return id;
    }

    public boolean isFailed() {
        return failed;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public JCloudConfiguration getConfiguration() {
        return getConfigurationAs(JCloudConfiguration.class);
    }

    public <T> T getConfigurationAs(Class<T> configurationClazz) {
        return customConfigurationByTarget.values().stream().filter(configurationClazz::isInstance)
                .map(configurationClazz::cast).findFirst()
                .orElseThrow(() -> new RuntimeException("No found configuration for " + configurationClazz));
    }

    public String getRunningTestClassAndMethodName() {
        String classMethodName = getRunningTestClassName();
        Optional<String> methodName = getRunningTestMethodName();
        if (methodName.isPresent()) {
            classMethodName += "." + methodName.get();
        }

        return classMethodName;
    }

    public String getRunningTestClassName() {
        return getTestContext().getRequiredTestClass().getSimpleName();
    }

    public Optional<String> getRunningTestMethodName() {
        if (methodTestContext == null) {
            return Optional.empty();
        }

        return Optional.of(methodTestContext.getRequiredTestMethod().getName());
    }

    public ExtensionContext.Store getTestStore() {
        return getTestContext().getStore(this.testNamespace);
    }

    public ExtensionContext getTestContext() {
        return Optional.ofNullable(methodTestContext).orElse(testContext);
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return getTestContext().getRequiredTestClass().isAnnotationPresent(annotationClass);
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return getTestContext().getRequiredTestClass().getAnnotation(annotationClass);
    }

    public void setMethodTestContext(ExtensionContext methodTestContext) {
        this.methodTestContext = methodTestContext;
    }

    public Path getLogFolder() {
        return Paths.get(LOG_FILE_PATH);
    }

    public Path getLogFile() {
        return getLogFolder().resolve(getRunningTestClassName() + LOG_SUFFIX);
    }

    public <T extends Annotation> Optional<T> getAnnotatedConfiguration(Class<T> clazz) {
        return getAnnotatedConfiguration(clazz, (s) -> true);
    }

    public <T extends Annotation> Optional<T> getAnnotatedConfiguration(Class<T> clazz, Predicate<T> apply) {
        List<Annotation> configurationsByClass = annsForConfiguration.get(clazz);
        if (configurationsByClass == null) {
            configurationsByClass = loadAnnotatedConfiguration(clazz);
            annsForConfiguration.put(clazz, configurationsByClass);
        }

        return configurationsByClass.stream().filter(clazz::isInstance).map(clazz::cast).filter(apply::test)
                .findFirst();
    }

    public <T extends Annotation, C> C loadCustomConfiguration(String target, BaseConfigurationBuilder<T, C> builder) {
        if (customConfigurationByTarget.containsKey(target)) {
            throw new RuntimeException("Target configuration has been already loaded: " + target);
        }

        C configuration = ConfigurationLoader.load(target, this, builder);
        customConfigurationByTarget.put(target, configuration);
        return configuration;
    }

    protected void markTestSuiteAsFailed() {
        failed = true;
    }

    private List<Annotation> loadAnnotatedConfiguration(Class<? extends Annotation> clazz) {
        return Arrays.asList(testContext.getRequiredTestClass().getAnnotationsByType(clazz));
    }

    private static String generateContextId(ExtensionContext context) {
        String fullId = context.getRequiredTestClass().getSimpleName() + "-" + System.currentTimeMillis();
        return fullId.substring(0, Math.min(JCLOUD_ID_MAX_SIZE, fullId.length()));
    }
}
