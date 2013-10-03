package nc.noumea.mairie.ptg.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import nc.noumea.mairie.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.MairiePrimeTableEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PointageCalcule;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.VentilAbsence;
import nc.noumea.mairie.ptg.domain.VentilDate;
import nc.noumea.mairie.ptg.domain.VentilHsup;
import nc.noumea.mairie.ptg.domain.VentilPrime;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Repository;

@Repository
public class VentilationRepository implements IVentilationRepository {

	@PersistenceContext(unitName = "ptgPersistenceUnit")
	private EntityManager ptgEntityManager;

	@Override
	public List<Date> getDistinctDatesOfPointages(Integer idAgent, Date fromEtatDate, Date toEtatDate) {

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT distinct (to_date(p.date_debut)) ");
		sb.append("FROM PTG_ETAT_POINTAGE ep ");
		sb.append("INNER JOIN PTG_POINTAGE p ON ep.ID_POINTAGE = p.ID_POINTAGE ");
		sb.append("INNER JOIN ( ");
		sb.append("SELECT epmax.id_pointage, max(epmax.id_etat_pointage) AS maxIdEtatPointage  ");
		sb.append("FROM ptg_etat_pointage epmax ");
		sb.append("INNER JOIN ptg_pointage ptg ON ptg.id_pointage = epmax.id_pointage ");
		sb.append("WHERE ptg.id_agent = :idAgent ");
		sb.append("GROUP BY epmax.id_pointage)  ");
		sb.append("maxEtats ON maxEtats.maxIdEtatPointage = ep.id_etat_pointage AND maxEtats.id_pointage = ep.id_pointage ");
		sb.append("WHERE p.ID_AGENT = :idAgent ");
		sb.append("AND (ep.date_etat BETWEEN :fromEtatDate AND :toEtatDate AND ep.etat = :approuve ");
		sb.append("OR ep.etat = :ventile) ");
		sb.append("ORDER BY (to_date(p.date_debut)) ASC ");

		Query q = ptgEntityManager.createNativeQuery(sb.toString());
		q.setParameter("idAgent", idAgent);
		q.setParameter("fromEtatDate", fromEtatDate);
		q.setParameter("toEtatDate", toEtatDate);
		q.setParameter("approuve", EtatPointageEnum.APPROUVE.getCodeEtat());
		q.setParameter("ventile", EtatPointageEnum.VENTILE.getCodeEtat());

		@SuppressWarnings("unchecked")
		List<Date> result = q.getResultList();

		return result;
	}

	@Override
	public List<Integer> getListIdAgentsForVentilationByDateAndEtat(Date fromEtatDate, Date toEtatDate) {

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT distinct (p.id_agent) as id_agent ");
		sb.append("FROM PTG_ETAT_POINTAGE ep ");
		sb.append("INNER JOIN PTG_POINTAGE p ON ep.ID_POINTAGE = p.ID_POINTAGE ");
		sb.append("INNER JOIN ( ");
		sb.append("SELECT epmax.id_pointage, max(epmax.id_etat_pointage) AS maxIdEtatPointage ");
		sb.append("FROM ptg_etat_pointage epmax ");
		sb.append("INNER JOIN ptg_pointage ptg ON ptg.id_pointage = epmax.id_pointage ");
		sb.append("GROUP BY epmax.id_pointage)  ");
		sb.append("maxEtats ON maxEtats.maxIdEtatPointage = ep.id_etat_pointage AND maxEtats.id_pointage = ep.id_pointage ");
		sb.append("WHERE ep.date_etat BETWEEN :fromEtatDate AND :toEtatDate AND ep.etat = :approuve ");
		sb.append("OR ep.etat = :ventile ");

		Query q = ptgEntityManager.createNativeQuery(sb.toString());
		q.setParameter("fromEtatDate", fromEtatDate);
		q.setParameter("toEtatDate", toEtatDate);
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
	public List<Integer> getListIdAgentsForExportPaie(Integer idVentilDate) {

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT distinct (ptg.id_agent) as id_agent ");
		sb.append("FROM PTG_POINTAGE ptg ");
		sb.append("INNER JOIN PTG_POINTAGE_VENTIL_DATE pv ON ptg.ID_POINTAGE = pv.ID_POINTAGE ");
		sb.append("WHERE pv.ID_VENTIL_DATE = :idVentilDate ");

		Query q = ptgEntityManager.createNativeQuery(sb.toString());
		q.setParameter("idVentilDate", idVentilDate);

		@SuppressWarnings("unchecked")
		List<BigDecimal> rawResult = q.getResultList();
		List<Integer> result = new ArrayList<Integer>();

		for (BigDecimal l : rawResult) {
			result.add(l.intValue());
		}

		return result;
	}

	@Override
	public List<Pointage> getListPointagesAbsenceAndHSupForVentilation(Integer idAgent, Date fromEtatDate,
			Date toEtatDate, Date dateLundi) {

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT p.* ");
		sb.append("FROM PTG_ETAT_POINTAGE ep ");
		sb.append("INNER JOIN PTG_POINTAGE p ON ep.ID_POINTAGE = p.ID_POINTAGE ");
		sb.append("INNER JOIN ( ");
		sb.append("SELECT epmax.id_pointage, max(epmax.id_etat_pointage) AS maxIdEtatPointage ");
		sb.append("FROM ptg_etat_pointage epmax ");
		sb.append("INNER JOIN ptg_pointage ptg ON ptg.id_pointage = epmax.id_pointage ");
		sb.append("WHERE ptg.id_agent = :idAgent ");
		sb.append("GROUP BY epmax.id_pointage)  ");
		sb.append("maxEtats ON maxEtats.maxIdEtatPointage = ep.id_etat_pointage AND maxEtats.id_pointage = ep.id_pointage ");
		sb.append("WHERE p.ID_AGENT = :idAgent ");
		sb.append("AND p.DATE_LUNDI = :dateLundi ");
		sb.append("AND (p.ID_TYPE_POINTAGE = :typePointageHSUP OR p.ID_TYPE_POINTAGE = :typePointageABS) ");
		sb.append("AND (ep.date_etat BETWEEN :fromEtatDate AND :toEtatDate AND ep.etat = :approuve ");
		sb.append("OR ep.etat IN (:ventile, :valide, :journalise)) ");
		sb.append("ORDER BY id_pointage ASC ");

		Query q = ptgEntityManager.createNativeQuery(sb.toString(), Pointage.class);
		q.setParameter("idAgent", idAgent);
		q.setParameter("dateLundi", dateLundi);
		q.setParameter("fromEtatDate", fromEtatDate);
		q.setParameter("toEtatDate", toEtatDate);
		q.setParameter("approuve", EtatPointageEnum.APPROUVE.getCodeEtat());
		q.setParameter("ventile", EtatPointageEnum.VENTILE.getCodeEtat());
		q.setParameter("typePointageHSUP", RefTypePointageEnum.H_SUP.getValue());
		q.setParameter("typePointageABS", RefTypePointageEnum.ABSENCE.getValue());
		q.setParameter("valide", EtatPointageEnum.VALIDE.getCodeEtat());
		q.setParameter("journalise", EtatPointageEnum.JOURNALISE.getCodeEtat());

		@SuppressWarnings("unchecked")
		List<Pointage> result = q.getResultList();

		return result;
	}

	@Override
	public List<Pointage> getListPointagesPrimeForVentilation(Integer idAgent, Date fromEtatDate, Date toEtatDate,
			Date dateDebutMois) {

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT p.* ");
		sb.append("FROM PTG_ETAT_POINTAGE ep ");
		sb.append("INNER JOIN PTG_POINTAGE p ON ep.ID_POINTAGE = p.ID_POINTAGE ");
		sb.append("INNER JOIN ( ");
		sb.append("SELECT epmax.id_pointage, max(epmax.id_etat_pointage) AS maxIdEtatPointage ");
		sb.append("FROM ptg_etat_pointage epmax ");
		sb.append("INNER JOIN ptg_pointage ptg ON ptg.id_pointage = epmax.id_pointage ");
		sb.append("WHERE ptg.id_agent = :idAgent ");
		sb.append("GROUP BY epmax.id_pointage)  ");
		sb.append("maxEtats ON maxEtats.maxIdEtatPointage = ep.id_etat_pointage AND maxEtats.id_pointage = ep.id_pointage ");
		sb.append("WHERE p.ID_AGENT = :idAgent ");
		sb.append("AND extract(MONTH FROM p.DATE_DEBUT) = :month ");
		sb.append("AND (p.ID_TYPE_POINTAGE = :typePointagePRIME) ");
		sb.append("AND (ep.date_etat BETWEEN :fromEtatDate AND :toEtatDate AND ep.etat = :approuve ");
		sb.append("OR ep.etat IN (:ventile, :valide, :journalise)) ");
		sb.append("ORDER BY id_pointage ASC ");

		Query q = ptgEntityManager.createNativeQuery(sb.toString(), Pointage.class);
		q.setParameter("idAgent", idAgent);
		q.setParameter("month", new LocalDate(dateDebutMois).monthOfYear().get());
		q.setParameter("fromEtatDate", fromEtatDate);
		q.setParameter("toEtatDate", toEtatDate);
		q.setParameter("approuve", EtatPointageEnum.APPROUVE.getCodeEtat());
		q.setParameter("ventile", EtatPointageEnum.VENTILE.getCodeEtat());
		q.setParameter("typePointagePRIME", RefTypePointageEnum.PRIME.getValue());
		q.setParameter("valide", EtatPointageEnum.VALIDE.getCodeEtat());
		q.setParameter("journalise", EtatPointageEnum.JOURNALISE.getCodeEtat());

		@SuppressWarnings("unchecked")
		List<Pointage> result = q.getResultList();

		return result;
	}

	@Override
	public List<Pointage> getListPointagesForPrimesCalculees(Integer idAgent, Date fromEtatDate, Date toEtatDate,
			Date dateLundi) {

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT p.* ");
		sb.append("FROM PTG_ETAT_POINTAGE ep ");
		sb.append("INNER JOIN PTG_POINTAGE p ON ep.ID_POINTAGE = p.ID_POINTAGE ");
		sb.append("INNER JOIN ( ");
		sb.append("SELECT epmax.id_pointage, max(epmax.id_etat_pointage) AS maxIdEtatPointage ");
		sb.append("FROM ptg_etat_pointage epmax ");
		sb.append("INNER JOIN ptg_pointage ptg ON ptg.id_pointage = epmax.id_pointage ");
		sb.append("WHERE ptg.id_agent = :idAgent ");
		sb.append("GROUP BY epmax.id_pointage) ");
		sb.append("maxEtats ON maxEtats.maxIdEtatPointage = ep.id_etat_pointage AND maxEtats.id_pointage = ep.id_pointage ");
		sb.append("WHERE p.ID_AGENT = :idAgent ");
		sb.append("AND p.DATE_LUNDI = :dateLundi ");
		sb.append("AND (ep.date_etat BETWEEN :fromEtatDate AND :toEtatDate AND ep.etat = :approuve ");
		sb.append("OR ep.etat IN (:ventile, :valide, :journalise)) ");
		sb.append("ORDER BY id_pointage DESC ");

		Query q = ptgEntityManager.createNativeQuery(sb.toString(), Pointage.class);
		q.setParameter("idAgent", idAgent);
		q.setParameter("dateLundi", dateLundi);
		q.setParameter("fromEtatDate", fromEtatDate);
		q.setParameter("toEtatDate", toEtatDate);
		q.setParameter("approuve", EtatPointageEnum.APPROUVE.getCodeEtat());
		q.setParameter("ventile", EtatPointageEnum.VENTILE.getCodeEtat());
		q.setParameter("valide", EtatPointageEnum.VALIDE.getCodeEtat());
		q.setParameter("journalise", EtatPointageEnum.JOURNALISE.getCodeEtat());

		@SuppressWarnings("unchecked")
		List<Pointage> result = q.getResultList();

		return result;
	}

	@Override
	public List<PointageCalcule> getListPointagesCalculesPrimeForVentilation(Integer idAgent, Date dateDebutMois) {

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT pc.* ");
		sb.append("FROM PTG_POINTAGE_CALCULE pc ");
		sb.append("WHERE pc.ID_AGENT = :idAgent ");
		sb.append("AND extract(MONTH FROM pc.DATE_DEBUT) = :month ");

		Query q = ptgEntityManager.createNativeQuery(sb.toString(), PointageCalcule.class);
		q.setParameter("idAgent", idAgent);
		q.setParameter("month", new LocalDate(dateDebutMois).monthOfYear().get());

		@SuppressWarnings("unchecked")
		List<PointageCalcule> result = q.getResultList();

		return result;
	}

	@Override
	public VentilDate getLatestVentilDate(TypeChainePaieEnum chainePaie, boolean isPaid) {

		TypedQuery<VentilDate> q = ptgEntityManager
				.createQuery(
						"SELECT d FROM VentilDate d where d.typeChainePaie = :chainePaie and d.paye = :paid ORDER BY d.dateVentilation desc",
						VentilDate.class);

		q.setParameter("chainePaie", chainePaie);
		q.setParameter("paid", isPaid);
		q.setMaxResults(1);

		List<VentilDate> list = q.getResultList();

		return list.size() == 0 ? null : list.get(0);
	}

	@Override
	public void removeVentilationsForDateAgentAndType(VentilDate ventilDate, Integer idAgent,
			RefTypePointageEnum typePointage) {

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

	@Override
	public List<VentilAbsence> getListOfVentilAbsenceForDateAgentAndType(Integer ventilDateId, List<Integer> agentIds) {
		List<VentilAbsence> resultat = new ArrayList<VentilAbsence>();
		List<Integer> agentIdsReduite = null;

		String query = "FROM VentilAbsence tb WHERE tb.idAgent in :agentIds AND  tb.ventilDate.idVentilDate = :ventilDateId";
		int fromIndex = 0;
		int toIndex = 0;

		do {
			toIndex = fromIndex + 1000;
			agentIdsReduite = agentIds.subList(fromIndex, toIndex > agentIds.size() ? agentIds.size() : toIndex);
			TypedQuery<VentilAbsence> q = ptgEntityManager.createQuery(query, VentilAbsence.class);
			q.setParameter("agentIds", agentIdsReduite);
			q.setParameter("ventilDateId", ventilDateId);
			resultat.addAll(q.getResultList());
			fromIndex = toIndex;
		} while (fromIndex < agentIds.size());

		return resultat;
	}

	@Override
	public List<VentilPrime> getListOfVentilPrimeForDateAgentAndType(Integer ventilDateId, List<Integer> agentIds) {
		List<VentilPrime> resultat = new ArrayList<VentilPrime>();
		List<Integer> agentIdsReduite = null;

		String query = "FROM VentilPrime tb WHERE tb.idAgent in :agentIds AND  tb.ventilDate.idVentilDate = :ventilDateId";
		int fromIndex = 0;
		int toIndex = 0;

		do {
			toIndex = fromIndex + 1000;
			agentIdsReduite = agentIds.subList(fromIndex, toIndex > agentIds.size() ? agentIds.size() : toIndex);
			TypedQuery<VentilPrime> q = ptgEntityManager.createQuery(query, VentilPrime.class);
			q.setParameter("agentIds", agentIdsReduite);
			q.setParameter("ventilDateId", ventilDateId);
			resultat.addAll(q.getResultList());
			fromIndex = toIndex;
		} while (fromIndex < agentIds.size());

		return resultat;
	}

	@Override
	public List<VentilHsup> getListOfVentilHSForDateAgentAndType(Integer ventilDateId, List<Integer> agentIds) {
		List<VentilHsup> resultat = new ArrayList<VentilHsup>();
		List<Integer> agentIdsReduite = null;

		String query = "FROM VentilHsup tb WHERE tb.idAgent in :agentIds AND  tb.ventilDate.idVentilDate= :ventilDateId";
		int fromIndex = 0;
		int toIndex = 0;

		do {
			toIndex = fromIndex + 1000;
			agentIdsReduite = agentIds.subList(fromIndex, toIndex > agentIds.size() ? agentIds.size() : toIndex);
			TypedQuery<VentilHsup> q = ptgEntityManager.createQuery(query, VentilHsup.class);
			q.setParameter("agentIds", agentIdsReduite);
			q.setParameter("ventilDateId", ventilDateId);
			resultat.addAll(q.getResultList());
			fromIndex = toIndex;
		} while (fromIndex < agentIds.size());

		return resultat;
	}

	@Override
	public List<VentilHsup> getListVentilHSupForAgentAndVentilDateOrderByDateAsc(Integer idAgent, Integer idVentilDate) {

		TypedQuery<VentilHsup> q = ptgEntityManager
				.createQuery(
						"from VentilHsup h where h.idAgent = :idAgent and h.ventilDate.idVentilDate = :idVentilDate order by h.dateLundi asc",
						VentilHsup.class);
		q.setParameter("idAgent", idAgent);
		q.setParameter("idVentilDate", idVentilDate);

		return q.getResultList();
	}

	@Override
	public List<VentilPrime> getListVentilPrimesMoisForAgentAndVentilDateOrderByDateAsc(Integer idAgent,
			Integer idVentilDate) {

		TypedQuery<VentilPrime> q = ptgEntityManager
				.createQuery(
						"from VentilPrime p where p.idAgent = :idAgent and p.ventilDate.idVentilDate = :idVentilDate and p.refPrime.mairiePrimeTableEnum = :mairiePrimeTableEnum order by p.dateDebutMois asc",
						VentilPrime.class);
		q.setParameter("idAgent", idAgent);
		q.setParameter("idVentilDate", idVentilDate);
		q.setParameter("mairiePrimeTableEnum", MairiePrimeTableEnum.SPPRIM);

		return q.getResultList();
	}

	@Override
	public boolean canStartVentilation(TypeChainePaieEnum chainePaie) {

		Query q = ptgEntityManager
				.createQuery("SELECT COUNT(vT) from VentilTask vT WHERE vT.taskStatus is NULL AND vT.dateVentilation is NULL AND vT.typeChainePaie = :chainePaie)");
		q.setParameter("chainePaie", chainePaie);

		return ((long) q.getSingleResult() == 0);
	}
}