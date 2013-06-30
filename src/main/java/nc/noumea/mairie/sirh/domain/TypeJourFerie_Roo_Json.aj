// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package nc.noumea.mairie.sirh.domain;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import nc.noumea.mairie.sirh.domain.TypeJourFerie;

privileged aspect TypeJourFerie_Roo_Json {
    
    public String TypeJourFerie.toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }
    
    public static TypeJourFerie TypeJourFerie.fromJsonToTypeJourFerie(String json) {
        return new JSONDeserializer<TypeJourFerie>().use(null, TypeJourFerie.class).deserialize(json);
    }
    
    public static String TypeJourFerie.toJsonArray(Collection<TypeJourFerie> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }
    
    public static Collection<TypeJourFerie> TypeJourFerie.fromJsonArrayToTypeJourFeries(String json) {
        return new JSONDeserializer<List<TypeJourFerie>>().use(null, ArrayList.class).use("values", TypeJourFerie.class).deserialize(json);
    }
    
}
