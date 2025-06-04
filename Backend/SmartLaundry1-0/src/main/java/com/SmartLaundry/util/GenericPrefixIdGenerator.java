package com.SmartLaundry.util;

import org.hibernate.id.IdentifierGenerator;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;
import org.hibernate.id.Configurable;

public class GenericPrefixIdGenerator implements IdentifierGenerator, Configurable {

    private String prefix;
    private String tableName;
    private String columnName;
    private int defaultNumberLength;

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) {
        this.prefix = params.getProperty("prefix");
        this.tableName = params.getProperty("table_name");
        this.columnName = params.getProperty("column_name");
        this.defaultNumberLength = Integer.parseInt(params.getProperty("number_length", "5"));
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        try {
            Connection connection = session.doReturningWork(conn -> conn);
            Statement stmt = connection.createStatement();

            String query = String.format("SELECT %s FROM %s ORDER BY %s DESC LIMIT 1", columnName, tableName, columnName);
            ResultSet rs = stmt.executeQuery(query);

            int nextNumber = 1;
            int numberLength = defaultNumberLength;

            if (rs.next()) {
                String lastId = rs.getString(1);
                String numericPart = lastId.substring(prefix.length());
                nextNumber = Integer.parseInt(numericPart) + 1;
                numberLength = Math.max(numericPart.length(), numberLength);
            }

            return prefix + String.format("%0" + numberLength + "d", nextNumber);

        } catch (Exception e) {
            throw new HibernateException("Unable to generate ID for " + tableName, e);
        }
    }
}