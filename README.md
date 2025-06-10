# MultiFork

A real-time video streaming platform using application-level multicast. It is designed to handle various types of network nodes such as servers, clients, regular nodes, and edge nodes. The system is built using Java and supports TCP and UDP communication, dynamic provider selection, encryption, and fault-tolerant streaming.

![demo](https://github.com/user-attachments/assets/c6c0b144-a75d-4273-a8de-0eb95cc03c14)

---

## ✨ Features

- Real-time video streaming using application-level multicast.
- Dynamic node addition and removal.
- Automatic provider selection based on latency.
- TCP-encrypted communication between nodes.
- UDP-based communication for video delivery to clients.
- Fault-tolerant with simple and complex node failure handling.

---

## ⚠️ Limitations

- Clients cannot currently switch edge nodes mid-stream.
- Automatic edge node selection is not implemented (manual selection only).
- No graphical user interface.

---

## 📦 Requirements

- **FFmpeg**
- **Java 11** or higher
- **Maven** (version 3.8+ recommended)

---

## 🛠️ Build

To compile and package the entire project, including dependencies into a single executable JAR file:

```
mvn clean compile assembly:single
```

---

## 🚀 Run

All components require a configuration JSON file for the bootstrapper. Ensure it is properly defined before launching the components.

---

### 🧭 Bootstrapper

```
java -cp ESR-Projeto-1.0-jar-with-dependencies.jar bootstrapper.Bootstrapper <config_file>
```

---

### 🖥️ Server

```
java -cp ESR-Projeto-1.0-jar-with-dependencies.jar server.Server <name> <video_folder> <bootstrapper_ip>
```

- **name:** Server's identifier
- **video_folder:** Path to the folder containing video files
- **bootstrapper_ip:** IP address of the bootstrapper

---

###	🌐 Node

```
java -cp ESR-Projeto-1.0-jar-with-dependencies.jar node.Node <name> <bootstrapper_ip>
```

- **name:** Node's identifier
- **bootstrapper_ip:** IP address of the bootstrapper

---

### 👤 Client

```
java -cp ESR-Projeto-1.0-jar-with-dependencies.jar client.Client <name> <bootstrapper_ip>
```
- **name:** Client's identifier
- **bootstrapper_ip:** IP address of the bootstrapper
