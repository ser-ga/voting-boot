package ru.topjava.voting.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.topjava.voting.util.VoteTime;

@Configuration
public class VoteTimeConfig {

    @Value("${voting.time.hour}")
    private Integer hour;

    @Value("${voting.time.minute}")
    private Integer minute;

    @Bean
    public VoteTime voteTime() {
        return new VoteTime(hour, minute);
    }

}
