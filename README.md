# Multicast Overlay

## Comandos Úteis

Compilar o código fonte
```
mvn compile
```

Eliminar todos os artefactos
```
mvn clean
```

Empacotar o código compilado num arquivo `JAR`
```
mvn clean compile assembly:single
```

Executar classes do programa
```
mvn exec:java -Dexec.mainClass="bootstrapper.Bootstrapper" -Dexec.args="<config.json>"
mvn exec:java -Dexec.mainClass="server.Server" -Dexec.args="<nodename> <bootstrapper_IP>"
mvn exec:java -Dexec.mainClass="client.Client" -Dexec.args="<nodename> <bootstrapper_IP>"
```