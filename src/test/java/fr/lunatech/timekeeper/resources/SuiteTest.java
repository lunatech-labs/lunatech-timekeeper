package fr.lunatech.timekeeper.resources;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        InfrastructureTest.class,
        UserResourceTest.class,
        ClientResourceTest.class,
        ProjectResourceTest.class
})
public class SuiteTest {
}
