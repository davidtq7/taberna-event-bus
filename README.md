# La Taberna del Bus de Eventos

Proyecto de ejemplo en Java 17 para aplicar el patrón Publisher/Subscriber. La taberna recibe un pedido y el `EventBus` avisa a los componentes interesados. La barra y la cocina trabajan al mismo tiempo, mientras la banda publica canciones. Nadie llama directamente a otro componente.

## Requisitos

- JDK 17 o posterior (`java -version` y `javac -version`).
- No se usan bibliotecas externas para compilar o ejecutar.

## Ejecutar

En Windows, desde PowerShell dentro de la carpeta del proyecto:

```powershell
.\ejecutar.ps1
```

En Linux o macOS:

```bash
bash ejecutar.sh
```

También se puede compilar a mano con `javac --release 17 -encoding UTF-8 -d out` sobre todos los archivos de `src/main/java` y ejecutar `java -cp out taberna.App`.

La simulación dura unos 5 segundos. Primero aparece el pedido de `mesa_5`. A los 2 segundos se sirve la cerveza y a los 5 queda lista la hamburguesa. La banda toca al inicio y vuelve a publicar otra canción a los 3 segundos. El sistema de humo solo reacciona a **Through the Fire and Flames**. El registro de servicio cuenta las bebidas y los platos terminados. Las horas mostradas en consola sirven para comprobar que los trabajos se solapan. En terminales compatibles, los mensajes del panel LED aparecen en color cian.

## Probar

```powershell
.\probar.ps1
```

En Linux o macOS: `bash probar.sh`. Las pruebas comprueban suscripción y desuscripción, recepción general con `Evento.class`, ausencia de duplicados, inmutabilidad del pedido, el nuevo registro de servicio y el flujo de respuesta de barra y cocina.

## Organización

- `taberna.bus`: interfaz `Evento` y clase `EventBus`.
- `taberna.eventos`: cuatro eventos inmutables y los datos de un pedido.
- `taberna.componentes`: actores de la taberna.
- `taberna.App`: conecta los componentes y ejecuta el escenario.
- `diagramas`: archivos `.puml` y sus imágenes generadas con PlantUML.
- `Diagramas UML.pdf`: diagramas de clases, secuencia y componentes con el código PlantUML.

El bus guarda los suscriptores por tipo. `publicar` avisa también a quien se haya suscrito a una interfaz o superclase, por eso el panel escucha `Evento.class`. La estructura usa colecciones seguras para varios hilos. Si un suscriptor lanza una excepción, los demás siguen recibiendo el evento.

Para demostrar la extensión, se agregaron `SistemaDeHumo` y `RegistroDeServicio` como clases nuevas y se registraron en `App`. No fue necesario modificar `Banda`, `Barra`, `Cocina` ni `EventBus`. Para agregar otro efecto bastaría con crear un componente que se suscriba al evento que le interesa.

## Referencias

- [Introducción a Event Bus de Akamai](https://www.akamai.com/glossary/what-is-an-event-bus).
- [Video proporcionado en la consigna](https://www.youtube.com/watch?v=GdelFsukQwE).
