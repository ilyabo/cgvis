package at.fhjoanneum.cgvis.test.data;

import junit.framework.TestCase;
import at.fhjoanneum.cgvis.data.provider.DataProviderParameters;
import at.fhjoanneum.cgvis.data.provider.DbDataProvider;

public class DbDataProviderTest extends TestCase {

    /*
     * Test method for
     * 'at.fhjoanneum.cgvis.data.provider.OracleDataProvider.OracleDataProvider(IPointSetter,
     * String)'
     */
    public void testOracleDataProvider() {
        new DbDataProvider(new DataProviderParameters());
        System.out.println("Finished.");
    }

}
