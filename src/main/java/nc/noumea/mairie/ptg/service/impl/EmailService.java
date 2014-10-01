package nc.noumea.mairie.ptg.service.impl;

import nc.noumea.mairie.ptg.dto.EmailInfoDto;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.ptg.service.IEmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmailService implements IEmailService {

	@Autowired
	private IPointageRepository pointageRepository;

	@Override
	@Transactional(readOnly = true)
	public EmailInfoDto getListIdDestinatairesEmailInfo() {

		EmailInfoDto dto = new EmailInfoDto();

		dto.setListApprobateurs(pointageRepository.getListApprobateursPointagesSaisiesJourDonne());

		return dto;
	}

}
