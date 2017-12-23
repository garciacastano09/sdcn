package es.upm.sdcn;

import java.io.Serializable;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostgreSQLClient implements Serializable {

    private static final long serialVersionUID = 1L;
    Logger LOG = Logger.getLogger("sdcn");

    public PostgreSQLClient() {
        LOG.log(Level.INFO,"-------- PostgreSQL JDBC Connection Testing ------------");

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            LOG.log(Level.SEVERE,"Where is your PostgreSQL JDBC Driver? Include it in your library path!");
            e.printStackTrace();
            return;
        }

        LOG.info("PostgreSQL JDBC Driver Registered!");

        executeQuery("create table if not exists clients(" +
                "accountnumber integer primary key, " +
                "balance integer not null, " +
                "name varchar(30) not null" +
                ");");
    }

    public boolean createClient(Client client) {
        LOG.log(Level.INFO,"PostgreSQLClient.createClient");

        StringBuilder stb = new StringBuilder();
        stb.append("INSERT INTO clients VALUES (" );
        stb.append(client.getAccountNumber());
        stb.append(",");
        stb.append(client.getBalance());
        stb.append(",");
        stb.append("\'");
        stb.append(client.getName());
        stb.append("\'");
        stb.append(");");
        return executeSql(stb.toString());
    }

    public Client readClient(int accountNumber){
        LOG.log(Level.INFO,"PostgreSQLClient.createClient");

        StringBuilder stb = new StringBuilder();
        stb.append("select * from clients where accountnumber=");
        stb.append(accountNumber);
        stb.append(";");
        return executeQuery(stb.toString());
    }
    public boolean updateClient(int a, int b){
        return true;
    }
    public boolean deleteClient(int a){
        return true;
    }
    public boolean createBank(PostgreSQLClient a){
        return true;
    }

    private Connection getConnector(){
        Connection c = null;
        try{
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/sdcn", "sdcn","1234");
        }
        catch (SQLException e){
            LOG.log(Level.SEVERE,"Connection Failed! Check output console");
            LOG.log(Level.SEVERE,e.getMessage());
        }
        return c;
    }

    private boolean executeSql(String sqlString){
        LOG.log(Level.INFO,"PostgreSQLClient.executeQuery "+sqlString);

        Connection c = getConnector();

        try( Statement stmt = c.createStatement()){
            stmt.execute(sqlString);
            LOG.log(Level.INFO, sqlString);

            stmt.close();
            c.close();
            return true;
        }
        catch(SQLException e){
            LOG.log(Level.SEVERE, e.getMessage());
            return false;
        }

    }

    private Client executeQuery(String sqlString){
        LOG.log(Level.INFO,"PostgreSQLClient.executeQuery "+sqlString);

        Connection c = getConnector();

        try(Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(sqlString)){

            LOG.log(Level.INFO, sqlString);

            rs.next();
            Client result = new Client(rs.getInt(1), rs.getString(3), rs.getInt(2));
            c.close();
            return result;
        }
        catch(SQLException e){
            LOG.log(Level.SEVERE, e.getMessage());
            return null;
        }

    }

}



