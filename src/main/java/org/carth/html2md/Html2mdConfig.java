package org.carth.html2md;

import org.carth.html2md.copydown.CopyDown;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Html2mdConfig {

    @Bean
    CopyDown copyDown() {
        return new CopyDown();
    }
}
