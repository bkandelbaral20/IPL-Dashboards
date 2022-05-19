package io.javabrains.ipldashboard.data;

import io.javabrains.ipldashboard.model.Match;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;




@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final String[] FIELD_NAMES = new String [] {
        "id", "city", "date", "player_of_match", "venue", "neutral_venue", "team1", "team2", "toss_winner", 
        "toss_decision", "winner", "result", "result_margin", "eliminator", "method", "umpire1", "umpire2"
    };

  @Autowired
  public JobBuilderFactory jobBuilderFactory;

  @Autowired 
  public StepBuilderFactory stepBuilderFactory;

@Bean //this is to read all the data from csv file so, basically takes MatchInput
public FlatFileItemReader<MatchInput> reader() {  
  return new FlatFileItemReaderBuilder<MatchInput>()
    .name("MatchItemReader")
    .resource(new ClassPathResource("IPL Matches.csv"))
    .delimited() //track the csv file from class/resources folder
    .names(FIELD_NAMES)
    .fieldSetMapper(new BeanWrapperFieldSetMapper<MatchInput>() { //it will create a instances of MatchInput.class
    {
      setTargetType(MatchInput.class);
    }
      }).build();
}

@Bean  //this part will process the Matchinput data and convert them into Match
public MatchDataProcessor processor() {  //checkout its class to see how it works
  return new MatchDataProcessor();
}

@Bean //this is to write csv file contents into database so, this takes Match instances, our final results(final data)
public JdbcBatchItemWriter<Match> writer(DataSource dataSource) { 
  return new JdbcBatchItemWriterBuilder<Match>()
    .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>()) //we are displaying Match instances into a SQl table along with its values
    .sql("INSERT INTO match (id, city, date, player_of_match, venue, team1, team2, toss_winner, toss_decision, match_winner, result, result_margin, umpire1, umpire2)" 
    + " VALUES (:id,:city,:date,:playerOfMatch, :venue,:Team1,:Team2,:tossWinner, :tossDecision,:matchWinner,:result, :resultMargin, :umpire1, :umpire2)" )
    .dataSource(dataSource)
    //values should be same as Match class instances variables
    .build();
}

@Bean //after job is done(processed), called this notification listner. Checkout its class to see its details
public Job importUserJob(JobCompletionNotificationListener listener, Step step1) { // defined batch job and conatines 1 step
  return jobBuilderFactory.get("importUserJob")
    .incrementer(new RunIdIncrementer())
    .listener(listener)
    .flow(step1)
    .end()
    .build();
}

@Bean
public Step step1(JdbcBatchItemWriter<Match> writer) { //batch job steps are defined here, what dows this each steps do 
  return stepBuilderFactory.get("step1")
    .<MatchInput, Match> chunk(10)
    .reader(reader()) //reads using the reader    
    .processor(processor())
    .writer(writer)
    .build();
}
}


