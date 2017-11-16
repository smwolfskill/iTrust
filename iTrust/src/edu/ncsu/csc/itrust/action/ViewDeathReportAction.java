package edu.ncsu.csc.itrust.action;

import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.PatientDAO;
import edu.ncsu.csc.itrust.exception.DBException;

import java.sql.SQLException;
import java.util.List;

public class ViewDeathReportAction {
    private PatientDAO pDAO;

    public ViewDeathReportAction(DAOFactory factory) {
        this.pDAO = factory.getPatientDAO();
    }

    public List<List<String>> getDeaths(String gender, int startYear, int endYear) throws DBException, SQLException {
        return getDeathsForHCP(-1, gender, startYear, endYear);
    }

    public List<List<String>> getDeathsForHCP(long HCPID, String gender, int startYear, int endYear) throws DBException, SQLException {
        return pDAO.getCommonDeaths(HCPID, gender, startYear, endYear);
    }
}
