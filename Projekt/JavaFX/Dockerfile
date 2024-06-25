# Verwende OpenJDK 17 mit Alpine Linux als Basisimage
FROM openjdk:17.0.2-alpine

# Setze das Arbeitsverzeichnis im Container
WORKDIR /app

# Kopiere das JAR-File ins Arbeitsverzeichnis
COPY target/JavaFX-1.0-SNAPSHOT.jar /app/JavaFX-1.0-SNAPSHOT.jar

# Kopiere die JavaFX-Bibliothek ins Image
COPY /path/to/openjfx-17.0.2 /opt/javafx

# Installiere erforderliche Pakete
RUN apk --no-cache add \
    libstdc++ \
    libx11 \
    libxext \
    libxrender \
    libxtst \
    libxi \
    libxcursor \
    wget \
    unzip \
    && rm -rf /var/cache/apk/*

# Setze die Umgebungsvariablen für JavaFX
ENV PATH_TO_FX=/opt/javafx/lib

# Exponiere den Port, auf dem die Anwendung läuft (angenommen es ist 8080)
EXPOSE 8080

# Befehl zum Starten der Anwendung
CMD ["java", "--module-path", "/opt/javafx/lib", "--add-modules", "javafx.controls,javafx.fxml", "-jar", "/app/JavaFX-1.0-SNAPSHOT.jar"]
