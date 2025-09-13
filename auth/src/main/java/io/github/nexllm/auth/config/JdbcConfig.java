package io.github.nexllm.auth.config;

import io.github.nexllm.auth.config.jdbc.JsonbReadingConverter;
import io.github.nexllm.auth.config.jdbc.JsonbWritingConverter;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;

@Configuration
public class JdbcConfig extends AbstractJdbcConfiguration {

    @Override
    protected List<?> userConverters() {
        return List.of(
            new JsonbReadingConverter(),
            new JsonbWritingConverter()
        );
    }
}
