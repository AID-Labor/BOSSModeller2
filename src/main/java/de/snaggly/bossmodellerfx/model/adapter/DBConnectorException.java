package de.snaggly.bossmodellerfx.model.adapter;

/**
 * Custom Exception for DBInterface.
 *
 * @author Omar Emshani
 */
public class DBConnectorException extends Exception{
    public DBConnectorException(String head, String reason) {
        this.head = head;
        this.reason = reason;
    }

    public final String head;
    public final String reason;
}
