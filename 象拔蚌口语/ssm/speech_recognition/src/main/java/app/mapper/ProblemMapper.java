package app.mapper;

import app.entity.Problem;
import app.entity.ProblemExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProblemMapper {
    int countByExample(ProblemExample example);
    int deleteByExample(ProblemExample example);
    int deleteByPrimaryKey(Integer problemId);
    int insert(Problem record);
    int insertSelective(Problem record);
    List<Problem> selectByExample(ProblemExample example);
    Problem selectByPrimaryKey(Integer problemId);
    int updateByExampleSelective(@Param("record") Problem record, @Param("example") ProblemExample example);
    int updateByExample(@Param("record") Problem record, @Param("example") ProblemExample example);
    int updateByPrimaryKeySelective(Problem record);
    int updateByPrimaryKey(Problem record);
    List<Problem> getRanRecord (@Param("type") int type, @Param("num") int num);
}
