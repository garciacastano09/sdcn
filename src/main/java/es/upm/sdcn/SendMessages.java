package es.upm.sdcn;

import org.jgroups.*;

public interface SendMessages {

    public void sendAdd(Address address, Client client);

    public void sendRead(Address address, Integer accountNumber);

    public void sendUpdate(Address address, Client client);

    public void sendDelete(Address address, Integer accountNumber);

    public void sendCreateBank(Address address, PostgreSQLClient clientDB);

}
