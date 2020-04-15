package fr.lunatech.timekeeper.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.media.Schema;

/**
 * This Filter removes the panache id from the generated Swagger API.
 * @author nmartignole
 */
public class TimeKeeperOASFilter implements OASFilter {
    private static Logger logger =  LoggerFactory.getLogger(TimeKeeperOASFilter.class);

    @Override
    public Schema filterSchema(Schema schema) {
        if(schema != null){
            if(schema.getProperties() != null) {
                if(schema.getProperties().get("id") != null){
                    logger.info("Filtered the 'id' property from a Panache entity to generate OpenAPI doc");
                    schema.removeProperty("id");
                }
            }
        }
        return schema;
    }
}
