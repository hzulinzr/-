package app.controller;

import app.entity.Problem;
import app.entity.Response;
import app.entity.Solution;
import app.service.impl.SrServiceImpl;
import app.service.impl.UserServiceImpl;
import app.tools.RandomString;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static app.tools.JackJson.*;

@Controller
public class UserController {
    @Resource
    private UserServiceImpl userService;
    @Resource
    private SrServiceImpl srService;

    @RequestMapping(value = "user/login", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String Login(HttpServletRequest request) throws JsonProcessingException {
        Response response = new Response();
        response.setSuccess(0);
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        if (username == null || password == null) {
            response.setMsg("参数错误！");
            return objToJson(response);
        }
        int status = userService.Login(username, password);
        if (status == 2) {
            response.setMsg("用户名不存在");
        } else if (status == 1) {
            response.setMsg("密码错误");
        } else {
            response.setMsg("登陆成功");
            response.setSuccess(1);
            request.getSession().setAttribute("username", username);
            request.getSession().setAttribute("password", password);
        }
        System.out.println(objToJson(response));
        return objToJson(response);
    }

    @RequestMapping(value = "user/GetText", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String GetText(HttpServletRequest request) throws IOException {
        Response response = new Response();
        response.setSuccess(0);
        String username = (String) request.getSession().getAttribute("username");
        if (username == null) {
            response.setMsg("参数错误!");
            return objToJson(response);
        }
        String type = request.getParameter("type");
        int t = Integer.parseInt(type);
        Problem pro = srService.GetRanText(t);
        System.out.println(pro.getPinyin());
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode obj = mapper.valueToTree(pro);
        if (pro.getType() != 3) {
            String pinyin = pro.getPinyin();
            String[] pinyinArr = pinyin.split(" ");
            obj = changeObj(pro, pinyinArr, pinyin);
            System.out.println(obj);
        }
        request.getSession().setAttribute("content", obj.get("content"));
        response.setReObj(obj);
        response.setSuccess(1);
        response.setMsg("成功获取文本");
        //System.out.println(objToJson(obj));
        return objToJson(response);
    }

    @RequestMapping(value = "load/uploadMav", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String saveMav(@RequestParam("file") CommonsMultipartFile partFile, @RequestParam("content") String oldContent, HttpServletRequest request) throws JsonProcessingException {
        try {
            String username = (String) request.getSession().getAttribute("username");
            if (username == null || oldContent == null) {
                Response response = new Response();
                response.setMsg("参数错误!");
                response.setSuccess(0);
                return objToJson(response);
            }
            String path = request.getServletContext().getRealPath("\\uploadWav") + "\\" + RandomString.getRandomString(18) + ".wav";
            File file = new File(path);
            InputStream inputStream = partFile.getInputStream();
            FileUtils.copyInputStreamToFile(inputStream, file);
            if (inputStream != null) {
                inputStream.close();
            }
            ObjectNode obj = srService.recognize(path, oldContent, username);
            Response response = new Response();
            response.setMsg("后台发生错误!");
            response.setSuccess(0);

            System.out.println(obj.toString());

            if (obj.get("type").asInt() == 0) {
                if (obj.get("msg").toString().contains("speech quality error")) {
                    response.setMsg("您的录音不清晰，请重试!");
                }
                return objToJson(response);

            }
            String vPath = srService.GetFilePath(request.getServletContext().getRealPath(""), oldContent);
            System.out.println("vPath=" + vPath);
            if (vPath == null)
                return objToJson(response);
            obj.put("path", vPath);
            System.out.println(obj.toString());
            response.setSuccess(1);
            response.setReObj(obj);
            response.setMsg("获取成功");
            System.out.println(objToJson(response));
            return objToJson(response);
        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response();
            response.setMsg("后台发生错误!");
            response.setSuccess(0);
            return objToJson(response);
        }
    }

    @RequestMapping(value = "load/uploadMav1", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String saveMav1(@RequestParam("file") CommonsMultipartFile partFile, @RequestParam("content") String oldContent, HttpServletRequest request) throws JsonProcessingException {
        try {
            String username = (String) request.getSession().getAttribute("username");
            //String oldContent = (String)request.getSession().getAttribute("content");
            if (username == null || oldContent == null) {
                Response response = new Response();
                response.setMsg("参数错误!");
                response.setSuccess(0);
                return objToJson(response);
            }


            String path = request.getServletContext().getRealPath("\\uploadWav") + "\\" + RandomString.getRandomString(18) + ".wav";
            File file = new File(path);

            InputStream inputStream = partFile.getInputStream();
            FileUtils.copyInputStreamToFile(inputStream, file);
            if (inputStream != null) {
                inputStream.close();
            }
            //	String path = request.getServletContext().getRealPath("\\uploadWav") + "\\ZYWM6LpytxGcFMC8qx.wav";
            ObjectNode obj = srService.recognize1(path, oldContent, username);

            // System.out.println(path + " " + oldContent + username);

            Response response = new Response();
            response.setMsg("后台发生错误!");
            response.setSuccess(0);

            System.out.println(obj.toString());

            if (obj.get("type").asInt() == 0)
                return objToJson(response);

            String vPath = srService.GetFilePath(request.getServletContext().getRealPath(""), oldContent);
            System.out.println("vPath=" + vPath);
            //if (vPath == null)
            //    return objToJson(response);
            obj.put("path", vPath);
            System.out.println(obj.toString());
            response.setSuccess(1);
            response.setReObj(obj);
            response.setMsg("获取成功");
            System.out.println(objToJson(response));

            return objToJson(response);


        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response();
            response.setMsg("后台发生错误!");
            response.setSuccess(0);
            return objToJson(response);
        }
    }

    @RequestMapping(value = "user/GetHistoryMistake", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String GetHistoryMistake(HttpServletRequest request) throws IOException {
        String username = (String) request.getSession().getAttribute("username");
        if (username == null) return null;
        List<Solution> solutions = srService.GetHistoryMistake(username);
        String s = objToJson(solutions);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(s);
        ArrayNode arrayNode = mapper.createArrayNode();
        for (int i = 0; i < rootNode.size(); i++) {
            if (rootNode.get(i).get("problem").get("type").asInt() != 3) {
                String pinyin = rootNode.get(i).get("problem").get("pinyin").asText();
                System.out.println("========第" + i + "个================");
                System.out.println(pinyin);
                String[] pinyinArr = pinyin.split(" ");
                for (String py : pinyinArr) {
                    arrayNode.add(py);
                }
                ((ObjectNode) rootNode.get(i).get("problem")).put("pinyin", arrayNode);
            }
        }


        return objToJson(rootNode);
    }
}
