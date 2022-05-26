package io.javabrains.ipldashboard.controller;

import io.javabrains.ipldashboard.model.Team;
import io.javabrains.ipldashboard.repository.MatchRepository;
import io.javabrains.ipldashboard.repository.TeamRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;




@RestController
@CrossOrigin
public class TeamController { //we are making this controller, to make a url based page to display all its teams informations
    
  private TeamRepository teamRepository;
  private MatchRepository matchRepository;

  public TeamController(TeamRepository teamRepository,MatchRepository matchRepository) {
    this.teamRepository = teamRepository;
    this.matchRepository = matchRepository;

  }
 
    @GetMapping("/team/{teamName}") //this is the map, to get team info
    public Team getTeam(@PathVariable String teamName) {
       Team team = this.teamRepository.findByTeamName(teamName);
       team.setMatches(matchRepository.findLatestMatchesByTeam(teamName, 4));
       return team;
    }

    

    

}
