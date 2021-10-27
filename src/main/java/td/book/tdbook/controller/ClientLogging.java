package td.book.tdbook.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "${tdbook.fe.url}")
@RequestMapping("/client/logs")
public class ClientLogging {

    public static final Logger LOGGER = LoggerFactory.getLogger(ClientLogging.class);

    @PostMapping
    public void receiveLog(@RequestBody String log) {
        LOGGER.info(log);
    }

}
