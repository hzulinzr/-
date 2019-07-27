package app.tools;


import app.entity.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


import java.io.IOException;
import java.util.Iterator;

public class JackJson {

    public static Object jsonToObj(Object obj, String jsonStr) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return obj = mapper.readValue(jsonStr, obj.getClass());
    }
    public static String objToJson(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }

    //对json对象进行增删,有json数组的
    public static ObjectNode changeObj(Object obj, String[] pinyinArr, String key){
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.valueToTree(obj);
        ObjectNode objectNode = (ObjectNode) jsonNode;
        objectNode.remove(key);
        JsonNode jsonNodeArr = mapper.createArrayNode();
        for (String pinyin : pinyinArr){
            ((ArrayNode) jsonNodeArr).add(pinyin);
        }
        objectNode.put("pinyin", jsonNodeArr);
        return objectNode;
    }

    //jsonNodeTree,增删
    public ObjectNode jsonTree(){
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.createObjectNode();
        ObjectNode objectNode = (ObjectNode) jsonNode;
        return objectNode;
    }

    public static void main(String[] args){
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.createObjectNode();
        ObjectNode objectNode = (ObjectNode) jsonNode;
        ArrayNode links = mapper.createArrayNode();
        links.add("1").add("2").add("3");
        objectNode.put("links", links);
        System.out.println(objectNode);

    }

}
