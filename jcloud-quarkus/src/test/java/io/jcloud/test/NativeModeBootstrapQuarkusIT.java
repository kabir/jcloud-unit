package io.jcloud.test;

import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.jcloud.api.Dependency;
import io.jcloud.api.JCloud;
import io.jcloud.api.Quarkus;
import io.jcloud.api.RestService;
import io.jcloud.test.samples.QuarkusPingApplication;

@Tag("native")
@JCloud
public class NativeModeBootstrapQuarkusIT {
    @Quarkus(dependencies = @Dependency(artifactId = "quarkus-resteasy"), classes = QuarkusPingApplication.class)
    static final RestService app = new RestService().withProperties("quarkus-ping-application.properties")
            .withProperty("quarkus.package.type", "native");

    @Test
    public void shouldExecuteAppInNativeMode() {
        app.given().get("/ping").then().body(is("pong"));
    }
}
