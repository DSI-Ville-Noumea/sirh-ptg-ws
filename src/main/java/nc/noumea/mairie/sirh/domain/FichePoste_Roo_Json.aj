// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package nc.noumea.mairie.sirh.domain;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import nc.noumea.mairie.sirh.domain.FichePoste;

privileged aspect FichePoste_Roo_Json {
    
    public String FichePoste.toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }
    
    public String FichePoste.toJson(String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(this);
    }
    
    public static FichePoste FichePoste.fromJsonToFichePoste(String json) {
        return new JSONDeserializer<FichePoste>().use(null, FichePoste.class).deserialize(json);
    }
    
    public static String FichePoste.toJsonArray(Collection<FichePoste> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }
    
    public static String FichePoste.toJsonArray(Collection<FichePoste> collection, String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(collection);
    }
    
    public static Collection<FichePoste> FichePoste.fromJsonArrayToFichePostes(String json) {
        return new JSONDeserializer<List<FichePoste>>().use(null, ArrayList.class).use("values", FichePoste.class).deserialize(json);
    }
    
}
