package j.z.unlock;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.KeyManagerFactory;

import fi.iki.elonen.NanoHTTPD;

public class WssService extends Service {

    private static Random rand = new Random(Integer.MAX_VALUE);
    private static SimpleHttpServer server = null;
    private char[] keyStorePassword;
    private String TAG = WssService.class.getName();
    private  int targetPort;

    public WssService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startid) {
        keyStorePassword = getResources().getString(R.string.keyStorePassword).toCharArray();
        targetPort = getResources().getInteger(R.integer.targetPort);
        startServer();
    }

    @Override
    public void onDestroy() {
        stopServer();
    }

    private void stopServer() {
        if (server == null) {
            return;
        }
        server.stop();
        server = null;
    }

    private void startServer() {
        if (server != null) {
            return;
        }
        try {
            int listeningPort = getResources().getInteger(R.integer.listeningPort);
            server = new SimpleHttpServer(listeningPort, new MyHttpRequestHandler());
            final KeyStore keyStore = getKeyStore();
            final KeyManagerFactory keyManagerFactory = getKeyManagerFactory(keyStore);
            server.makeSecure(NanoHTTPD.makeSSLSocketFactory(keyStore, keyManagerFactory), null);
            server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        } catch (IOException | CertificateException | NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException e) {
            Log.e(TAG, e.toString());
            stopServer();
            server = null;
        }
    }

    private KeyStore getKeyStore() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        InputStream keystoreStream = getResources().openRawResource(R.raw.keystore);
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(keystoreStream, keyStorePassword);
        return keystore;
    }

    private KeyManagerFactory getKeyManagerFactory(KeyStore store) throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(store, keyStorePassword);
        return keyManagerFactory;
    }

    private static byte[] intToFourBytes(int value) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) value;
            value = value >> 8;
        }
        return b;
    }

    private void unlock() {
        try {
            DatagramSocket socket = new DatagramSocket();
            byte[] buffer = new byte[76];
            int id = rand.nextInt();
            byte[] idBuf = intToFourBytes(id);
            System.arraycopy(idBuf, 0, buffer, 0, 4);
            System.arraycopy(intToFourBytes(76), 0, buffer, 4, 4);//size is 76
            System.arraycopy(intToFourBytes(0), 0, buffer, 8, 4);//packet type is 0
            System.arraycopy(intToFourBytes(4), 0, buffer, 12, 4);//cmd type is 4
            byte[] callID = getInfoFromTaiChuan("RoomID").getBytes("GB2312");
            System.arraycopy(callID, 0, buffer, 16, callID.length);
            byte[] callName = getInfoFromTaiChuan("RoomName").getBytes("GB2312");
            System.arraycopy(callName, 0, buffer, 32, callName.length);
            System.arraycopy(intToFourBytes(3), 0, buffer, 64, 4);//callType = 3;
            buffer[68] = 0; //video code type , h264 or Mpeg
            buffer[72] = 0;//video width
            buffer[74] = 0;//video height
            String targetIpAddress = getResources().getString(R.string.targetIpAddress);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length,
                    new InetSocketAddress(targetIpAddress, targetPort));
            socket.send(packet);
            socket.close();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }

    void permitElevator() {
        try {
            DatagramSocket socket = new DatagramSocket();
            byte[] buffer = new byte[52];
            int id = rand.nextInt();
            byte[] idBuf = intToFourBytes(id);
            System.arraycopy(idBuf, 0, buffer, 0, 4);
            System.arraycopy(intToFourBytes(52), 0, buffer, 4, 4);//size is 52
            System.arraycopy(intToFourBytes(519), 0, buffer, 8, 4);//packet type 519
            System.arraycopy(intToFourBytes(512), 0, buffer, 12, 4);//cmd type 512

            byte[] QH = getInfoFromTaiChuan("QH").getBytes();
            System.arraycopy(QH, 0, buffer, 16, QH.length);//phase
            byte[] DH = getInfoFromTaiChuan("DH").getBytes();
            System.arraycopy(DH, 0, buffer, 20, DH.length);// build
            byte[] DYH = getInfoFromTaiChuan("DYH").getBytes();
            System.arraycopy(DYH, 0, buffer, 24, DYH.length);// unit
            byte[] CH = getInfoFromTaiChuan("CH").getBytes();
            System.arraycopy(CH, 0, buffer, 28, CH.length);// floor
            byte[] FH = getInfoFromTaiChuan("FH").getBytes();
            System.arraycopy(FH, 0, buffer, 32, FH.length);//room code

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, new InetSocketAddress("172.16.240.2", targetPort));
            socket.send(packet);
            socket.close();

        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }

    private byte[] callElevator() {
        byte[] buffer = new byte[21];
        int id = rand.nextInt();
        byte[] idBuf = intToFourBytes(id);
        System.arraycopy(idBuf, 0, buffer, 0, 4);
        System.arraycopy(intToFourBytes(21), 0, buffer, 4, 4);//size is 21
        System.arraycopy(intToFourBytes(519), 0, buffer, 8, 4);//packet type 519
        buffer[12] = 58;//cmd type 58
        buffer[14] = (byte) Integer.parseInt(getInfoFromTaiChuan("DYH"));//unit code
        buffer[15] = 17; //func
        buffer[16] = (byte) Integer.parseInt(getInfoFromTaiChuan("CH")); //start floor
        //buffer[17] = 0; //end floor
        return buffer;
    }


    private String getInfoFromTaiChuan(String key) {
        Uri uri = Uri.parse("content://com.taichuan.data.config.ini/get");
        Cursor localCursor = getContentResolver().query(uri, null, key, null, "");
        if (localCursor == null) {
            return "";
        }
        String result = "";
        if (localCursor.moveToNext()) {
            result = localCursor.getString(localCursor.getColumnIndex(key));
        }
        localCursor.close();
        return result;
    }

    private class MyHttpRequestHandler implements HttpRequestHandler {
        @Override
        public NanoHTTPD.Response handle(NanoHTTPD.IHTTPSession session) {
            String msg = "<html><body><h1>Hello server</h1>\n";
            Map<String, String> parms = session.getParms();
            String user = parms.get("username");
            if (user == null) {
                msg += "<form action='?' method='get'>\n  <p>Your name: <input type='text' name='username'></p>\n" + "</form>\n";
            } else {
                String key = getResources().getString(R.string.key);
                if (user.equals(key)) {
                    unlock();
                }
                msg += "<p>Hello, " + user + "!</p>";
            }
            return NanoHTTPD.newFixedLengthResponse(msg + "</body></html>\n");
        }
    }
}

