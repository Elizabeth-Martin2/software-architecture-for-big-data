package io.collective.start;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.collective.articles.ArticleDataGateway;
import io.collective.articles.ArticleRecord;
import io.collective.articles.ArticlesController;
import io.collective.restsupport.BasicApp; // compiler can't resolve >_<
import io.collective.restsupport.NoopController; // compiler can't resolve >_<
import org.eclipse.jetty.server.handler.HandlerList;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.TimeZone;

// Added imports
import io.collective.endpoints.EndpointDataGateway;
import io.collective.endpoints.EndpointTask;
import io.collective.endpoints.EndpointWorkFinder;
import io.collective.endpoints.EndpointWorker;
import io.collective.workflow.WorkFinder; // compiler can't resolve >_<
import io.collective.workflow.WorkScheduler; // compiler can't resolve >_<
import io.collective.workflow.Worker; // compiler can't resolve >_<
import io.collective.restsupport.RestTemplate; // compiler can't resolve >_<
import java.util.Collections;

public class App extends BasicApp {
    private static ArticleDataGateway articleDataGateway = new ArticleDataGateway(List.of(
            new ArticleRecord(10101, "Programming Languages InfoQ Trends Report - October 2019 4", true),
            new ArticleRecord(10106, "Ryan Kitchens on Learning from Incidents at Netflix, the Role of SRE, and Sociotechnical Systems", true)
    ));

    @Override
    public void start() {
        super.start();

        // todo - start the endpoint worker
        EndpointWorker worker = new EndpointWorker(new RestTemplate(), articleDataGateway);
        List<Worker<EndpointTask>> worker_list = Collections.singletonList(worker);
        
        EndpointDataGateway endpoint = new EndpointDataGateway();
        WorkFinder<EndpointTask> work_finder = new EndpointWorkFinder(endpoint);

        WorkScheduler<EndpointTask> scheduler = new WorkScheduler<>(work_finder, worker_list, 300);
        scheduler.start();
    }

    public App(int port) {
        super(port);
    }

    @NotNull
    @Override
    protected HandlerList handlerList() {
        HandlerList list = new HandlerList();
        list.addHandler(new ArticlesController(new ObjectMapper(), articleDataGateway));
        list.addHandler(new NoopController());
        return list;
    }

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        String port = System.getenv("PORT") != null ? System.getenv("PORT") : "8881";
        App app = new App(Integer.parseInt(port));
        app.start();
    }
}