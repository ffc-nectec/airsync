package th.in.ffc.airsync.api;

import org.glassfish.jersey.server.ResourceConfig;


public class ApplicationConfig extends ResourceConfig {

    public ApplicationConfig() {
        packages("th.in.ffc.airsync.api");

        //store(ApiWebSocks.class);
        //store(JacksonFeature.class);
        //store(RolesAllowedDynamicFeature.class);
        //store(CsrfProtectionFilter.class);
    }
}

