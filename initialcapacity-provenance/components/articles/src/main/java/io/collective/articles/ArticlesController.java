package io.collective.articles;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.collective.restsupport.BasicHandler; // compiler can't resolve >_<
import org.eclipse.jetty.server.Request;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

// Added import
import java.util.stream.Collectors;

public class ArticlesController extends BasicHandler {
    private final ArticleDataGateway gateway;

    public ArticlesController(ObjectMapper mapper, ArticleDataGateway gateway) {
        super(mapper);
        this.gateway = gateway;
    }

    @Override
    public void handle(String target, Request request, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        get("/articles", List.of("application/json", "text/html"), request, servletResponse, () -> {
            // todo - query the articles gateway for *all* articles, map record to infos, and send back a collection of article infos
            List<ArticleRecord> record_list = gateway.findAll();
            List<ArticleInfo> info_list = record_list.stream().map(record -> new ArticleInfo(record.getId(), record.getTitle())).collect(Collectors.toList());
            writeJsonBody(servletResponse, info_list);
        });

        get("/available", List.of("application/json"), request, servletResponse, () -> {
            // todo - query the articles gateway for *available* articles, map records to infos, and send back a collection of article infos
            List<ArticleRecord> record_list = gateway.findAvailable();
            List<ArticleInfo> info_list = record_list.stream().map(record -> new ArticleInfo(record.getId(), record.getTitle())).collect(Collectors.toList());
            writeJsonBody(servletResponse, info_list);
        });
    }
}
