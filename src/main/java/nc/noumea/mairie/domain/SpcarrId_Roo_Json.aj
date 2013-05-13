// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package nc.noumea.mairie.domain;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import nc.noumea.mairie.domain.SpcarrId;

privileged aspect SpcarrId_Roo_Json {
    
    public String SpcarrId.toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }
    
    public static SpcarrId SpcarrId.fromJsonToSpcarrId(String json) {
        return new JSONDeserializer<SpcarrId>().use(null, SpcarrId.class).deserialize(json);
    }
    
    public static String SpcarrId.toJsonArray(Collection<SpcarrId> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }
    
    public static Collection<SpcarrId> SpcarrId.fromJsonArrayToSpcarrIds(String json) {
        return new JSONDeserializer<List<SpcarrId>>().use(null, ArrayList.class).use("values", SpcarrId.class).deserialize(json);
    }
    
}
