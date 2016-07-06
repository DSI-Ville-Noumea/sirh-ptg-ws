package nc.noumea.mairie.ptg.service;

import java.text.ParseException;

import nc.noumea.mairie.ptg.dto.FichePointageDto;
import nc.noumea.mairie.ptg.dto.FichePointageDtoKiosque;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;

public interface ISaisieService {

	public ReturnMessageDto saveFichePointage(Integer idAgentOperator, FichePointageDtoKiosque fichePointageDto)
			throws ParseException;

	public ReturnMessageDto saveFichePointage(Integer idAgentOperator, FichePointageDto fichePointageDto,
			boolean approveModifiedPointages) throws ParseException;

	public ReturnMessageDto saveFichePointageKiosque(Integer idAgentOperator, FichePointageDtoKiosque fichePointageDto,
			boolean approveModifiedPointages) throws ParseException;

}
