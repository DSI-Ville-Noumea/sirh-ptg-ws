package nc.noumea.mairie.ptg.dto;

import java.io.IOException;
import java.util.Date;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.springframework.stereotype.Component;

@Component
public class JsonDateSerializer extends JsonSerializer<Date>{
	
	@Override
	public void serialize(Date date, JsonGenerator gen, SerializerProvider provider)
			throws IOException, JsonProcessingException {

		if (date == null) {
			gen.writeNull();
			return;
		}
		
		DateTime dt = new DateTime(date);
		
		DateTimeFormatter formater = new DateTimeFormatterBuilder()
			.appendLiteral("/Date(")
			.appendLiteral(String.format("%s", dt.getMillis()))
			.appendPattern("Z")
			.appendLiteral(")/")
			.toFormatter();
		
		gen.writeString(formater.print(dt));
	}
}
