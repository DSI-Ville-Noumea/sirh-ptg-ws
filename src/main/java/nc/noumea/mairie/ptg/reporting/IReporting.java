package nc.noumea.mairie.ptg.reporting;

import java.io.IOException;

import com.lowagie.text.DocumentException;

public interface IReporting {

	void getFichePointageHebdoReporting() throws DocumentException, IOException;
}
