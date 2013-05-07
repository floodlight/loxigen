import org.openflow.protocol.match.MatchBuilderVer10;
import org.openflow.types.MacAddress;
import org.openflow.types.OFPort;


public class Test {

    private static volatile OFPort port;
    private static volatile MacAddress ethSrc;

    /**
     * @param args
     */
    public static void main(String[] args) {
        MacAddress mac = MacAddress.of("01:02:03:04:05:06");

        long start = System.currentTimeMillis();
        for(int i=0; i < 10000000; i++) {
            MatchBuilderVer10 m = new MatchBuilderVer10();
            // m.set(MatchField.IN_PORT, OFPort.CONTROLLER);
            m.setInputPort(OFPort.CONTROLLER);
            port = m.getInputPort(); //m.get(MatchField.IN_PORT);
            m.setDataLayerSource(mac);
            ethSrc = m.getDataLayerSource(); //(MatchField.ETH_SRC);
        }
        long end = System.currentTimeMillis();
        System.out.println("end-start: "+ (end-start));
    }

}
