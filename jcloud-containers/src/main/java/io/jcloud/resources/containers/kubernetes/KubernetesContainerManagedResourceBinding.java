package io.jcloud.resources.containers.kubernetes;

import io.jcloud.api.extensions.ContainerManagedResourceBinding;
import io.jcloud.core.JCloudContext;
import io.jcloud.core.ManagedResource;
import io.jcloud.core.extensions.KubernetesExtensionBootstrap;

public class KubernetesContainerManagedResourceBinding implements ContainerManagedResourceBinding {
    @Override
    public boolean appliesFor(JCloudContext context) {
        return KubernetesExtensionBootstrap.isEnabled(context);
    }

    @Override
    public ManagedResource init(String image, String expectedLog, String[] command, int[] ports) {
        return new KubernetesContainerManagedResource(image, expectedLog, command, ports);
    }
}
