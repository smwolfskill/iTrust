package edu.ncsu.csc.itrust.dao.mysql;

import edu.ncsu.csc.itrust.DBUtil;
import edu.ncsu.csc.itrust.beans.PatientBean;
import edu.ncsu.csc.itrust.beans.PreRegisterBean;
import edu.ncsu.csc.itrust.beans.loaders.PatientLoader;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.exception.DBException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.sql.Date;


public class PreRegisterDAO
{
    private DAOFactory factory;
    private PatientLoader patientLoader;

    public PreRegisterDAO(DAOFactory factory)
    {
        this.factory = factory;
        this.patientLoader = new PatientLoader();

    }

    public void addPreregisterPatient(long pid, String height, String weight, String smoker) throws DBException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = factory.getConnection();
            ps = conn.prepareStatement("INSERT INTO PreRegisteredPatients(MID, Height, Weight, Smoker) VALUES(?,?,?,?)");
            ps.setLong(1,pid);
            if(height == null || height.equals(""))
                ps.setNull(2,Types.FLOAT);
            else
                ps.setFloat(2, Float.parseFloat(height));

            if(weight == null || weight.equals(""))
                ps.setNull(3,Types.FLOAT);
            else
                ps.setFloat(3, Float.parseFloat(weight));

            if(smoker == null || smoker.equals(""))
                ps.setNull(4,Types.INTEGER);
            else
                ps.setInt(4, Integer.parseInt(smoker));

            ps.executeUpdate();
            ps.close();
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

    public PreRegisterBean getPreregisteredPatient(long pid) throws DBException
    {
        Connection conn = null;
        PreparedStatement ps = null;
        PreRegisterBean preRegPatient;
        try {
            conn = factory.getConnection();
            ps = conn.prepareStatement("SELECT p.*, pp.height, pp.weight, pp.smoker FROM PreRegisteredPatients AS pp join Patients as P on" +
                    " pp.mid = p.mid WHERE DateofDeactivation IS NULL AND p.mid = ?");
            ps.setLong(1, pid);
            ResultSet rs = ps.executeQuery();
            preRegPatient = new PreRegisterBean();
            while(rs.next())
            {
                PatientBean patient = patientLoader.loadSingle(rs);
                preRegPatient.setPatient(patient);
                preRegPatient.setHeight(rs.getString("height"));
                preRegPatient.setWeight(rs.getString("weight"));
                preRegPatient.setSmoker(rs.getString("smoker"));
            }
            rs.close();
            ps.close();
            return preRegPatient;

        } catch (SQLException e) {

            throw new DBException(e);
        } finally {
            DBUtil.closeConnection(conn, ps);
        }
    }

    public List<PreRegisterBean> getPreregisteredPatients() throws DBException
    {
        Connection conn = null;
        PreparedStatement ps = null;
        ArrayList<PreRegisterBean> preRegPatients;
        try {
            conn = factory.getConnection();
            ps = conn.prepareStatement("SELECT p.*, pp.height, pp.weight, pp.smoker FROM PreRegisteredPatients AS pp join Patients as P on" +
                    " pp.mid = p.mid WHERE DateofDeactivation IS NULL");
            ResultSet rs = ps.executeQuery();
            preRegPatients = new ArrayList<>();
            while(rs.next())
            {
                PatientBean patient = patientLoader.loadSingle(rs);
                PreRegisterBean preReg = new PreRegisterBean();
                preReg.setPatient(patient);
                preReg.setHeight(rs.getString("height"));
                preReg.setWeight(rs.getString("weight"));
                preReg.setSmoker(rs.getString("smoker"));
                preRegPatients.add(preReg);
            }
            rs.close();
            ps.close();
            return preRegPatients;

        } catch (SQLException e) {

            throw new DBException(e);
        } finally {
            DBUtil.closeConnection(conn, ps);
        }
    }

    public boolean activatePreregisteredPatient(long pid) throws DBException
    {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = factory.getConnection();
            ps = conn.prepareStatement("DELETE FROM PreRegisteredPatients WHERE mid = ?");
            ps.setLong(1, pid);
            ps.executeUpdate();
            ps.close();
            return true;

        } catch (SQLException e) {

            throw new DBException(e);
        } finally {
            DBUtil.closeConnection(conn, ps);
        }
    }

    public boolean deactivatePreregisteredPatient(long pid) throws DBException
    {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = factory.getConnection();
            ps = conn.prepareStatement("UPDATE PATIENTS SET DateofDeactivation = ? WHERE mid = ?");
            Date date = new Date(Calendar.getInstance().getTime().getTime());
            ps.setDate(1, date);
            ps.setLong(2,pid);
            ps.executeUpdate();
            ps.close();
            return true;

        } catch (SQLException e) {

            throw new DBException(e);
        } finally {
            DBUtil.closeConnection(conn, ps);
        }
    }

    public boolean editPreregisteredPatient(PreRegisterBean preReg, long hcpid) throws DBException
    {
        Connection conn = null;
        PreparedStatement ps = null;
        try {

            factory.getPatientDAO().editPatient(preReg.getPatient(),hcpid);
            conn = factory.getConnection();
            ps = conn.prepareStatement("UPDATE PreRegisteredPatients SET height = ?, weight = ?, smoker = ? WHERE mid = ?");
            ps.setString(1,preReg.getHeight());
            ps.setString(2,preReg.getWeight());
            ps.setString(3,preReg.getSmoker());
            ps.setLong(4, preReg.getMid());
            ps.executeUpdate();
            ps.close();

            return true;

        } catch (SQLException e) {

            throw new DBException(e);
        } finally {
            DBUtil.closeConnection(conn, ps);
        }
    }
}
