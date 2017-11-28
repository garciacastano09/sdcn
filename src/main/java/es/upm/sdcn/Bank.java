package es.upm.sdcn;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.Address;

public class Bank extends ReceiverAdapter {

    private JChannel channel;
    private ClientDB clientDB;
    private SendMessagesBank sendMessages;
    private boolean isLeader = false;
    private View previousView = null;



    public Bank(String cluster) {
        clientDB     = new ClientDB();
        try {
            channel = new JChannel().setReceiver(this);
            channel.connect(cluster);
        } catch (Exception e) {
            System.out.println("Error to create the JGroups channel");
        }
        sendMessages = new SendMessagesBank(channel);

        previousView = channel.getView();
        //if (previousView.size() == 1) isLeader = true;
    }

    public boolean isLeader() {
        return isLeader;
    }

    public void close() {
        channel.close();
    }

    // View
    @Override
    public void viewAccepted(View newView) {

        try {
            Address address = null;
            if (newView.getMembers().get(0).equals(channel.address())) isLeader = true;

            //System.out.println("** view: " + newView);
            if (isLeader && previousView != null && previousView.size() < newView.size()) {
                // Send the bankDB to the new one
                // TODO: block meanwhile
                // TODO: Check whether some view has lost
                // Get the last in the view
                address = newView.getMembers().get(newView.size() - 1);
                sendMessages.sendCreateBank(address, clientDB);
                System.out.println("SendCreateBank");
            }
        } catch (Exception e) {
            System.out.println("Unexpected exception in viewAccepted");
        }
        previousView = newView;
    }

    // Receive messages
    @Override
    public void receive(Message msg) {
        Object dhtbdObj = null;

        try {
            dhtbdObj = (Object) org.jgroups.util.Util.objectFromByteBuffer(msg.getBuffer());
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
            System.out.println(e.toString());
            System.out.println("Exception objectFromByteBuffer");
        }
        OperationBank operation = (OperationBank) dhtbdObj;
        handleReceiverMsg(operation);
    }

    private synchronized void handleReceiverMsg(OperationBank operation) {
        //System.out.println(operation.getOperation());
        switch (operation.getOperation()) {
            case CREATE_CLIENT:
                clientDB.createClient(operation.getClient());
                break;
            case READ_CLIENT:
                clientDB.readClient(operation.getAccountNumber());
                break;
            case UPDATE_CLIENT:
                clientDB.updateClient(operation.getClient().getAccountNumber(),
                        operation.getClient().getBalance());
                break;
            case DELETE_CLIENT:
                clientDB.deleteClient(operation.getAccountNumber());
                break;
            case CREATE_BANK:
                System.out.println("Received CREATE_BANK");
                //System.out.println(operation.getClientDB().toString());
                //System.out.println("------------------------------");
                clientDB.createBank(operation.getClientDB());
                break;
        }
    }

    public boolean createClient(Client client) {
        sendMessages.sendAdd(null, client);
        return clientDB.createClient(client);
    }

    public Client readClient(Integer accountNumber) {
        // Handling locally. No need for distributing
        //sendMessages.sendRead(null, accountNumber);
        return clientDB.readClient(accountNumber);
    }

    public boolean updateClient (int accNumber, int balance) {
        boolean isCorrect = clientDB.updateClient(accNumber, balance);
        sendMessages.sendUpdate(null, clientDB.readClient(accNumber));
        return isCorrect;
    }

    public boolean deleteClient(Integer accountNumber) {
        sendMessages.sendDelete(null, accountNumber);
        return clientDB.deleteClient(accountNumber);
    }

    public String toString() {
        String string = null;
        string = "          Bank Java     \n" +
                "------------------------\n";
        string = clientDB.toString();
        return string;
    }

}
