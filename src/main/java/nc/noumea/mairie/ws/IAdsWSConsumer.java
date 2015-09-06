package nc.noumea.mairie.ws;

import nc.noumea.mairie.ads.dto.EntiteDto;

public interface IAdsWSConsumer {

	EntiteDto getEntiteWithChildrenByIdEntite(Integer idEntite);

	EntiteDto getInfoSiservByIdEntite(Integer idEntite);
}
