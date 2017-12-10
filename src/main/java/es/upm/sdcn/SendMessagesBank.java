package es.upm.sdcn;

import org.jgroups.*;

public class SendMessagesBank implements SendMessages {

    private JChannel channel;

    public SendMessagesBank(JChannel channel)  {
        this.channel = channel;
    }

    private void sendMessage(Address address, OperationBank operation) {

        try {
            byte[] bufferByte = org.jgroups.util.Util.objectToByteBuffer(operation);
            Message msg = new Message(address, bufferByte);
            channel.send(msg);
        } catch (Exception e) {
            System.err.println(e);
            System.out.println("Error when sending message");
            e.printStackTrace();
        }
    }

    public void sendAdd(Address address, Client client) {
        OperationBank operation = new OperationBank(OperationEnum.CREATE_CLIENT, client);
        sendMessage(address, operation);
    }

    public void sendRead(Address address, Integer accountNumber) {
        OperationBank operation = new OperationBank(OperationEnum.READ_CLIENT, accountNumber);
        sendMessage(address, operation);
    }

    public void sendUpdate(Address address, Client client) {
        OperationBank operation = new OperationBank(OperationEnum.UPDATE_CLIENT, client);
        sendMessage(address, operation);
    }

    public void sendDelete(Address address, Integer accountNumber) {
        OperationBank operation = new OperationBank(OperationEnum.DELETE_CLIENT, accountNumber);
        sendMessage(address, operation);
    }

    public void sendCreateBank (Address address, PostgreSQLClient clientDB) {
        OperationBank operation = new OperationBank(OperationEnum.CREATE_BANK, clientDB);
        sendMessage(address, operation);
    }
}
