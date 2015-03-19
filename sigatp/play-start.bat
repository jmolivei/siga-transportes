echo "Iniciando Servidor Play"
mvn play:clean play:initialize play:dependencies play:run -Dplay.serverJvmArgs="-Dsiga.properties.file=C:/desenvolvimento/jboss-eap-6.2.3/standalone/properties/siga.properties -Xmx1024m"
echo "Iniciado"
timeout 3
