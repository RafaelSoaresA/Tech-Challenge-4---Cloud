package br.com.techchallenge.avaliacao.infra.config;

import br.com.techchallenge.avaliacao.application.avaliacao.usecase.CreateAvaliacaoUseCase;
import br.com.techchallenge.avaliacao.domain.avaliacao.gateways.AvaliacaoEventPublisher;
import br.com.techchallenge.avaliacao.domain.avaliacao.gateways.AvaliacaoGateway;
import br.com.techchallenge.avaliacao.infra.avaliacao.messaging.SnsAvaliacaoEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

@Configuration
public class SnsConfig {

    @Value("${aws.profile:}")
    private String profile;

    @Bean
    public SnsClient snsClient(@Value("${aws.region}") String region) {

        AwsCredentialsProvider credentials =
                (profile != null && !profile.isBlank())
                        ? ProfileCredentialsProvider.create(profile)
                        : DefaultCredentialsProvider.create();

        return SnsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(credentials)
                .build();
    }

    @Bean
    public AvaliacaoEventPublisher avaliacaoEventPublisher(
            SnsClient snsClient,
            ObjectMapper objectMapper,
            @Value("${aws.sns.avaliacao-topic-arn}") String topicArn) {
        return new SnsAvaliacaoEventPublisher(snsClient, objectMapper, topicArn);
    }

    public CreateAvaliacaoUseCase createAvaliacaoUseCase(
            AvaliacaoGateway gateway,
            AvaliacaoEventPublisher publisher) {
        return new CreateAvaliacaoUseCase(gateway, publisher);
    }
}
