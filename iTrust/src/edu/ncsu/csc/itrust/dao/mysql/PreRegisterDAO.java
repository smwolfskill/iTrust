package edu.ncsu.csc.itrust.dao.mysql;

import edu.ncsu.csc.itrust.DBUtil;
import edu.ncsu.csc.itrust.beans.loaders.PatientLoader;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.exception.DBException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PreRegisterDAO
{
    private DAOFactory factory;
    private PatientLoader patientLoader;

    public PreRegisterDAO(DAOFactory factory)
    {
        this.factory = factory;
        this.patientLoader = new PatientLoader();

    }

    public void addPreregisterPatient(long pid, float height, float weight, int isSmoker) throws DBException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = factory.getConnection();
            ps = conn.prepareStatement("INSERT INTO PreRegisteredPatients(MID, Height, Weight, Smoker) VALUES(?,?,?,?)");
            ps.setLong(1,pid);
            ps.setFloat(2,height);
            ps.setFloat(3,weight);
            ps.setFloat(4,isSmoker);
            ps.executeUpdate();
        } catch (SQLException e) {

            throw new DBException(e);
        } finally {
            DBUtil.closeConnection(conn, ps);
        }
    }

    public boolean checkPreregisteredPatient(long pid) throws DBException
    {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = factory.getConnection();
            ps = conn.prepareStatement("SELECT * FROM PreRegisteredPatients WHERE MID=?");
            ps.setLong(1, pid);

            boolean check = (ps.executeQuery().next());
            ps.close();
            return check;
        } catch (SQLException e) {

            throw new DBException(e);
        } finally {
            DBUtil.closeConnection(conn, ps);
        }
    }
}
