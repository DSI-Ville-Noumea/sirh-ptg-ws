package nc.noumea.mairie.ptg.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.domain.VentilDate;

import org.springframework.stereotype.Repository;

@Repository
public class VentilationRepository implements IVentilationRepository {

	@PersistenceContext(unitName = "ptgPersistenceUnit")
	private EntityManager ptgEntityManager;
	
	@Override
	public List<Date> getDistinctDatesOfPointages(Integer idAgent, Date fromDate, Date toDate) {

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT distinct (to_date(p.date_debut)) ");
		sb.append("FROM PTG_ETAT_POINTAGE ep ");
		sb.append("INNER JOIN PTG_POINTAGE p ON ep.ID_POINTAGE = p.ID_POINTAGE ");
		sb.append("INNER JOIN ( ");
		sb.append("SELECT epmax.id_pointage, max(epmax.date_etat) AS maxdate  ");
		sb.append("FROM ptg_etat_pointage epmax ");
		sb.append("INNER JOIN ptg_pointage ptg ON ptg.id_pointage = epmax.id_pointage ");
		sb.append("WHERE ptg.id_agent = :idAgent ");
		sb.append("GROUP BY epmax.id_pointage)  ");
		sb.append("maxEtats ON maxEtats.maxdate = ep.date_etat AND maxEtats.id_pointage = ep.id_pointage ");
		sb.append("WHERE p.ID_AGENT = :idAgent ");
		sb.append("AND (ep.date_etat BETWEEN :fromDate AND :toDate AND ep.etat = :approuve ");
		sb.append("OR ep.etat = :ventile) ");
		sb.append("ORDER BY (to_date(p.date_debut)) ASC ");

		Query q = ptgEntityManager.createNativeQuery(sb.toString());
		q.setParameter("idAgent", idAgent);
		q.setParameter("fromDate", fromDate);
		q.setParameter("toDate", toDate);
		q.setParameter("approuve", EtatPointageEnum.APPROUVE.getCodeEtat());
		q.setParameter("ventile", EtatPointageEnum.VENTILE.getCodeEtat());
		
		@SuppressWarnings("unchecked")
		List<Date> result = q.getResultList();

		return result;
	}
	
	@Override
	public List<Pointage> getListPointagesAbsenceAndHSupForVentilation(Integer idAgent, Date fromEtatDate, Date toEtatDate, Date dateLundi) {

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT p.* ");
		sb.append("FROM PTG_ETAT_POINTAGE ep ");
		sb.append("INNER JOIN PTG_POINTAGE p ON ep.ID_POINTAGE = p.ID_POINTAGE ");
		sb.append("INNER JOIN ( ");
		sb.append("SELECT epmax.id_pointage, max(epmax.date_etat) AS maxdate  ");
		sb.append("FROM ptg_etat_pointage epmax ");
		sb.append("INNER JOIN ptg_pointage ptg ON ptg.id_pointage = epmax.id_pointage ");
		sb.append("WHERE ptg.id_agent = :idAgent ");
		sb.append("GROUP BY epmax.id_pointage)  ");
		sb.append("maxEtats ON maxEtats.maxdate = ep.date_etat AND maxEtats.id_pointage = ep.id_pointage ");
		sb.append("WHERE p.ID_AGENT = :idAgent ");
		sb.append("AND (p.ID_TYPE_POINTAGE = :typePointageHSUP OR p.ID_TYPE_POINTAGE = :typePointageABS) ");
		sb.append("AND (ep.date_etat BETWEEN :fromDate AND :toDate AND ep.etat = :approuve ");
		sb.append("OR ep.etat = :ventile) ");
		sb.append("ORDER BY id_pointage DESC ");

		Query q = ptgEntityManager.createNativeQuery(sb.toString(), Pointage.class);
		q.setParameter("idAgent", idAgent);
		q.setParameter("fromDate", fromEtatDate);
		q.setParameter("toDate", toEtatDate);
		q.setParameter("approuve", EtatPointageEnum.APPROUVE.getCodeEtat());
		q.setParameter("ventile", EtatPointageEnum.VENTILE.getCodeEtat());
		q.setParameter("typePointageHSUP", RefTypePointageEnum.H_SUP.getValue());
		q.setParameter("typePointageABS", RefTypePointageEnum.ABSENCE.getValue());
		
		@SuppressWarnings("unchecked")
		List<Pointage> result = q.getResultList();

		return result;
	}
	
	@Override
	public List<Pointage> getListPointagesPrimeForVentilation(Integer idAgent, Date fromEtatDate, Date toEtatDate, Date dateDebutMois) {

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT p.* ");
		sb.append("FROM PTG_ETAT_POINTAGE ep ");
		sb.append("INNER JOIN PTG_POINTAGE p ON ep.ID_POINTAGE = p.ID_POINTAGE ");
		sb.append("INNER JOIN ( ");
		sb.append("SELECT epmax.id_pointage, max(epmax.date_etat) AS maxdate  ");
		sb.append("FROM ptg_etat_pointage epmax ");
		sb.append("INNER JOIN ptg_pointage ptg ON ptg.id_pointage = epmax.id_pointage ");
		sb.append("WHERE ptg.id_agent = :idAgent ");
		sb.append("GROUP BY epmax.id_pointage)  ");
		sb.append("maxEtats ON maxEtats.maxdate = ep.date_etat AND maxEtats.id_pointage = ep.id_pointage ");
		sb.append("WHERE p.ID_AGENT = :idAgent ");
		sb.append("AND (p.ID_TYPE_POINTAGE = :typePointageHSUP OR p.ID_TYPE_POINTAGE = :typePointageABS) ");
		sb.append("AND (ep.date_etat BETWEEN :fromDate AND :toDate AND ep.etat = :approuve ");
		sb.append("OR ep.etat = :ventile) ");
		sb.append("ORDER BY id_pointage DESC ");

		Query q = ptgEntityManager.createNativeQuery(sb.toString(), Pointage.class);
		q.setParameter("idAgent", idAgent);
		q.setParameter("fromDate", fromEtatDate);
		q.setParameter("toDate", toEtatDate);
		q.setParameter("approuve", EtatPointageEnum.APPROUVE.getCodeEtat());
		q.setParameter("ventile", EtatPointageEnum.VENTILE.getCodeEtat());
		q.setParameter("typePointageHSUP", RefTypePointageEnum.H_SUP.getValue());
		q.setParameter("typePointageABS", RefTypePointageEnum.ABSENCE.getValue());
		
		@SuppressWarnings("unchecked")
		List<Pointage> result = q.getResultList();

		return result;
	}
	
	@Override
	public List<Integer> getListIdAgentsForVentilationByDateAndEtat(Date fromDate, Date toDate) {
		
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT distinct (p.id_agent) as id_agent ");
		sb.append("FROM PTG_ETAT_POINTAGE ep ");
		sb.append("INNER JOIN PTG_POINTAGE p ON ep.ID_POINTAGE = p.ID_POINTAGE ");
		sb.append("INNER JOIN ( ");
		sb.append("SELECT epmax.id_pointage, max(epmax.date_etat) AS maxdate  ");
		sb.append("FROM ptg_etat_pointage epmax ");
		sb.append("INNER JOIN ptg_pointage ptg ON ptg.id_pointage = epmax.id_pointage ");
		sb.append("GROUP BY epmax.id_pointage)  ");
		sb.append("maxEtats ON maxEtats.maxdate = ep.date_etat AND maxEtats.id_pointage = ep.id_pointage ");
		sb.append("WHERE ep.date_etat BETWEEN :fromDate AND :toDate AND ep.etat = :approuve ");
		sb.append("OR ep.etat = :ventile ");

		Query q = ptgEntityManager.createNativeQuery(sb.toString());
		q.setParameter("fromDate", fromDate);
		q.setParameter("toDate", toDate);
		q.setParameter("approuve", EtatPointageEnum.APPROUVE.getCodeEtat());
		q.setParameter("ventile", EtatPointageEnum.VENTILE.getCodeEtat());
		
		@SuppressWarnings("unchecked")
		List<BigDecimal> rawResult = q.getResultList();
		List<Integer> result = new ArrayList<Integer>();
		
		for (BigDecimal l : rawResult) {
			result.add(l.intValue());
		}
		
		return result;
	}

	@Override
	public VentilDate getLatestVentilDate(TypeChainePaieEnum chainePaie, boolean isPaid) {

		TypedQuery<VentilDate> q = ptgEntityManager.createQuery(
				"SELECT d FROM VentilDate d where d.typeChainePaie = :chainePaie and d.paye = :paid ORDER BY d.dateVentilation desc",
				VentilDate.class);

		q.setParameter("chainePaie", chainePaie);
		q.setParameter("paid", isPaid);
		q.setMaxResults(1);

		List<VentilDate> list = q.getResultList();

		return list.size() == 0 ? null : list.get(0);
	}

	@Override
	public void removeVentilationsForDateAgentAndType(VentilDate ventilDate, Integer idAgent, RefTypePointageEnum typePointage) {

		String query = null;

		switch (typePointage) {
		case PRIME:
			query = "DELETE FROM VentilPrime vp WHERE vp.ventilDate.idVentilDate = :idVentilDate and vp.idAgent = :idAgent";
			break;
		case H_SUP:
			query = "DELETE FROM VentilHsup vh WHERE vh.ventilDate.idVentilDate = :idVentilDate and vh.idAgent = :idAgent";
			break;
		case ABSENCE:
			query = "DELETE FROM VentilAbsence va WHERE va.ventilDate.idVentilDate = :idVentilDate and va.idAgent = :idAgent";
			break;
		}

		Query q = ptgEntityManager.createQuery(query);
		q.setParameter("idVentilDate", ventilDate.getIdVentilDate());
		q.setParameter("idAgent", idAgent);
		q.executeUpdate();
		ptgEntityManager.flush();
	}
}
