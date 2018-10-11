package j.z.unlock;

import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by jiezhou on 10/11/2018.
 */

public class StaticContentHandler implements HttpRequestHandler {

    private AssetManager assets;

    public StaticContentHandler(AssetManager assets) {
        this.assets = assets;
    }

    @Override
    public NanoHTTPD.Response handle(NanoHTTPD.IHTTPSession session) {
        String uri = session.getUri();
        Pattern p = Pattern.compile(".*static\\/(\\w+\\.\\w+)$");
        Matcher matcher = p.matcher(uri);
        if (!matcher.matches()){
            return null;
        }
        String fileName = matcher.group(1);

        try {
            InputStream stream = assets.open(fileName, AssetManager.ACCESS_STREAMING);
            return NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.OK, "text/html", stream);
        } catch (IOException e) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND,"text/plain", "Not found");
        }
    }
}
