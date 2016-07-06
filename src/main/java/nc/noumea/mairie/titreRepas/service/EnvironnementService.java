package nc.noumea.mairie.titreRepas.service;

/*
 * #%L
 * sirh-kiosque-j2ee
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2014 Mairie de Nouméa
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("environnementService")
public class EnvironnementService {

	@Autowired
	@Qualifier("typeEnvironnement")
	private String typeEnvironnement;

	public String getTypeEnvironnement() {
		return typeEnvironnement;
	}

	public void setTypeEnvironnement(String typeEnvironnement) {
		this.typeEnvironnement = typeEnvironnement;
	}

	public boolean isRecette() {
		return getTypeEnvironnement().toUpperCase().equals("RECETTE");
	}

	public boolean isProduction() {
		return getTypeEnvironnement().toUpperCase().equals("PROD");
	}
}
