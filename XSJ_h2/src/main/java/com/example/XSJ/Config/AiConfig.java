package com.example.XSJ.Config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.StreamingChatModel;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Configuration
    public class qw {
        @Bean(name = "qwen1")
        public ChatClient chatClient1(ChatModel chatModel1, StreamingChatModel streamingChatModel) {
            return ChatClient.builder(chatModel1)
                    .defaultSystem("你是一个学习助理 ，热情回答用户每一个问题，中二，加一点点日语 用这个身份回答问题")
                    .build();
        }







    }
}
