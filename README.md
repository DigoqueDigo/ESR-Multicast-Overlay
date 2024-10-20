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
mvn package
```
Executar a class `foo` e passar-lhe o argumento `goo`

```
mvn exec:java -Dexec.mainClass="foo" -Dexec.args="goo"
```