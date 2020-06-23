package fr.lunatech.cache;

import javax.ws.rs.core.EntityTag;
import java.net.URI;

public interface ETagSupport {

    // Do not call it get... to avoid JSON extra attribute
    EntityTag computeETag(URI uri);

}
