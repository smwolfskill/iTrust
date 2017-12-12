package edu.ncsu.csc.itrust.dao.mysql;

import edu.ncsu.csc.itrust.DBUtil;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.exception.DBException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MessageFilterDAO {
    private transient final DAOFactory factory;

    public MessageFilterDAO(final DAOFactory factory) {
        this.factory = factory;
    }

    public void editMessageFilter(long MID, String nf) throws DBException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = factory.getConnection();
            pstmt = conn.prepareStatement("INSERT INTO messagefilter (MID, Filter) VALUES (?, ?) ON DUPLICATE KEY UPDATE Filter=VALUES(Filter)");
            pstmt.setLong(1, MID);
            pstmt.setString(2, nf);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getErrorCode());
            throw new DBException(e);
        } finally {
            DBUtil.closeConnection(conn, pstmt);
        }
    }

    public String getMessageFilter(long MID) throws DBException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = factory.getConnection();
            pstmt = conn.prepareStatement("SELECT Filter FROM messagefilter WHERE MID = ?");
            pstmt.setLong(1, MID);
            final ResultSet results = pstmt.executeQuery();
            if (!results.next()) {
                return "";
            }
            String resultString = results.getString("Filter");
            results.close();
            pstmt.close();
            return resultString;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new DBException(e);
        } finally {
            DBUtil.closeConnection(conn, pstmt);
        }
    }

}
