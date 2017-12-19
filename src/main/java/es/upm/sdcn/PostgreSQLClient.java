package es.upm.sdcn;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;

public class PostgreSQLClient implements Serializable {

    private static final long serialVersionUID = 1L;

    private java.util.HashMap <Integer, Client> clientDB;
    private Connection connector;

    public PostgreSQLClient(PostgreSQLClient clientDB) {
        this.clientDB = clientDB.getClientDB();
    }

    public PostgreSQLClient() {

        clientDB = new java.util.HashMap <Integer, Client>();
    }

    public java.util.HashMap <Integer, Client> getClientDB() {
        return this.clientDB;
    }

    public boolean createClient(Client client) {
        if (clientDB.containsKey(client.getAccountNumber())) {
            return false;
        } else {
            clientDB.put(client.getAccountNumber(), client);
            return true;
        }
    }

    public Client readClient(Integer accountNumber) {
        if (clientDB.containsKey(accountNumber)) {
            return clientDB.get(accountNumber);
        } else {
            return null;
        }
    }

    public boolean updateClient (int accNumber, int balance) {
        if (clientDB.containsKey(accNumber)) {
            Client client = clientDB.get(accNumber);
            client.setBalance(balance);
            clientDB.put(client.getAccountNumber(), client);
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteClient(Integer accountNumber) {
        if (clientDB.containsKey(accountNumber)) {
            clientDB.remove(accountNumber);
            return true;
        } else {
            return false;
        }
    }

    public boolean createBank(PostgreSQLClient clientDB) {
        System.out.println("createBank");
        this.clientDB = clientDB.getClientDB();
        System.out.println(clientDB.toString());
        return true;
    }

    public String toString() {
        String aux = new String();

        for (java.util.HashMap.Entry <Integer, Client>  entry : clientDB.entrySet()) {
            aux = aux + entry.getValue().toString() + "\n";
        }
        return aux;
    }
}



