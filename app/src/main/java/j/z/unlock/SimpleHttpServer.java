package j.z.unlock;

import fi.iki.elonen.NanoHTTPD;

class SimpleHttpServer extends NanoHTTPD {

    HttpRequestHandler handler;

    public SimpleHttpServer(int port, HttpRequestHandler handler) {
        super(port);
        this.handler = handler;
    }

    @Override
    public Response serve(IHTTPSession session) {
        if (null == handler) {
            return newFixedLengthResponse(Response.Status.NOT_IMPLEMENTED, "text/plain", "not implemented");
        }
        return handler.handle(session);
    }
}
