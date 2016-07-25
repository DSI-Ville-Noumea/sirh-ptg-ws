package nc.noumea.mairie.ptg.domain;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

@Entity
@RevisionEntity(AuditListener.class)
@Table(name = "revinfo")
public class RevInfo implements Serializable {

	private static final long	serialVersionUID	= -5670027823511344741L;

	@Id
	@GeneratedValue
	@RevisionNumber
	private int					rev;

	@RevisionTimestamp
	@Column(name = "timestamp")
	@Temporal(TemporalType.TIMESTAMP)
	private Date				timestamp;

	@Transient
	public Date getRevisionDate() {
		return timestamp;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "RevInfo(rev = " + rev + ", revisionDate = " + DateFormat.getDateTimeInstance().format(getRevisionDate()) + ")";
	}

	public int getRev() {
		return rev;
	}

	public void setRev(int rev) {
		this.rev = rev;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + rev;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RevInfo other = (RevInfo) obj;
		if (rev != other.rev)
			return false;
		return true;
	}

}