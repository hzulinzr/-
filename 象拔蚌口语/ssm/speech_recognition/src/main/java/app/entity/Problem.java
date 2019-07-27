package app.entity;

public class Problem {
    private Integer problemId;
    private String content;
    private Integer type;
    private String explanation;
    private String pinyin;

    public Problem() {
    }

    public Problem(Integer problemId, String content, Integer type, String explanation, String pinyin) {
        this.problemId = problemId;
        this.content = content;
        this.type = type;
        this.explanation = explanation;
        this.pinyin = pinyin;
    }

    public Integer getProblemId() {
        return problemId;
    }

    public void setProblemId(Integer problemId) {
        this.problemId = problemId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation == null ? null : explanation.trim();
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin == null ? null : pinyin.trim();
    }
}
