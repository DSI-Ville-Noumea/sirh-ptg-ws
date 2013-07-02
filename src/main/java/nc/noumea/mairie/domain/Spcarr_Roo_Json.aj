// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package nc.noumea.mairie.domain;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import nc.noumea.mairie.domain.Spcarr;

privileged aspect Spcarr_Roo_Json {
    
    public String Spcarr.toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }
    
    public String Spcarr.toJson(String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(this);
    }
    
    public static Spcarr Spcarr.fromJsonToSpcarr(String json) {
        return new JSONDeserializer<Spcarr>().use(null, Spcarr.class).deserialize(json);
    }
    
    public static String Spcarr.toJsonArray(Collection<Spcarr> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }
    
    public static String Spcarr.toJsonArray(Collection<Spcarr> collection, String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(collection);
    }
    
    public static Collection<Spcarr> Spcarr.fromJsonArrayToSpcarrs(String json) {
        return new JSONDeserializer<List<Spcarr>>().use(null, ArrayList.class).use("values", Spcarr.class).deserialize(json);
    }
    
}
