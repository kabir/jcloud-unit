package io.jcloud.resources.quarkus;

import java.lang.annotation.Annotation;
import java.util.ServiceLoader;

import io.jcloud.api.Quarkus;
import io.jcloud.api.Service;
import io.jcloud.api.extensions.AnnotationBinding;
import io.jcloud.api.extensions.QuarkusManagedResourceBinding;
import io.jcloud.core.JCloudContext;
import io.jcloud.core.ManagedResource;
import io.jcloud.resources.quarkus.local.ProdModeBootstrapQuarkusManagedResourceJava;

public class QuarkusAnnotationBinding implements AnnotationBinding {

    private final ServiceLoader<QuarkusManagedResourceBinding> customBindings = ServiceLoader
            .load(QuarkusManagedResourceBinding.class);

    @Override
    public boolean isFor(Annotation... annotations) {
        return findAnnotation(annotations, Quarkus.class).isPresent();
    }

    @Override
    public ManagedResource getManagedResource(JCloudContext context, Service service, Annotation... annotations) {
        Quarkus metadata = findAnnotation(annotations, Quarkus.class).get();

        for (QuarkusManagedResourceBinding binding : customBindings) {
            if (binding.appliesFor(context)) {
                return binding.init(metadata);
            }
        }

        // If none handler found, then the container will be running on localhost by default
        return new ProdModeBootstrapQuarkusManagedResourceJava(metadata.location(), metadata.classes(),
                metadata.dependencies());
    }

}
