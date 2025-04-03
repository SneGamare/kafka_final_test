package com.example.kafka.config;

import lombok.Builder;
import lombok.Data;
import org.apache.kafka.common.serialization.Deserializer;

@Data
@Builder
public class ConsumerConfiguration<T> {
    private String bootstrapServers;
    private String groupId;
    private String topic;
    private String securityProtocol;
    private int maxPollRecords;
    private Class<? extends Deserializer<?>> valueDeserializer;
    private int inMemoryPartitions;
    private String processorThreadPoolName;
    private DeferredCommitConfig deferredCommitConfig;
    private DlqConfig dlqConfig;
    private MessageProcessor<T> processor;
    private FailureHandler<T> failureHandler;

    @Data
    @Builder
    public static class DeferredCommitConfig {
        private int maxDeferredCommits;
        private long commitIntervalMillis;
        private int commitBatchSize;
    }

    @Data
    @Builder
    public static class DlqConfig {
        private String dlqTopic;
        private int retries;
        private long retryBackoffMillis;
    }

    public interface MessageProcessor<T> {
        void process(reactor.kafka.receiver.ReceiverRecord<String, T> message);
        String partitionKey(T value);
    }

    public interface FailureHandler<T> {
        void handle(String bootstrapServers, DlqConfig dlqConfig, String securityProtocol,
                   reactor.kafka.receiver.ReceiverRecord<String, T> message);
    }
} 