# Streaming-Systems

Für das Praktikum Streaming Systems wurden 10 Aufgaben bearbeitet, die sich in zwei Anwendungen teilen lassen.

ür die Erstellung der ersten Anwendung liegt der Fokus auf der Entwicklung einer Event Sourcing-Anwendung. Ziel ist es, eine robuste und skalierbare Plattform zu erstellen, die Positions- und Zustandsänderungen von Objekten effizient verwalten kann. Die Anwendung soll Objekte, die das 'MovingItem'-Interface implementieren, durch verschiedene Befehle erstellen, vernichten, die Position ändern und den Wert aktualisieren können. Hierbei wird auf Event Sourcing-Prinzipien zurückgegriffen, bei denen Ereignisse als fundamentale Elemente des Systems gelten. Es wird ein Domänenmodell entworfen, das aus Events wie 'MovingItemCreatedEvent' und 'MovingItemValueChangedEvent' besteht, um die Anwendung zu führen und zu validieren.

Die Anwendung soll eine klare Trennung zwischen der Write Side, die Befehle verarbeitet und Ereignisse erzeugt, und der Read Side, die Query-Schnittstellen bereitstellt, gewährleisten. Das Event Store übernimmt die dauerhafte Speicherung von Ereignissen.

In der zweiten Anwendung, steht die Entwicklung eines Testdatengenerators im Kontext der Verkehrsüberwachung im Fokus. Dieser Generator erstellt Datensätze, die verschiedene Messwerte von Sensoren zu unterschiedlichen Zeitpunkten simulieren. Die Konfigurierbarkeit des Generators ermöglicht es, die Anzahl der Sensoren, die Anzahl der Messwerte, die Taktung und weitere Parameter anzupassen.

Ziel ist es, eine Apache Kafka-basierte Plattform zu entwickeln, die diese Datensätze in einen Topic mit einer Partition einstellt. Eine darauf aufbauende Anwendung wird implementiert, um die Durchschnittsgeschwindigkeit für jeden Sensor in zeitlichen Fenstern zu ermitteln. Diese Berechnungen ermöglichen die Analyse des zeitlichen Verlaufs der Durchschnittsgeschwindigkeit an einzelnen Messstellen sowie die Ermittlung von Durchschnittsgeschwindigkeiten auf Streckenabschnitten. Die Integration von Datenverarbeitungstechnologien wie Apache Beam und Complex Event Processing (CEP) unterstützt dabei eine umfassende Analyse der generierten Datenströme.