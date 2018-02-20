package th.in.ffc.airsync.api;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.CsrfProtectionFilter;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

public class ApplicationConfig extends ResourceConfig {

    public ApplicationConfig() {
        packages("th.in.ffc.airsync.api");

        //register(JacksonFeature.class);
        //register(RolesAllowedDynamicFeature.class);
        //register(CsrfProtectionFilter.class);
    }
}

