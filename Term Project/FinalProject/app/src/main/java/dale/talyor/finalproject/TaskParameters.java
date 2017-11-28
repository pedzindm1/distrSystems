package dale.talyor.finalproject;

import java.net.InetSocketAddress;

/**
 * Created by dalepedzinski on 11/28/17.
 */

public class TaskParameters {

    public TaskParameters(Applications applications, InetSocketAddress server) {
        this.applications = applications;
        this.ServerInfo = server;
    }

    public Applications getApplications() {
        return applications;
    }

    public void setApplications(Applications applications) {
        this.applications = applications;
    }

    private Applications applications;

    public InetSocketAddress getServerInfo() {
        return ServerInfo;
    }

    public void setServerInfo(InetSocketAddress serverURL) {
        this.ServerInfo = serverURL;
    }

    private InetSocketAddress ServerInfo;
}
