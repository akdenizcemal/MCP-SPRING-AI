package com.mcp.demo;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpConfig {

    @Bean
    ToolCallbackProvider taskTools(TaskService taskService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(taskService)
                .build();
    }
}
