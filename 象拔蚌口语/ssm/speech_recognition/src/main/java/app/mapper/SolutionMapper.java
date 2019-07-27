package app.mapper;

import app.entity.Solution;
import app.entity.SolutionExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SolutionMapper {
    int countByExample(SolutionExample example);

    int deleteByExample(SolutionExample example);

    int deleteByPrimaryKey(Integer solutionId);

    int insert(Solution record);

    int insertSelective(Solution record);

    List<Solution> selectByExampleWithBLOBs(SolutionExample example);

    List<Solution> selectByExample(SolutionExample example);

    Solution selectByPrimaryKey(Integer solutionId);

    int updateByExampleSelective(@Param("record") Solution record, @Param("example") SolutionExample example);

    int updateByExampleWithBLOBs(@Param("record") Solution record, @Param("example") SolutionExample example);

    int updateByExample(@Param("record") Solution record, @Param("example") SolutionExample example);

    int updateByPrimaryKeySelective(Solution record);

    int updateByPrimaryKeyWithBLOBs(Solution record);

    int updateByPrimaryKey(Solution record);

    List<Solution> GetHistoryMistake(String username);
}
