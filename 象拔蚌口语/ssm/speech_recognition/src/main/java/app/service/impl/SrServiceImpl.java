package app.service.impl;

import app.entity.Problem;
import app.entity.ProblemExample;
import app.entity.Solution;
import app.entity.SolutionExample;
import app.mapper.ProblemMapper;
import app.mapper.SolutionMapper;
import app.mapper.UserMapper;
import app.tools.HttpRequest;
import app.tools.JackJson;
import app.tools.WaveFileReader;
import com.baidu.aip.speech.AipSpeech;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.String.join;


@Service
public class SrServiceImpl {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SolutionMapper solutionMapper;
    @Autowired
    private ProblemMapper problemMapper;
    ObjectNode JSON = new JackJson().jsonTree();

    public static final String APP_ID = "15899203";
    public static final String API_KEY = "UyyOEmx2UYbIQpIZ2QcMzNPS";
    public static final String SECRET_KEY = "9yl2RzwBTc7mnFTQv8gbQj2KAk38Gzqu";

    public Problem GetRanText(int type){
        System.out.println(type);
        List<Problem> problems = problemMapper.getRanRecord(type, 1);
        if(problems.size()<=0){
            return null;
        }
        else{
            return problems.get(0);
        }
    }

    public ObjectNode recognize(String path, String oldContent, String username){
        AipSpeech client = new AipSpeech(APP_ID, API_KEY, SECRET_KEY);
        //System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        JSONObject json = client.asr(path, "wav", 16000, null);

        System.out.println(json.toString());
        if(json.has("result"))
        {
            JSONArray result  = json.getJSONArray("result");
            String content = "";
            for(int i=0;i<result.length();i++)
                content += result.getString(i);
            System.out.println("before content=" + content );
            ProblemExample example = new ProblemExample();
            ProblemExample.Criteria criteria = example.createCriteria();
            criteria.andContentEqualTo(oldContent);
            List<Problem> problems = problemMapper.selectByExample(example);
            if(problems.size()<=0)
            {
                JSON.put("type", 0);
                JSON.put("msg", "后台发生错误");
                return JSON;
            }
            System.out.println("type=" + problems.get(0).getType());
            if(problems.get(0).getType()!=3){
                System.out.println("delete end tag");
                content = content.replace(".","");
                content = content.replace("。","");
            }
            System.out.println("!!before content=" + content );

            oldContent = FixString(oldContent);
            content = FixString(content);
            String[] s1 = new String[oldContent.toCharArray().length];
            String[] s2 = new String[content.toCharArray().length];
            System.out.println("===============");
            System.out.println(oldContent);
            System.out.println("==================");
            System.out.println("oldContent=" + oldContent);
            System.out.println("content=" + content);
            ToPinyin(oldContent,s1);
            ToPinyin(content,s2);

            int[] index = new int[s1.length];
            for(int i=0;i<index.length;i++)
                index[i] = 0;
            double rate = check(s1,s2,index)/oldContent.length() * 100;

            Solution solution = new Solution();
            solution.setDate(new Date());
            solution.setPath(path);
            solution.setProblemId(problems.get(0).getProblemId());
            solution.setResult(content);
            solution.setScore(new Integer((int)rate));
            solution.setUser(username);

            solutionMapper.insert(solution);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.createObjectNode();
            ObjectNode objectNode = (ObjectNode) jsonNode;
            JsonNode jsonNodeArr = mapper.createArrayNode();
            for (Integer i : index){
                ((ArrayNode) jsonNodeArr).add(i);
            }
            objectNode.put("array", jsonNodeArr);

            objectNode.put("type", 1);
            objectNode.put("msg", "您获得 " + String.valueOf((int)rate) + " 分");

            return objectNode;
        }
        else
        {
            String errMsg = json.getString("err_msg");
            JSON.put("type", 0);
            JSON.put("msg", errMsg);
            return JSON;
        }
    }


    public ObjectNode recognize1(String path,String oldContent,String username) //识锟斤拷
    {

        WaveFileReader wav = new WaveFileReader(path);
        int[][] data = wav.getData();
        String[] arr = new String[data[0].length];
        for(int i=0;i<data[0].length;i++)
        {
            System.out.print(data[0][i] + " ");
            arr[i] = String.valueOf(data[0][i]);
        }
        System.out.println();
        String wavs = join("&wavs=", arr);
        String s = HttpRequest.sendPost("http://127.0.0.1:20000", "token=qwertasd&fs=16000&wavs="+ wavs );
        System.out.println("result:\n" + s);
        String content = s.replace('#', ' ');

        if(!content.isEmpty() && !content.equals(""))
        {

            ProblemExample example = new ProblemExample();
            ProblemExample.Criteria criteria = example.createCriteria();
            criteria.andContentEqualTo(oldContent);
            List<Problem> problems = problemMapper.selectByExample(example);
            if(problems.size()<=0)
            {
                JSON.put("type", 0);
                JSON.put("msg", "后台发生错误");
                return JSON;
            }
            System.out.println("type=" + problems.get(0).getType());
            if(problems.get(0).getType()!=3){
                System.out.println("delete end tag");
                content = content.replace(".","");
                content = content.replace("。","");
            }
            System.out.println("!!before content=" + content );
            oldContent = FixString(oldContent);
            content = FixString(content);
            String[] s1 = new String[oldContent.toCharArray().length];
            String[] s2 = content.split(" ");
            System.out.println("oldContent=" + oldContent);
            System.out.println("content=" + content);
            ToPinyin(oldContent,s1);
            //    ToPinyin(content,s2);

            int[] index = new int[s1.length];
            for(int i=0;i<index.length;i++)
                index[i] = 0;
            double rate = check(s1,s2,index)/oldContent.length() * 100;

            Solution solution = new Solution();
            solution.setDate(new Date());
            solution.setPath(path);
            solution.setProblemId(problems.get(0).getProblemId());
            solution.setResult(content);
            solution.setScore(new Integer((int)rate));
            solution.setUser(username);

            solutionMapper.insert(solution);


            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.createObjectNode();
            ObjectNode objectNode = (ObjectNode) jsonNode;
            JsonNode jsonNodeArr = mapper.createArrayNode();
            for (Integer i : index){
                ((ArrayNode) jsonNodeArr).add(i);
            }
            objectNode.put("array", jsonNodeArr);

            objectNode.put("type", 1);
            objectNode.put("msg", "您获得 " + String.valueOf((int)rate) + " 分");

            return objectNode;
        }
        else
        {
            JSON.put("type", 0);
            JSON.put("msg", "识别错误");
            return JSON;
        }
    }

    public String FixString(String s){
        System.out.println("============没替换前=====");
        System.out.println(s);
        s = s.replace('，',',');
        s = s.replace('，','.');
        s = s.replace('。', '.');
        s = s.replace('：',',');
        s = s.replace('“',' ');
        s = s.replace('”',' ');
        s = s.replace('；',',');
        s = s.replace('、', ',');
        s = s.replace('‘',',');
        s = s.replace('？','.');
        s = s.replace('?','.');
        s = s.replace('?','.');

        System.out.println("============看这里===========");
        System.out.println(s);
        return s;
    }

    public List<Solution> GetHistoryMistake(String userName) throws JsonProcessingException {
        List<Solution> solutions =  solutionMapper.GetHistoryMistake(userName);
        List<Solution> solutionList = new ArrayList<>();
        for(Solution s : solutions)
        {
            SolutionExample solutionExample = new SolutionExample();
            SolutionExample.Criteria criteria = solutionExample.createCriteria();
            criteria.andUserEqualTo(s.getUser());
            criteria.andProblemIdEqualTo(s.getProblemId());
            criteria.andScoreGreaterThanOrEqualTo(new Integer(60));
            List<Solution> list = solutionMapper.selectByExample(solutionExample);
            if(list.size()<=0)
            {
                s.parseTime();
                solutionList.add(s);
            }
        }



        return solutionList;
    }

    public String ToPinyin(String chinese, String[] ss){
        String pinyinStr = "";
        char[] newChar = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        //defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < newChar.length; i++) {
            if (newChar[i] > 128) {
                try {
                    ss[i] = PinyinHelper.toHanyuPinyinStringArray(newChar[i], defaultFormat)[0];
                    pinyinStr += ss[i] + " ";
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            }else{
                ss[i] = newChar[i] + "";
                pinyinStr += newChar[i] + " ";
            }
        }
        return pinyinStr;
    }

    public double check(String[] aa,String[] bb,int[] index){
        double score;
        int len1 = aa.length;
        System.out.println("len1=" + len1);
        int len2 = bb.length;
        String[] a = new String[len1+1];
        String[] b = new String[len2+1];

        for(int i=len1-1;i>=0;i--)
            a[i+1] = aa[i];
        for(int i=len2-1;i>=0;i--)
            b[i+1] = bb[i];


        double[][] dp = new double[len1+1][len2+1];

        for(int i=0;i<len1;i++)
            for(int j=0;j<len2;j++)
                dp[i][j] = 0;

        for(int i=1;i<=len1;i++)
            for(int j=1;j<=len2;j++)
            {

                String s1 = a[i];
                String s2 = b[j];
                if(s1.substring(0, s1.length()-1).equals(s2.substring(0, s2.length()-1)))
                {
                    if(s1.charAt(s1.length()-1) == s2.charAt(s2.length()-1))
                        dp[i][j] = Math.max(dp[i][j] ,dp[i-1][j-1] + 1);

                    else {
                        dp[i][j] = Math.max(dp[i][j] ,dp[i-1][j-1] + 0.5);
                    }
                }
                else dp[i][j] = Math.max(dp[i-1][j],dp[i][j-1]);
            }

        int i = len1,j=len2;
        while(dp[i][j]!=0)
        {
            String s1 = a[i];
            String s2 = b[j];
            if(s1.substring(0, s1.length()-1).equals(s2.substring(0, s2.length()-1)))
            {
                if(s1.charAt(s1.length()-1) == s2.charAt(s2.length()-1)) index[i-1] = 2;
                else index[i-1] = 1;
                i--;
                j--;
            }
            else if(dp[i][j-1] > dp[i-1][j])
                j--;
            else
                i--;
        }

      /*  int[] index1 = new int[len1];
        for(i=1;i<=len1;i++)
        index1[i-1] = index[i];

        index = new int[len1];
        for(i=0;i<len1;i++)
        index[i] = index1[i];
        */
        //dp[len1][len2] = dp[len1][len2] + 0.5*Math.abs(len1-len2);
        System.out.println("dp=" + dp[len1][len2]);
        System.out.println("dp=" + Math.max(dp[len1][len2] - 0.5*Math.abs(len1-len2),0));
        return Math.max(dp[len1][len2] - 0.5*Math.abs(len1-len2),0);
    }

    public String GetFilePath(String path,String oldContent)
    {
        ProblemExample example = new ProblemExample();
        ProblemExample.Criteria criteria = example.createCriteria();
        criteria.andContentEqualTo(oldContent);
        List<Problem> problems = problemMapper.selectByExample(example);
        String url = "";
        if(problems.size()==1)
        {
            int type = (int)problems.get(0).getType();
            if(type==1)
                url = "http://appcdn.fanyi.baidu.com/zhdict/mp3/"+ GetPinyin(problems.get(0).getContent()) +".mp3";
            else if(type==2)
                url = "https://ss0.baidu.com/6KAZsjip0QIZ8tyhnq/text2audio?tex=" + problems.get(0).getContent() +"&cuid=dict&lan=ZH&ctp=1&pdt=30&vol=9&spd=4";
            else
                url = "https://www.rustycn.com/voice/wav/" + problems.get(0).getProblemId().toString() + ".wav";

            /*int id = (int)problems.get(0).getProblemId();
            System.out.println("id=" + id);
            path = path + "wav\\" + String.valueOf(id) + ".mp3";
            System.out.println("path=" + path);
            File file = new File(path);
            if(file.exists())
            	return path;
            }*/
            return url;
        }
        return null;
    }
    public String GetPinyin(String chinese){
        String pinyinStr = "";
        char[] newChar = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        for (int i = 0; i < newChar.length; i++) {
            if (newChar[i] > 128) {
                try {
                    pinyinStr += PinyinHelper.toHanyuPinyinStringArray(newChar[i], defaultFormat)[0];
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            }else{
                pinyinStr += newChar[i];
            }
        }
        return pinyinStr;
    }

}
