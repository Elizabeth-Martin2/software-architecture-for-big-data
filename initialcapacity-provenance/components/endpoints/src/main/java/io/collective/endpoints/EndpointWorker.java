package io.collective.endpoints;

import io.collective.articles.ArticleDataGateway;
import io.collective.restsupport.RestTemplate; // compiler can't resolve >_<
import io.collective.workflow.Worker; // compiler can't resolve >_<
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

// Added imports & packages
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.collective.rss.RSS;

public class EndpointWorker implements Worker<EndpointTask> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final RestTemplate template;
    private final ArticleDataGateway gateway;

    public EndpointWorker(RestTemplate template, ArticleDataGateway gateway) {
        this.template = template;
        this.gateway = gateway;
    }

    @NotNull
    @Override
    public String getName() {
        return "ready";
    }

    @Override
    public void execute(EndpointTask task) throws IOException {
        String response = template.get(task.getEndpoint(), task.getAccept());
        gateway.clear();
       // todo - map rss results to an article infos collection and save articles infos to the article gateway
        RSS rss_results = new XmlMapper().readValue(response, RSS.class);
        rss_results.getChannel().getItem().forEach(item -> gateway.save(item.getTitle()));
    }
}