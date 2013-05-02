// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package nc.noumea.mairie.sirh.domain;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import nc.noumea.mairie.sirh.domain.Siserv;

privileged aspect Siserv_Roo_Json {
    
    public String Siserv.toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }
    
    public static Siserv Siserv.fromJsonToSiserv(String json) {
        return new JSONDeserializer<Siserv>().use(null, Siserv.class).deserialize(json);
    }
    
    public static String Siserv.toJsonArray(Collection<Siserv> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }
    
    public static Collection<Siserv> Siserv.fromJsonArrayToSiservs(String json) {
        return new JSONDeserializer<List<Siserv>>().use(null, ArrayList.class).use("values", Siserv.class).deserialize(json);
    }
    
}
