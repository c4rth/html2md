package org.carth.html2md.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Setter
@Getter
public class CommandLineArgs {
    private String page;
    private boolean debug;
}
