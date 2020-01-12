package input;

import cube.OperationList;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Launcher;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import three.min2phase.Min2PhaseSolver;
import two.TableSolver;

import java.nio.file.Paths;

public class Controller extends AbstractVerticle {
TableSolver miniSolver = new TableSolver();
Min2PhaseSolver rubikSolver = new Min2PhaseSolver();
final String TEXT = "text/plain;charset=UTF-8";

@Override
public void start() {
    Router r = Router.router(vertx);
    r.route("/haha").handler(ctx -> {
        HttpServerRequest req = ctx.request();
        int one = Integer.parseInt(req.getParam("one"));
        int two = Integer.parseInt(req.getParam("two"));
        ctx.response().end(new JsonObject().put("one", one).put("two", two).put("sum", one + two).toString());
    });
    r.route("/debug").handler(ctx -> {
        ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, TEXT).end(Paths.get(".").toAbsolutePath().toString());
    });
    r.route("/api/solve").handler(ctx -> {
        HttpServerRequest req = ctx.request();
        int N = Integer.parseInt(req.getParam("n"));
        String state = req.getParam("q");
        String ans = "";
        if (N == 2) {
            ans = miniSolver.solve(state);
            ans = new OperationList(ans).toFormatString();
        } else if (N == 3) {
            ans = rubikSolver.solve(state);
            ans = new OperationList(ans).toFormatString();
        } else {
            throw new RuntimeException("error N");
        }
        ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, TEXT).end(ans);
    });
    var staticHandler = StaticHandler.create();
    staticHandler.setAlwaysAsyncFS(true);
    staticHandler.setCachingEnabled(false);
    r.route("/*").handler(staticHandler);
    int port = 9080;
    vertx.createHttpServer().requestHandler(r).listen(port, "0.0.0.0", (server) -> {
        System.out.println("listening at http://localhost:" + port + "/haha?one=1&two=2");
    });
}

public static void main(String[] args) {
    Launcher.main(new String[]{
            "run",
            "input.Controller",
            "--redeploy=**/*",
            "--launcher-class=io.vertx.core.Launcher"});
}
}