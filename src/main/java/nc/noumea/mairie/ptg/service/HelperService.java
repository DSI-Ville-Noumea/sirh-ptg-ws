package nc.noumea.mairie.ptg.service;

import java.util.Date;

import org.springframework.stereotype.Service;

@Service
public class HelperService {

	public Date getCurrentDate() {
		return new Date();
	}
}
