package nc.noumea.mairie.ptg.repository;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;


public class MairieIntegerDateType implements UserType {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    private static final int[] SQL_TYPES = { Types.NUMERIC };

	@Override
	public int[] sqlTypes() {
		return SQL_TYPES;
	}

	@Override
	public Class returnedClass() {
		return Date.class;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		if (x == null || y == null) {
            return false;
        } else if (x == y) {
                return true;
        } else {
            return x.equals(y);
        }
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names,
			SessionImplementor session, Object owner)
			throws HibernateException, SQLException {

		Integer originalDate = rs.getInt(names[0]);
        if (originalDate == null) {
            return null;
        }

        String strDate = originalDate.toString();
        if (strDate.length() != 1 && strDate.length() != 8) {
            throw new HibernateException(String.format("Could not convert object %s to date", strDate));
        }
        
        if (Integer.parseInt(strDate) == 0)
        	return null;

        Date result = null;
        try {
            result = dateFormat.parse(strDate);
        } catch (ParseException e) {
            // empty catch, we'll just log it and return null
            // if the date is bad
        	throw new HibernateException(String.format("Could not convert object %s to date", strDate));
        }

        return result;
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index,
			SessionImplementor session) throws HibernateException, SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	@Override
	public boolean isMutable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object assemble(Serializable cached, Object owner)
			throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object replace(Object original, Object target, Object owner)
			throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}

	
}
