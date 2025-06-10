package com.SmartLaundry.util;

import org.hibernate.id.IdentifierGenerator;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;
import org.hibernate.id.Configurable;

public class GenericPrefixIdGenerator implements IdentifierGenerator, Configurable {

    private String prefix;
    private String tableKey;
    private int defaultNumberLength;

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) {
        this.prefix = params.getProperty("prefix");
        this.tableKey = params.getProperty("table_name");
        this.defaultNumberLength = Integer.parseInt(params.getProperty("number_length", "5"));
    }

    @Override
    public synchronized Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        Connection connection = null;
        PreparedStatement updateStmt = null;
        PreparedStatement selectStmt = null;
        PreparedStatement insertStmt = null;
        ResultSet rs = null;

        try {
            connection = session.doReturningWork(conn -> conn);

            // Try to increment next_val for the given table_key
            updateStmt = connection.prepareStatement("UPDATE id_sequence SET next_val = next_val + 1 WHERE table_key = ?");
            updateStmt.setString(1, tableKey);
            int rowsUpdated = updateStmt.executeUpdate();

            if (rowsUpdated == 0) {
                // If no row was updated, insert a new one with next_val = 1
                insertStmt = connection.prepareStatement("INSERT INTO id_sequence (table_key, next_val) VALUES (?, 1)");
                insertStmt.setString(1, tableKey);
                insertStmt.executeUpdate();

                // Return ID with next_val = 1
                return prefix + String.format("%0" + defaultNumberLength + "d", 1);
            }

            // Now retrieve the incremented next_val
            selectStmt = connection.prepareStatement("SELECT next_val FROM id_sequence WHERE table_key = ?");
            selectStmt.setString(1, tableKey);
            rs = selectStmt.executeQuery();

            if (rs.next()) {
                int nextVal = rs.getInt(1);
                return prefix + String.format("%0" + defaultNumberLength + "d", nextVal);
            } else {
                throw new HibernateException("No entry found in id_sequence for key: " + tableKey);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new HibernateException("Failed to generate ID for " + tableKey, e);
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
            try { if (updateStmt != null) updateStmt.close(); } catch (Exception ignored) {}
            try { if (selectStmt != null) selectStmt.close(); } catch (Exception ignored) {}
            try { if (insertStmt != null) insertStmt.close(); } catch (Exception ignored) {}
        }
    }

}

//public class GenericPrefixIdGenerator implements IdentifierGenerator, Configurable {
//
//    private String prefix;
//    private String tableName;
//    private String columnName;
//    private int defaultNumberLength;
//
//    @Override
//    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) {
//        this.prefix = params.getProperty("prefix");
//        this.tableName = params.getProperty("table_name");
//        this.columnName = params.getProperty("column_name");
//        this.defaultNumberLength = Integer.parseInt(params.getProperty("number_length", "5"));
//    }
//
//    @Override
//    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
//        try {
//            Connection connection = session.doReturningWork(conn -> conn);
//            Statement stmt = connection.createStatement();
//
//            String query = String.format("SELECT %s FROM %s ORDER BY %s DESC LIMIT 1", columnName, tableName, columnName);
//            ResultSet rs = stmt.executeQuery(query);
//
//            int nextNumber = 1;
//            int numberLength = defaultNumberLength;
//
//            if (rs.next()) {
//                String lastId = rs.getString(1);
//                String numericPart = lastId.substring(prefix.length());
//                nextNumber = Integer.parseInt(numericPart) + 1;
//                numberLength = Math.max(numericPart.length(), numberLength);
//            }
//
////            return prefix + String.format("%0" + numberLength + "d", nextNumber);
//            String generatedId = prefix + String.format("%0" + numberLength + "d", nextNumber);
//            System.out.println("Generated ID: " + generatedId);
//            return generatedId;
//
//        } catch (Exception e) {
//            throw new HibernateException("Unable to generate ID for " + tableName, e);
//        }
//    }
//}