package th.in.ffc.airsync.api;

import org.glassfish.jersey.server.ResourceConfig;


public class ApplicationConfig extends ResourceConfig {

    public ApplicationConfig() {
        packages("th.in.ffc.airsync.api");

        //register(ApiWebSocks.class);
        //register(JacksonFeature.class);
        //register(RolesAllowedDynamicFeature.class);
        //register(CsrfProtectionFilter.class);
    }
}

