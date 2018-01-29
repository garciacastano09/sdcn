package es.upm.sdcn;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import static es.upm.sdcn.ClientService.CLIENT_ZK_PATH_PREFIX;
import static es.upm.sdcn.ZkConnect.getZkConnect;

public class App implements ServletContextListener {
    static Logger LOG = Logger.getLogger("sdcn");

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        LOG.log(Level.INFO, "Run main");
        ZkConnect zk = null;
        try {
            zk = getZkConnect();
            zk.createNode(CLIENT_ZK_PATH_PREFIX, new byte[0]);
            LOG.log(Level.INFO, "Server connected to ZK.");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Could not connect to ZK.");
            e.printStackTrace();
            return;
        }
        try {
            zk.getChildren("/clients",new SDCNWatcher(zk));
            LOG.log(Level.INFO, "Listening to /clients children");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("Shutting down!");
    }
}
