package io.jcloud.configuration;

public final class KubernetesServiceConfiguration {
    private String template;
    private boolean useInternalService = false;

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public boolean isUseInternalService() {
        return useInternalService;
    }

    public void setUseInternalService(boolean useInternalService) {
        this.useInternalService = useInternalService;
    }
}
