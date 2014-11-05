package nc.noumea.mairie.ptg.repository;

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
import nc.noumea.mairie.ptg.domain.VentilTask;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Repository;

@Repository
public class VentilationRepository implements IVentilationRepository {

	@PersistenceContext(unitName = "ptgPersistenceUnit")
	private EntityManager ptgEntityManager;

	@Override
	public List<Date> getDistinctDatesOfPointages(Integer idAgent, Date fromEtatDate, Date toEtatDate) {

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT distinct (p.date_debut) ");
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
		sb.append("OR ep.etat = :ventile OR ep.etat = :rejete) ");
		sb.append("ORDER BY (p.date_debut) ASC ");

		Query q = ptgEntityManager.createNativeQuery(sb.toString());
		q.setParameter("idAgent", idAgent);
		q.setParameter("fromEtatDate", fromEtatDate);
		q.setParameter("toEtatDate", toEtatDate);
		q.setParameter("approuve", EtatPointageEnum.APPROUVE.getCodeEtat());
		q.setParameter("ventile", EtatPointageEnum.VENTILE.getCodeEtat());
		q.setParameter("rejete", EtatPointageEnum.REJETE.getCodeEtat());

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
		sb.append("WHERE (ep.date_etat BETWEEN :fromEtatDate AND :toEtatDate AND ep.etat = :approuve ) ");
		sb.append("OR ep.etat = :ventile ");

		Query q = ptgEntityManager.createNativeQuery(sb.toString());
		q.setParameter("fromEtatDate", fromEtatDate);
		q.setParameter("toEtatDate", toEtatDate);
		q.setParameter("approuve", EtatPointageEnum.APPROUVE.getCodeEtat());
		q.setParameter("ventile", EtatPointageEnum.VENTILE.getCodeEtat());

		@SuppressWarnings("unchecked")
		List<Integer> result = q.getResultList();

		return result;
	}

	@Override
	public List<Integer> getListIdAgentsWithPointagesValidatedAndRejetes(Integer idVentilDate) {

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT distinct (p.id_agent) as id_agent ");
		sb.append("FROM PTG_ETAT_POINTAGE ep ");
		sb.append("INNER JOIN PTG_POINTAGE p ON ep.ID_POINTAGE = p.ID_POINTAGE ");
		sb.append("INNER JOIN PTG_POINTAGE_VENTIL_DATE pvd ON p.ID_POINTAGE = pvd.ID_POINTAGE and pvd.Id_VENTIL_DATE = :idVentilDate ");
		sb.append("INNER JOIN ( ");
		sb.append("SELECT epmax.id_pointage, max(epmax.id_etat_pointage) AS maxIdEtatPointage ");
		sb.append("FROM ptg_etat_pointage epmax ");
		sb.append("INNER JOIN ptg_pointage ptg ON ptg.id_pointage = epmax.id_pointage ");
		sb.append("GROUP BY epmax.id_pointage)  ");
		sb.append("maxEtats ON maxEtats.maxIdEtatPointage = ep.id_etat_pointage AND maxEtats.id_pointage = ep.id_pointage ");
		sb.append("WHERE ep.etat = :rejete ");

		Query q = ptgEntityManager.createNativeQuery(sb.toString());
		q.setParameter("rejete", EtatPointageEnum.REJETE.getCodeEtat());
		q.setParameter("idVentilDate", idVentilDate);

		@SuppressWarnings("unchecked")
		List<Integer> result = q.getResultList();

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
		List<Integer> result = q.getResultList();

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
		sb.append("AND ((ep.date_etat BETWEEN :fromEtatDate AND :toEtatDate AND ep.etat = :approuve) ");
		sb.append("OR ep.etat IN (:ventile, :valide, :journalise, :refuse, :rejete)) ");
		sb.append("ORDER BY id_pointage DESC ");

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
		q.setParameter("refuse", EtatPointageEnum.REFUSE.getCodeEtat());
		q.setParameter("rejete", EtatPointageEnum.REJETE.getCodeEtat());

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
		sb.append("ORDER BY id_pointage DESC ");

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
	public List<Pointage> getListPointagesPrimeValideByMoisAndRefPrime(Integer idAgent, Date dateMois,
			Integer idRefPrime) {

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
		sb.append("AND p.ID_TYPE_POINTAGE = :typePointagePRIME ");
		sb.append("AND p.ID_REF_PRIME = :idRefPrime ");
		sb.append("AND ep.etat = :valide ");
		sb.append("ORDER BY id_pointage DESC ");

		Query q = ptgEntityManager.createNativeQuery(sb.toString(), Pointage.class);
		q.setParameter("idAgent", idAgent);
		q.setParameter("month", new LocalDate(dateMois).monthOfYear().get());
		q.setParameter("typePointagePRIME", RefTypePointageEnum.PRIME.getValue());
		q.setParameter("valide", EtatPointageEnum.VALIDE.getCodeEtat());
		q.setParameter("idRefPrime", idRefPrime);

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
				query = "DELETE FROM VentilPrime vp WHERE vp.ventilDate.idVentilDate = :idVentilDate and vp.idAgent = :idAgent and vp.etat = :etat ";
				break;
			case H_SUP:
				query = "DELETE FROM VentilHsup vh WHERE vh.ventilDate.idVentilDate = :idVentilDate and vh.idAgent = :idAgent and vh.etat = :etat ";
				break;
			case ABSENCE:
				query = "DELETE FROM VentilAbsence va WHERE va.ventilDate.idVentilDate = :idVentilDate and va.idAgent = :idAgent and va.etat = :etat ";
				break;
		}

		Query q = ptgEntityManager.createQuery(query);
		q.setParameter("idVentilDate", ventilDate.getIdVentilDate());
		q.setParameter("idAgent", idAgent);
		q.setParameter("etat", EtatPointageEnum.VENTILE);
		q.executeUpdate();
		ptgEntityManager.flush();
	}

	@Override
	public List<VentilAbsence> getListOfVentilAbsenceForDateAgent(Integer ventilDateId, List<Integer> agentIds) {
		List<VentilAbsence> resultat = new ArrayList<VentilAbsence>();
		List<Integer> agentIdsReduite = null;

		String query = "FROM VentilAbsence tb WHERE tb.idAgent in :agentIds AND  tb.ventilDate.idVentilDate = :ventilDateId and tb.etat = :etat ";
		int fromIndex = 0;
		int toIndex = 0;

		do {
			toIndex = fromIndex + 1000;
			agentIdsReduite = agentIds.subList(fromIndex, toIndex > agentIds.size() ? agentIds.size() : toIndex);
			TypedQuery<VentilAbsence> q = ptgEntityManager.createQuery(query, VentilAbsence.class);
			q.setParameter("agentIds", agentIdsReduite);
			q.setParameter("ventilDateId", ventilDateId);
			q.setParameter("etat", EtatPointageEnum.VENTILE);
			resultat.addAll(q.getResultList());
			fromIndex = toIndex;
		} while (fromIndex < agentIds.size());

		return resultat;
	}

	@Override
	public List<VentilAbsence> getListOfVentilAbsenceForDateAgentAllVentilation(Integer ventilDateId,
			List<Integer> agentIds) {
		List<VentilAbsence> resultat = new ArrayList<VentilAbsence>();
		List<Integer> agentIdsReduite = null;

		StringBuilder sb = new StringBuilder();
		sb.append("select * from ptg_ventil_absence ");
		sb.append("WHERE id_ventil_date =:ventilDateId and id_agent in :agentIds  and id_ventil_absence in ");
		sb.append("(select max(id_ventil_absence) from ptg_ventil_absence group by date_lundi,id_agent) ");

		int fromIndex = 0;
		int toIndex = 0;

		do {
			toIndex = fromIndex + 1000;
			agentIdsReduite = agentIds.subList(fromIndex, toIndex > agentIds.size() ? agentIds.size() : toIndex);
			Query q = ptgEntityManager.createNativeQuery(sb.toString(), VentilAbsence.class);
			q.setParameter("agentIds", agentIdsReduite);
			q.setParameter("ventilDateId", ventilDateId);

			@SuppressWarnings("unchecked")
			List<VentilAbsence> result = q.getResultList();
			resultat.addAll(result);
			fromIndex = toIndex;
		} while (fromIndex < agentIds.size());

		return resultat;
	}

	@Override
	public List<VentilPrime> getListOfVentilPrimeForDateAgent(Integer ventilDateId, List<Integer> agentIds,
			boolean isShowVentilation) {
		List<VentilPrime> resultat = new ArrayList<VentilPrime>();
		List<Integer> agentIdsReduite = null;

		String query = "FROM VentilPrime tb WHERE tb.idAgent in :agentIds AND  tb.ventilDate.idVentilDate = :ventilDateId and tb.etat = :etat ";
		if (isShowVentilation) {
			query += "and quantite <> 0 ";
		}

		int fromIndex = 0;
		int toIndex = 0;

		do {
			toIndex = fromIndex + 1000;
			agentIdsReduite = agentIds.subList(fromIndex, toIndex > agentIds.size() ? agentIds.size() : toIndex);
			TypedQuery<VentilPrime> q = ptgEntityManager.createQuery(query, VentilPrime.class);
			q.setParameter("agentIds", agentIdsReduite);
			q.setParameter("ventilDateId", ventilDateId);
			q.setParameter("etat", EtatPointageEnum.VENTILE);

			resultat.addAll(q.getResultList());
			fromIndex = toIndex;
		} while (fromIndex < agentIds.size());

		return resultat;
	}

	@Override
	public List<VentilPrime> getListOfVentilPrimeForDateAgentAllVentilation(Integer ventilDateId,
			List<Integer> agentIds, boolean isShowVentilation) {
		List<VentilPrime> resultat = new ArrayList<VentilPrime>();
		List<Integer> agentIdsReduite = null;

		StringBuilder sb = new StringBuilder();
		sb.append("select * from ptg_ventil_prime ");
		sb.append("WHERE id_ventil_date =:ventilDateId and id_agent in :agentIds  and id_ventil_prime in ");
		sb.append("(select max(id_ventil_prime) from ptg_ventil_prime group by date_debut_mois,id_agent,id_ref_prime) ");

		int fromIndex = 0;
		int toIndex = 0;

		do {
			toIndex = fromIndex + 1000;
			agentIdsReduite = agentIds.subList(fromIndex, toIndex > agentIds.size() ? agentIds.size() : toIndex);
			Query q = ptgEntityManager.createNativeQuery(sb.toString(), VentilPrime.class);
			q.setParameter("agentIds", agentIdsReduite);
			q.setParameter("ventilDateId", ventilDateId);

			@SuppressWarnings("unchecked")
			List<VentilPrime> result = q.getResultList();
			resultat.addAll(result);
			fromIndex = toIndex;
		} while (fromIndex < agentIds.size());

		return resultat;
	}

	@Override
	public List<VentilHsup> getListOfVentilHSForDateAgent(Integer ventilDateId, List<Integer> agentIds) {
		List<VentilHsup> resultat = new ArrayList<VentilHsup>();
		List<Integer> agentIdsReduite = null;

		String query = "FROM VentilHsup tb WHERE tb.idAgent in :agentIds AND  tb.ventilDate.idVentilDate= :ventilDateId and tb.etat = :etat ";
		int fromIndex = 0;
		int toIndex = 0;

		do {
			toIndex = fromIndex + 1000;
			agentIdsReduite = agentIds.subList(fromIndex, toIndex > agentIds.size() ? agentIds.size() : toIndex);
			TypedQuery<VentilHsup> q = ptgEntityManager.createQuery(query, VentilHsup.class);
			q.setParameter("agentIds", agentIdsReduite);
			q.setParameter("ventilDateId", ventilDateId);
			q.setParameter("etat", EtatPointageEnum.VENTILE);

			resultat.addAll(q.getResultList());
			fromIndex = toIndex;
		} while (fromIndex < agentIds.size());

		return resultat;
	}

	@Override
	public List<VentilHsup> getListOfVentilHSForDateAgentAllVentilation(Integer ventilDateId, List<Integer> agentIds) {
		List<VentilHsup> resultat = new ArrayList<VentilHsup>();
		List<Integer> agentIdsReduite = null;

		StringBuilder sb = new StringBuilder();
		sb.append("select * from ptg_ventil_hsup ");
		sb.append("WHERE id_ventil_date =:ventilDateId and id_agent in :agentIds  and id_ventil_hsup in ");
		sb.append("(select max(id_ventil_hsup) from ptg_ventil_hsup group by date_lundi,id_agent) ");

		int fromIndex = 0;
		int toIndex = 0;

		do {
			toIndex = fromIndex + 1000;
			agentIdsReduite = agentIds.subList(fromIndex, toIndex > agentIds.size() ? agentIds.size() : toIndex);
			Query q = ptgEntityManager.createNativeQuery(sb.toString(), VentilHsup.class);
			q.setParameter("agentIds", agentIdsReduite);
			q.setParameter("ventilDateId", ventilDateId);

			@SuppressWarnings("unchecked")
			List<VentilHsup> result = q.getResultList();
			resultat.addAll(result);
			fromIndex = toIndex;
		} while (fromIndex < agentIds.size());

		return resultat;
	}

	@Override
	public List<VentilHsup> getListVentilHSupForAgentAndVentilDateOrderByDateAsc(Integer idAgent, Integer idVentilDate) {

		TypedQuery<VentilHsup> q = ptgEntityManager
				.createQuery(
						"from VentilHsup h where h.idAgent = :idAgent and h.ventilDate.idVentilDate = :idVentilDate and h.etat = :etat order by h.dateLundi asc",
						VentilHsup.class);
		q.setParameter("idAgent", idAgent);
		q.setParameter("idVentilDate", idVentilDate);
		q.setParameter("etat", EtatPointageEnum.VENTILE);

		return q.getResultList();
	}

	@Override
	public List<VentilPrime> getListVentilPrimesMoisForAgentAndVentilDateOrderByDateAsc(Integer idAgent,
			Integer idVentilDate) {

		TypedQuery<VentilPrime> q = ptgEntityManager
				.createQuery(
						"from VentilPrime p where p.idAgent = :idAgent and p.ventilDate.idVentilDate = :idVentilDate and p.refPrime.mairiePrimeTableEnum = :mairiePrimeTableEnum"
								+ " and p.etat = :etat order by p.dateDebutMois asc", VentilPrime.class);
		q.setParameter("idAgent", idAgent);
		q.setParameter("idVentilDate", idVentilDate);
		q.setParameter("etat", EtatPointageEnum.VENTILE);
		q.setParameter("mairiePrimeTableEnum", MairiePrimeTableEnum.SPPRIM);

		return q.getResultList();
	}

	@Override
	public List<VentilAbsence> getListVentilAbsencesForAgentAndVentilDate(Integer idAgent, Integer idVentilDate) {

		TypedQuery<VentilAbsence> q = ptgEntityManager
				.createQuery(
						"from VentilAbsence a where a.idAgent = :idAgent and a.ventilDate.idVentilDate = :idVentilDate and a.etat = :etat ",
						VentilAbsence.class);
		q.setParameter("idAgent", idAgent);
		q.setParameter("idVentilDate", idVentilDate);
		q.setParameter("etat", EtatPointageEnum.VENTILE);

		return q.getResultList();
	}

	@Override
	public boolean canStartVentilation(TypeChainePaieEnum chainePaie) {

		Query q = ptgEntityManager
				.createQuery("SELECT COUNT(vT) from VentilTask vT WHERE vT.taskStatus is NULL AND vT.dateVentilation is NULL AND vT.typeChainePaie = :chainePaie)");
		q.setParameter("chainePaie", chainePaie);

		return ((long) q.getSingleResult() == 0);
	}

	@Override
	public VentilAbsence getPriorVentilAbsenceForAgentAndDate(Integer idAgent, Date dateLundi,
			VentilAbsence latestVentilAbsence) {

		TypedQuery<VentilAbsence> q = ptgEntityManager.createNamedQuery("getPriorVentilAbsenceForAgentAndDate",
				VentilAbsence.class);
		q.setParameter("idAgent", idAgent);
		q.setParameter("dateLundi", dateLundi);
		q.setParameter("idLatestVentilAbsence", latestVentilAbsence.getIdVentilAbsence());
		q.setMaxResults(1);

		List<VentilAbsence> vas = q.getResultList();

		return vas.size() != 0 ? vas.get(0) : null;
	}

	@Override
	public VentilHsup getPriorVentilHSupAgentAndDate(Integer idAgent, Date dateLundi, VentilHsup latestVentilHsup) {

		TypedQuery<VentilHsup> q = ptgEntityManager
				.createNamedQuery("getPriorVentilHSupAgentAndDate", VentilHsup.class);
		q.setParameter("idAgent", idAgent);
		q.setParameter("dateLundi", dateLundi);
		q.setParameter("idLatestVentilHSup", latestVentilHsup.getIdVentilHSup());
		q.setMaxResults(1);

		List<VentilHsup> vas = q.getResultList();

		return vas.size() != 0 ? vas.get(0) : null;
	}

	@Override
	public VentilPrime getPriorVentilPrimeForAgentAndDate(Integer idAgent, Date dateDebMois,
			VentilPrime latestVentilPrime) {

		TypedQuery<VentilPrime> q = ptgEntityManager.createNamedQuery("getPriorVentilPrimeForAgentAndDate",
				VentilPrime.class);
		q.setParameter("idAgent", idAgent);
		q.setParameter("dateDebutMois", dateDebMois);
		q.setParameter("idLatestVentilPrime", latestVentilPrime.getIdVentilPrime());
		q.setMaxResults(1);

		List<VentilPrime> vas = q.getResultList();

		return vas.size() != 0 ? vas.get(0) : null;
	}

	@Override
	public void persistEntity(Object entity) {
		ptgEntityManager.persist(entity);
	}

	@Override
	public List<VentilTask> getListOfVentilTaskErreur(TypeChainePaieEnum chainePaie, VentilDate ventilDateTo) {

		TypedQuery<VentilTask> q = ptgEntityManager
				.createQuery(
						"from VentilTask vT WHERE vT.taskStatus <> 'OK' AND vT.typeChainePaie = :chainePaie AND ventilDateTo = :ventilDateTo ",
						VentilTask.class);
		q.setParameter("chainePaie", chainePaie);
		q.setParameter("ventilDateTo", ventilDateTo);

		return q.getResultList();
	}

	@Override
	public List<VentilAbsence> getListOfVentilAbsenceForAgentBeetweenDate(Integer mois, Integer annee, Integer idAgent) {
		List<VentilAbsence> resultat = new ArrayList<VentilAbsence>();
		Date dateDeb = new DateTime(annee, mois, 1, 0, 0, 0).toDate();
		LocalDate lastDayOfMonth = new LocalDate(annee, mois, 1).dayOfMonth().withMaximumValue();
		Date dateFin = lastDayOfMonth.toDate();

		String query = "FROM VentilAbsence tb WHERE tb.idAgent = :idAgent AND  tb.dateLundi BETWEEN :datdeb AND :datfin and tb.etat = :etat ";

		TypedQuery<VentilAbsence> q = ptgEntityManager.createQuery(query, VentilAbsence.class);
		q.setParameter("idAgent", idAgent);
		q.setParameter("datdeb", dateDeb);
		q.setParameter("datfin", dateFin);
		q.setParameter("etat", EtatPointageEnum.VENTILE);

		resultat.addAll(q.getResultList());

		return resultat;
	}

	@Override
	public List<VentilAbsence> getListOfVentilAbsenceForAgentBeetweenDateAllVentilation(Integer mois, Integer annee,
			Integer idAgent) {
		List<VentilAbsence> resultat = new ArrayList<VentilAbsence>();
		Date dateDeb = new DateTime(annee, mois, 1, 0, 0, 0).toDate();
		LocalDate lastDayOfMonth = new LocalDate(annee, mois, 1).dayOfMonth().withMaximumValue();
		Date dateFin = lastDayOfMonth.toDate();

		StringBuilder sb = new StringBuilder();
		sb.append("select * from ptg_ventil_absence ");
		sb.append("WHERE date_lundi between :datdeb AND :datfin and id_agent = :idAgent  and id_ventil_absence in ");
		sb.append("(select max(id_ventil_absence) from ptg_ventil_absence group by date_lundi,id_agent) ");

		Query q = ptgEntityManager.createNativeQuery(sb.toString(), VentilAbsence.class);
		q.setParameter("idAgent", idAgent);
		q.setParameter("datdeb", dateDeb);
		q.setParameter("datfin", dateFin);

		@SuppressWarnings("unchecked")
		List<VentilAbsence> result = q.getResultList();
		resultat.addAll(result);

		return resultat;
	}

	@Override
	public List<VentilHsup> getListOfVentilHSForAgentBeetweenDate(Integer mois, Integer annee, Integer idAgent) {
		List<VentilHsup> resultat = new ArrayList<VentilHsup>();
		Date dateDeb = new DateTime(annee, mois, 1, 0, 0, 0).toDate();
		LocalDate lastDayOfMonth = new LocalDate(annee, mois, 1).dayOfMonth().withMaximumValue();
		Date dateFin = lastDayOfMonth.toDate();

		String query = "FROM VentilHsup tb WHERE tb.idAgent = :idAgent AND  tb.dateLundi BETWEEN :datdeb AND :datfin AND tb.etat = :etat ";

		TypedQuery<VentilHsup> q = ptgEntityManager.createQuery(query, VentilHsup.class);
		q.setParameter("idAgent", idAgent);
		q.setParameter("datdeb", dateDeb);
		q.setParameter("datfin", dateFin);
		q.setParameter("etat", EtatPointageEnum.VENTILE);

		resultat.addAll(q.getResultList());

		return resultat;
	}

	@Override
	public List<VentilHsup> getListOfVentilHSForAgentBeetweenDateAllVentilation(Integer mois, Integer annee,
			Integer idAgent) {
		List<VentilHsup> resultat = new ArrayList<VentilHsup>();
		Date dateDeb = new DateTime(annee, mois, 1, 0, 0, 0).toDate();
		LocalDate lastDayOfMonth = new LocalDate(annee, mois, 1).dayOfMonth().withMaximumValue();
		Date dateFin = lastDayOfMonth.toDate();

		StringBuilder sb = new StringBuilder();
		sb.append("select * from ptg_ventil_hsup ");
		sb.append("WHERE date_lundi between :datdeb AND :datfin and id_agent = :idAgent  and id_ventil_hsup in ");
		sb.append("(select max(id_ventil_hsup) from ptg_ventil_hsup group by date_lundi,id_agent) ");

		Query q = ptgEntityManager.createNativeQuery(sb.toString(), VentilHsup.class);
		q.setParameter("idAgent", idAgent);
		q.setParameter("datdeb", dateDeb);
		q.setParameter("datfin", dateFin);

		@SuppressWarnings("unchecked")
		List<VentilHsup> result = q.getResultList();
		resultat.addAll(result);

		return resultat;
	}

	@Override
	public List<Integer> getListAgentsForShowVentilationPrimesForDate(Integer ventilDateId, Integer agentMin,
			Integer agentMax, boolean allVentilation) {

		StringBuilder sb = new StringBuilder();
		sb.append("select distinct(tb.idAgent) from VentilPrime tb where tb.ventilDate.idVentilDate = :ventilDateId AND tb.etat in :etat ");

		if (null != agentMin && 0 != agentMin && null != agentMax && 0 != agentMax) {
			sb.append(" and tb.idAgent between :agentMin and :agentMax ");
		}

		TypedQuery<Integer> query = ptgEntityManager.createQuery(sb.toString(), Integer.class);
		query.setParameter("ventilDateId", ventilDateId);
		if (allVentilation) {
			List<EtatPointageEnum> liste = new ArrayList<>();
			liste.add(EtatPointageEnum.VALIDE);
			liste.add(EtatPointageEnum.VENTILE);

			query.setParameter("etat", liste);
		} else {
			query.setParameter("etat", EtatPointageEnum.VENTILE);
		}

		if (null != agentMin && 0 != agentMin && null != agentMax && 0 != agentMax) {
			query.setParameter("agentMin", agentMin);
			query.setParameter("agentMax", agentMax);
		}

		return query.getResultList();
	}

	@Override
	public List<Integer> getListAgentsForShowVentilationAbsencesForDate(Integer ventilDateId, Integer agentMin,
			Integer agentMax, boolean allVentilation) {

		StringBuilder sb = new StringBuilder();
		sb.append("select distinct(tb.idAgent) from VentilAbsence tb where tb.ventilDate.idVentilDate = :ventilDateId AND tb.etat in :etat ");

		if (null != agentMin && 0 != agentMin && null != agentMax && 0 != agentMax) {
			sb.append(" and tb.idAgent between :agentMin and :agentMax ");
		}

		TypedQuery<Integer> query = ptgEntityManager.createQuery(sb.toString(), Integer.class);
		query.setParameter("ventilDateId", ventilDateId);
		if (allVentilation) {
			List<EtatPointageEnum> liste = new ArrayList<>();
			liste.add(EtatPointageEnum.VALIDE);
			liste.add(EtatPointageEnum.VENTILE);

			query.setParameter("etat", liste);
		} else {
			query.setParameter("etat", EtatPointageEnum.VENTILE);
		}

		if (null != agentMin && 0 != agentMin && null != agentMax && 0 != agentMax) {
			query.setParameter("agentMin", agentMin);
			query.setParameter("agentMax", agentMax);
		}

		return query.getResultList();
	}

	@Override
	public List<Integer> getListAgentsForShowVentilationHeuresSupForDate(Integer ventilDateId, Integer agentMin,
			Integer agentMax, boolean allVentilation) {

		StringBuilder sb = new StringBuilder();
		sb.append("select distinct(tb.idAgent) from VentilHsup tb where tb.ventilDate.idVentilDate = :ventilDateId AND tb.etat in :etat ");

		if (null != agentMin && 0 != agentMin && null != agentMax && 0 != agentMax) {
			sb.append(" and tb.idAgent between :agentMin and :agentMax ");
		}

		TypedQuery<Integer> query = ptgEntityManager.createQuery(sb.toString(), Integer.class);
		query.setParameter("ventilDateId", ventilDateId);
		if (allVentilation) {
			List<EtatPointageEnum> liste = new ArrayList<>();
			liste.add(EtatPointageEnum.VALIDE);
			liste.add(EtatPointageEnum.VENTILE);

			query.setParameter("etat", liste);
		} else {
			query.setParameter("etat", EtatPointageEnum.VENTILE);
		}

		if (null != agentMin && 0 != agentMin && null != agentMax && 0 != agentMax) {
			query.setParameter("agentMin", agentMin);
			query.setParameter("agentMax", agentMax);
		}

		return query.getResultList();
	}

	@Override
	public List<VentilHsup> getListOfOldVentilHSForAgentAndDateLundi(Integer idAgent, Date dateLundi,
			Integer ventilDateId) {
		List<VentilHsup> resultat = new ArrayList<VentilHsup>();

		String query = "FROM VentilHsup tb WHERE tb.idAgent = :idAgent AND  tb.dateLundi = :dateLundi AND tb.ventilDate.idVentilDate = :ventilDateId AND tb.etat = :etat ";

		TypedQuery<VentilHsup> q = ptgEntityManager.createQuery(query, VentilHsup.class);
		q.setParameter("idAgent", idAgent);
		q.setParameter("dateLundi", dateLundi);
		q.setParameter("ventilDateId", ventilDateId);
		q.setParameter("etat", EtatPointageEnum.VALIDE);
		resultat.addAll(q.getResultList());

		return resultat;
	}

	@Override
	public List<VentilPrime> getListOfOldVentilPrimeForAgentAndDateDebutMois(Integer idAgent, Date dateDebutMois,
			Integer ventilDateId) {

		List<VentilPrime> resultat = new ArrayList<VentilPrime>();

		String query = "FROM VentilPrime tb WHERE tb.idAgent = :idAgent AND  tb.dateDebutMois = :dateDebutMois AND tb.ventilDate.idVentilDate = :ventilDateId AND tb.etat = :etat ";

		TypedQuery<VentilPrime> q = ptgEntityManager.createQuery(query, VentilPrime.class);
		q.setParameter("idAgent", idAgent);
		q.setParameter("dateDebutMois", dateDebutMois);
		q.setParameter("ventilDateId", ventilDateId);
		q.setParameter("etat", EtatPointageEnum.VALIDE);
		resultat.addAll(q.getResultList());

		return resultat;
	}

	@Override
	public List<VentilAbsence> getListOfVentilAbsenceWithDateForEtatPayeur(Integer idVentilDate) {
		List<VentilAbsence> resultat = new ArrayList<VentilAbsence>();

		StringBuilder sb = new StringBuilder();
		sb.append("select tb.* FROM PTG_VENTIL_ABSENCE tb WHERE tb.ID_VENTIL_ABSENCE in  ");
		sb.append("(select max(id_ventil_absence) from ptg_ventil_absence where id_ventil_date=:ventilDateId and etat=:etat group by id_agent,date_lundi) ");
		sb.append("order by date_Lundi asc, id_Agent asc ");

		Query q = ptgEntityManager.createNativeQuery(sb.toString(), VentilAbsence.class);
		q.setParameter("ventilDateId", idVentilDate);
		q.setParameter("etat", EtatPointageEnum.VALIDE.getCodeEtat());

		@SuppressWarnings("unchecked")
		List<VentilAbsence> result = q.getResultList();
		resultat.addAll(result);

		return resultat;
	}

	@Override
	public List<VentilHsup> getListVentilHSupForAgentAndVentilDateOrderByDateAscForReposComp(Integer idAgent, Integer idVentilDate) {
		
		List<VentilHsup> resultat = new ArrayList<VentilHsup>();

		StringBuilder sb = new StringBuilder();
		sb.append("select tb.* FROM PTG_VENTIL_HSUP tb WHERE tb.ID_VENTIL_HSUP in ");
		sb.append("(select max(id_ventil_hsup) from ptg_ventil_hsup where id_ventil_date=:ventilDateId and id_agent=:idAgent and etat=:etat group by date_lundi) ");
		sb.append("order by date_Lundi asc ");

		Query q = ptgEntityManager.createNativeQuery(sb.toString(), VentilHsup.class);
		q.setParameter("ventilDateId", idVentilDate);
		q.setParameter("idAgent", idAgent);
		q.setParameter("etat", EtatPointageEnum.VALIDE.getCodeEtat());

		@SuppressWarnings("unchecked")
		List<VentilHsup> result = q.getResultList();
		resultat.addAll(result);

		return resultat;
	}
	
	@Override
	public List<VentilHsup> getListOfVentilHeuresSupWithDateForEtatPayeur(Integer idVentilDate) {
		List<VentilHsup> resultat = new ArrayList<VentilHsup>();

		StringBuilder sb = new StringBuilder();
		sb.append("select tb.* FROM PTG_VENTIL_HSUP tb WHERE tb.ID_VENTIL_HSUP in  ");
		sb.append("(select max(id_ventil_hsup) from ptg_ventil_hsup where id_ventil_date=:ventilDateId and etat=:etat group by id_agent,date_lundi) ");
		sb.append("order by date_Lundi asc, id_Agent asc ");

		Query q = ptgEntityManager.createNativeQuery(sb.toString(), VentilHsup.class);
		q.setParameter("ventilDateId", idVentilDate);
		q.setParameter("etat", EtatPointageEnum.VALIDE.getCodeEtat());

		@SuppressWarnings("unchecked")
		List<VentilHsup> result = q.getResultList();
		resultat.addAll(result);

		return resultat;
	}

	@Override
	public List<VentilPrime> getListOfVentilPrimeWithDateForEtatPayeur(Integer idVentilDate) {
		List<VentilPrime> resultat = new ArrayList<VentilPrime>();

		StringBuilder sb = new StringBuilder();
		sb.append("select tb.* FROM PTG_VENTIL_PRIME tb WHERE tb.ID_VENTIL_PRIME in  ");
		sb.append("(select max(id_ventil_prime) from ptg_ventil_prime where id_ventil_date=:ventilDateId and etat=:etat group by id_agent,date_debut_mois) ");
		sb.append("order by date_debut_mois asc, id_Agent asc ");

		Query q = ptgEntityManager.createNativeQuery(sb.toString(), VentilPrime.class);
		q.setParameter("ventilDateId", idVentilDate);
		q.setParameter("etat", EtatPointageEnum.VALIDE.getCodeEtat());

		@SuppressWarnings("unchecked")
		List<VentilPrime> result = q.getResultList();
		resultat.addAll(result);

		return resultat;
	}
}