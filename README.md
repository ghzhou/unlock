a simple android app, target for android 2.3.

for security purpose, the keystore.jks and config.xml is not in repository.
in order to make it work, you need to provide additional 2 files.
app/src/main/res/raw/keystore.jks
app/src/main/res/values/config.xml

the keystore can be generated use key store explorer, kse-540.zip.

sample of a config.xml

<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="targetIpAddress">...</string>
    <integer name="targetPort">...</integer>
    <integer name="listeningPort">...</integer>
    <string name="key">...</string>
    <string name="keyStorePassword">...</string>
</resources>
