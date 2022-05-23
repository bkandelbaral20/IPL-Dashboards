package io.javabrains.ipldashboard.repository;

import io.javabrains.ipldashboard.model.Match;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;



public interface MatchRepository extends CrudRepository<Match, Long> {
// Telling JPA, get me list of matches where team1= teamName1 or team2= teamName2 and order them by descending(latest matches)page by page
    List<Match> getByTeam1OrTeam2OrderByDateDesc(String teamName1, String teamName2, Pageable pageable); //pageable = limitation how many matches, you want to see

    default List<Match> findLatestMatchesByTeam(String teamName, int count){
     return getByTeam1OrTeam2OrderByDateDesc(teamName, teamName,PageRequest.of(0,count));
    }
}
