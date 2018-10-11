package j.z.unlock;

import java.util.ArrayList;
import java.util.List;

import fi.iki.elonen.NanoHTTPD;

class SimpleHttpServer extends NanoHTTPD {

    List<HttpRequestHandler> handlers;

    public SimpleHttpServer(int port) {
        super(port);
        this.handlers = new ArrayList<>();
    }

    public void addHandler(HttpRequestHandler handler) {
        this.handlers.add(handler);
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        for(HttpRequestHandler handler: handlers) {

            NanoHTTPD.Response response = handler.handle(session);
            if(response != null) {
                return response;
            }
        }
        return newFixedLengthResponse(Response.Status.NOT_IMPLEMENTED, "text/plain", "not implemented");
    }
}
