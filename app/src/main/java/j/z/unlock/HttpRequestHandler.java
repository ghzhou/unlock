package j.z.unlock;

import fi.iki.elonen.NanoHTTPD;

interface HttpRequestHandler {
    NanoHTTPD.Response handle(NanoHTTPD.IHTTPSession session);
}
