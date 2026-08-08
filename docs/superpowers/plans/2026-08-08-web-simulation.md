# Web Simulation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Запустить существующую Java-симуляцию через локальную веб-страницу с управлением ходами и плавной анимацией перемещения животных.

**Architecture:** Новый пакет `karty.web` адаптирует существующие `GameMap` и действия к безопасному снимку состояния, не меняя игровой код. Встроенный `HttpServer` JDK обслуживает API и статические ресурсы, а браузер сопоставляет сущности по идентификатору и анимирует изменение их координат.

**Tech Stack:** Java 26, `com.sun.net.httpserver.HttpServer`, JUnit 4, HTML, CSS, vanilla JavaScript.

## Global Constraints

- Не изменять `karty.Main`, `karty.app.Simulation`, `karty.Units`, `karty.map` и `karty.actions`.
- Не добавлять сторонние зависимости или JavaScript-пакеты.
- Веб-режим запускается отдельным `karty.web.WebMain` на `http://localhost:8080`.
- Состояние игры — единственный источник истины на Java-сервере; браузер только отображает снимки и команды.
- Каждая сущность в API получает стабильный идентификатор, назначенный веб-адаптером по идентичности Java-объекта.

---

## File structure

- `src/main/java/karty/web/EntityView.java` — неизменяемая проекция сущности для API.
- `src/main/java/karty/web/SimulationState.java` — неизменяемый снимок карты и агрегированной статистики.
- `src/main/java/karty/web/WebSimulationController.java` — потокобезопасное управление картой, ходами и автопереходом.
- `src/main/java/karty/web/WebServer.java` — HTTP-маршруты, JSON-ответы и выдача ресурсов.
- `src/main/java/karty/web/WebMain.java` — отдельная точка входа для веб-режима.
- `src/main/resources/web/index.html` — семантический каркас страницы.
- `src/main/resources/web/styles.css` — адаптивная сетка и переходы сущностей.
- `src/main/resources/web/app.js` — получение снимков, управление и анимация.
- `src/test/java/karty/web/WebSimulationControllerTest.java` — проверка снимков, ходов и идентификаторов.
- `src/test/java/karty/web/WebServerTest.java` — проверка API через реальный HTTP-клиент JDK.

### Task 1: Snapshot model and simulation controller

**Files:**
- Create: `src/main/java/karty/web/EntityView.java`
- Create: `src/main/java/karty/web/SimulationState.java`
- Create: `src/main/java/karty/web/WebSimulationController.java`
- Create: `src/test/java/karty/web/WebSimulationControllerTest.java`

**Interfaces:**
- Consumes: `GameMap`, `InitAction`, `TurnAction`, `RestoreGrassAction`, `EntityPosition` from the existing simulation.
- Produces: `EntityView(long id, String type, int x, int y)` and `SimulationState(int width, int height, long turn, boolean running, int speedMillis, List<EntityView> entities, int grassCount, int herbivoreCount, int predatorCount)`.
- Produces: `WebSimulationController(int width, int height, int initialSpeedMillis)`, `SimulationState state()`, `SimulationState nextTurn()`, `SimulationState play()`, `SimulationState pause()`, `SimulationState setSpeed(int speedMillis)`, `void close()`.

- [ ] **Step 1: Write the failing controller test**

```java
package karty.web;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class WebSimulationControllerTest {
    private WebSimulationController controller;

    @After
    public void closeController() {
        if (controller != null) controller.close();
    }

    @Test
    public void createsSnapshotAndAdvancesExactlyOneTurn() {
        controller = new WebSimulationController(20, 10, 500);
        SimulationState before = controller.state();
        SimulationState after = controller.nextTurn();

        Assert.assertEquals(20, before.width());
        Assert.assertEquals(10, before.height());
        Assert.assertEquals(0, before.turn());
        Assert.assertEquals(1, after.turn());
        Assert.assertFalse(before.entities().isEmpty());
        Set<Long> ids = new HashSet<>();
        for (EntityView entity : after.entities()) ids.add(entity.id());
        Assert.assertEquals(after.entities().size(), ids.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsSpeedOutsideAnimationRange() {
        controller = new WebSimulationController(20, 10, 500);
        controller.setSpeed(10);
    }
}
```

- [ ] **Step 2: Run the new test and verify it fails because the web classes do not exist**

Run: `mvn test -Dtest=WebSimulationControllerTest`

Expected: compilation failure naming missing `WebSimulationController`, `SimulationState` and `EntityView`.

- [ ] **Step 3: Implement immutable views and the synchronized controller**

```java
public record EntityView(long id, String type, int x, int y) { }

public record SimulationState(
        int width, int height, long turn, boolean running, int speedMillis,
        List<EntityView> entities, int grassCount, int herbivoreCount, int predatorCount
) { }
```

In `WebSimulationController`, create the map and call `new InitAction(map).execute()` in the constructor. Keep an `IdentityHashMap<Entity, Long>` and assign `nextEntityId++` while projecting `map.getPositions()`; map `Grass`, `Rock`, `Tree`, `Herbivore` and `Predator` to uppercase API types. `nextTurn()` must execute `TurnAction` then `RestoreGrassAction`, increment `turn` once and return a fresh snapshot. Guard all public state-changing and snapshot methods with the same intrinsic lock. Limit speeds to 200–2,000 ms; scheduling uses a single daemon `ScheduledExecutorService`, cancels an old future before scheduling a new one, and `close()` cancels it and shuts the executor down.

- [ ] **Step 4: Run the controller tests and verify they pass**

Run: `mvn test -Dtest=WebSimulationControllerTest`

Expected: two passing tests.

- [ ] **Step 5: Commit the controller model**

```bash
git add src/main/java/karty/web/EntityView.java src/main/java/karty/web/SimulationState.java src/main/java/karty/web/WebSimulationController.java src/test/java/karty/web/WebSimulationControllerTest.java
git commit -m "feat: add web simulation controller"
```

### Task 2: HTTP server and JSON API

**Files:**
- Create: `src/main/java/karty/web/WebServer.java`
- Create: `src/main/java/karty/web/WebMain.java`
- Create: `src/test/java/karty/web/WebServerTest.java`

**Interfaces:**
- Consumes: `WebSimulationController.state()`, `.nextTurn()`, `.play()`, `.pause()`, `.setSpeed(int)`.
- Produces: `WebServer(WebSimulationController controller, int port)`, `void start()`, `int port()`, `void close()`.
- API: `GET /api/state`, `POST /api/next`, `POST /api/play`, `POST /api/pause`, `POST /api/speed` with JSON `{ "speedMillis": 500 }`.

- [ ] **Step 1: Write the failing HTTP integration test**

```java
package karty.web;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WebServerTest {
    private WebSimulationController controller;
    private WebServer server;
    private HttpClient client;

    @Before
    public void startServer() throws Exception {
        controller = new WebSimulationController(20, 10, 500);
        server = new WebServer(controller, 0);
        server.start();
        client = HttpClient.newHttpClient();
    }

    @After
    public void stopServer() {
        if (server != null) server.close();
        if (controller != null) controller.close();
    }

    @Test
    public void returnsStateAndAdvancesOnPost() throws Exception {
        URI stateUri = URI.create("http://localhost:" + server.port() + "/api/state");
        HttpResponse<String> initial = client.send(
                HttpRequest.newBuilder(stateUri).GET().build(), HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> after = client.send(
                HttpRequest.newBuilder(stateUri.resolve("/api/next")).POST(HttpRequest.BodyPublishers.noBody()).build(),
                HttpResponse.BodyHandlers.ofString());

        Assert.assertEquals(200, initial.statusCode());
        Assert.assertTrue(initial.body().contains("\"turn\":0"));
        Assert.assertEquals(200, after.statusCode());
        Assert.assertTrue(after.body().contains("\"turn\":1"));
    }
}
```

- [ ] **Step 2: Run the HTTP test and verify it fails because `WebServer` is missing**

Run: `mvn test -Dtest=WebServerTest`

Expected: compilation failure naming missing `WebServer`.

- [ ] **Step 3: Implement server routes, responses and startup**

```java
server.createContext("/api/state", exchange -> handleGet(exchange, controller::state));
server.createContext("/api/next", exchange -> handlePost(exchange, controller::nextTurn));
server.createContext("/api/play", exchange -> handlePost(exchange, controller::play));
server.createContext("/api/pause", exchange -> handlePost(exchange, controller::pause));
server.createContext("/api/speed", this::handleSpeed);
```

`WebServer` must accept `GET` only for `/api/state` and `POST` only for command routes; other methods return a JSON error with HTTP 405. Parse `speedMillis` from the small JSON body with a strict regular expression `"speedMillis"\\s*:\\s*(\\d+)`; missing or out-of-range values return HTTP 400. Serialize `SimulationState` manually using `StringBuilder`, emitting numbers and known uppercase types only. Serve classpath resources `/web/index.html`, `/web/styles.css` and `/web/app.js` with UTF-8 and correct content types. `WebMain` constructs a `WebSimulationController(80, 25, 650)`, starts a `WebServer` on port 8080, registers a shutdown hook calling `close()` on both and prints the local URL.

- [ ] **Step 4: Run the HTTP test and verify it passes**

Run: `mvn test -Dtest=WebServerTest`

Expected: the server starts on an ephemeral port and the test receives turn 0 followed by turn 1.

- [ ] **Step 5: Commit the server API**

```bash
git add src/main/java/karty/web/WebServer.java src/main/java/karty/web/WebMain.java src/test/java/karty/web/WebServerTest.java src/main/resources/web/index.html src/main/resources/web/styles.css src/main/resources/web/app.js
git commit -m "feat: expose simulation over HTTP"
```

### Task 3: Browser control panel and movement animation

**Files:**
- Modify: `src/main/resources/web/index.html`
- Modify: `src/main/resources/web/styles.css`
- Modify: `src/main/resources/web/app.js`

**Interfaces:**
- Consumes: `GET /api/state` and the four command endpoints from Task 2.
- Produces: `render(state)`, `requestState(path, options)`, `animateMoves(previous, next)` in `app.js`.

- [ ] **Step 1: Create the static page structure**

```html
<main class="app-shell">
  <header class="hero"><p class="eyebrow">Ecosystem</p><h1>Симуляция мира</h1></header>
  <section class="stats" aria-label="Статистика"><article>🌿 <strong id="grass-count">0</strong></article><article>🐇 <strong id="herbivore-count">0</strong></article><article>🐺 <strong id="predator-count">0</strong></article></section>
  <section class="controls" aria-label="Управление"><button id="play-button">Старт</button><button id="pause-button">Пауза</button><button id="next-button">Следующий ход</button><label>Скорость <input id="speed-input" type="range" min="200" max="2000" step="100" value="650"></label><output id="turn-number">Ход: 0</output></section>
  <p id="error-message" role="alert"></p><section id="board" class="board" aria-label="Карта симуляции"></section>
</main>
```

- [ ] **Step 2: Add responsive grid styling and motion classes**

```css
.board { position: relative; display: grid; grid-template-columns: repeat(var(--columns), minmax(22px, 1fr)); gap: 2px; }
.cell { aspect-ratio: 1; border-radius: 5px; background: #18342c; }
.entity { position: absolute; transition: transform 420ms cubic-bezier(.2,.8,.2,1), opacity 180ms ease; will-change: transform; }
.entity.appearing { opacity: 0; transform: var(--position) scale(.5); }
.entity.leaving { opacity: 0; }
```

Use a dark, forest-themed palette with high-contrast text and a max-width layout. Respect `prefers-reduced-motion` by removing transitions. Keep the board scrollable on small screens rather than shrinking cells below 22 px.

- [ ] **Step 3: Implement state requests and ID-based animation**

```javascript
let previousState = null;
let busy = false;

async function requestState(path, options = {}) {
  setBusy(true);
  try {
    const response = await fetch(path, options);
    const payload = await response.json();
    if (!response.ok) throw new Error(payload.error);
    render(payload);
  } catch (error) {
    document.querySelector('#error-message').textContent = error.message;
  } finally {
    setBusy(false);
  }
}
```

Create the cell layer once per board size, then maintain one absolutely positioned `.entity` element per `entity.id`. Calculate its CSS position using `(x - 1) / width * 100` and `(y - 1) / height * 100`; retain each element between snapshots so CSS transitions animate coordinate changes. Add new elements with `appearing`, mark unseen prior elements as `leaving`, and remove them on `transitionend`. Convert type names to `🌿`, `🪨`, `🌲`, `🐇` and `🐺`; animals use a higher z-index than plants and obstacles. On page load call `requestState('/api/state')`; bind all buttons and slider to API requests, and poll `/api/state` at 150 ms intervals only while `state.running` is true.

- [ ] **Step 4: Verify the browser workflow manually**

Run: `mvn test` and then `java --add-modules jdk.httpserver -cp target/classes karty.web.WebMain`

Expected: `http://localhost:8080` loads the board; one click on «Следующий ход» increments the counter once and animates moved animals; «Старт» advances automatically, «Пауза» stops it, and the slider changes the next interval.

- [ ] **Step 5: Commit the finished browser interface**

```bash
git add src/main/resources/web/index.html src/main/resources/web/styles.css src/main/resources/web/app.js
git commit -m "feat: add animated simulation interface"
```

### Task 4: Final verification and usage documentation

**Files:**
- Create: `README.md`

**Interfaces:**
- Consumes: the compiled `karty.web.WebMain` from Task 2.
- Produces: concise Russian launch instructions for the web mode.

- [ ] **Step 1: Document the exact local launch command**

````markdown
## Веб-интерфейс

Соберите проект и запустите веб-режим:

```powershell
mvn test
java --add-modules jdk.httpserver -cp target/classes karty.web.WebMain
```

Откройте `http://localhost:8080`.
````

- [ ] **Step 2: Run the complete verification suite**

Run: `mvn test`

Expected: all existing and new tests pass.

- [ ] **Step 3: Smoke-test both core API actions**

Run: `Invoke-WebRequest http://localhost:8080/api/state; Invoke-WebRequest -Method Post http://localhost:8080/api/next`

Expected: both responses return HTTP 200; the second response contains a `turn` value exactly one higher than the first.

- [ ] **Step 4: Commit documentation**

```bash
git add README.md
git commit -m "docs: explain web simulation launch"
```
