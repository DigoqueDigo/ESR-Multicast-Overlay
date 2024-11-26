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
java -cp ESR-Projeto-1.0-jar-with-dependencies.jar bootstrapper.Bootstrapper <config_file>
java -cp ESR-Projeto-1.0-jar-with-dependencies.jar client.Client <name> <bootstrapper_ip>
java -cp ESR-Projeto-1.0-jar-with-dependencies.jar node.Node <name> <bootstrapper_ip>
java -cp ESR-Projeto-1.0-jar-with-dependencies.jar server.Server <name> <video_folder> <bootstrapper_ip>
```