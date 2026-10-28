# SuperSnake — Engine feel and storage design

Date: 2026-10-28
Status: draft, for owner approval
Extends: `docs/superpowers/specs/2026-10-22-supersnake-design.md` (the main spec). Where this document and the main spec disagree, this document wins, and the main spec gets a one-line pointer to the section here that replaces it.

Tags, as in the main spec:

- **[owner]**: decided by the project owner, in the conversation of 2026-10-27/28.
- **[call]**: a call made by the designer (Claude), played back to the owner, who accepted it ("looks good to me", 2026-10-28). The owner can still overturn it.
- **[builder]**: left to whoever plans and builds.

---

## 1. Why this document exists

The first draft of Plan 2 (game engine) raised two questions that the owner wanted settled before any engine code is written:

1. **Storage.** `datastore-preferences-core` pulls in `com.squareup.okio`, a second third-party runtime library, against main spec §6 ("Current third-party runtime dependencies: libGDX only").
2. **Game feel.** The main spec's rules C1–C12 leave many moments open: when the snake starts, how quick turns behave, how pausing, stalls and the end of a game work. The owner asked for the engine to be "very smooth" and to handle the cases other Snake games have already solved.

Success: a player never loses a turn they made, never dies from something they didn't do, and every moment of a game has one defined, tested behaviour in `:domain`.

### Anti-goals

- A forgiving engine that drifts from the original Nokia feel. The owner chose original Nokia behaviour twice: no grace period at walls, and fixed speed.
- Any third-party runtime library beyond libGDX.
- A rule that lives only in UI code. Every rule below belongs to `:domain` or `:data`, under the 100% coverage gate (main spec §5).

---

## 2. Research behind it

Two research passes ran on 2026-10-28. Their sources are listed so the reasoning can be rechecked.

### Storage

All facts were checked on 2026-10-28.

- **Every DataStore "Preferences" artifact depends on okio.** `datastore-preferences-core-jvm` 1.3.0 depends on `datastore-core-okio-jvm` and `okio-jvm` 3.17.0. The Android flavour does too. Source: the POM and `.module` files under `https://dl.google.com/android/maven2/androidx/datastore/`.
- **`datastore-core-jvm` 1.3.0 does not depend on okio.**
  - Its POM and its Gradle module metadata list only `kotlin-stdlib`, `kotlinx-coroutines-core` and `androidx.annotation:annotation`, at both compile time and runtime.
  - A research note said it also needs `atomicfu` at runtime. The `.module` file shows it does not.
  - The main session read both files itself on 2026-10-28.
- **okio's health.** GitHub redirects `square/okio` to `lysine-dev/okio`. The repo has 9,054 stars, its last push was 2026-10-26, and 2 issues were opened and 3 closed in the last 30 days. Its latest stable release is 3.18.2. Known vulnerabilities:
  - none for `okio-jvm` 3.17.0, per OSV
  - one past advisory, GHSA-w33c-445m-f8w7, in gzip code
  - The new owner org was not verified.

Options considered:
- DataStore Preferences (needs okio)
- DataStore core with our own serializer
- SharedPreferences (Android only)
- a JDK-only file store
- Room (far too heavy)
- `java.util.Properties` (no atomic write)

### Game feel

Well supported, by several independent write-ups and forum threads, for example:
- https://www.lexaloffle.com/bbs/?pid=142543
- https://forum.freecodecamp.org/t/just-for-fun-my-first-javascript-game-snake/584535
- https://dev.to/bibekkakati/how-to-build-a-classic-snake-game-using-reactjs-5dn8/comments
- https://modd.io/forum/t/grid-based-movement-input-buffer/4533

These sources support the following:
- **Lost turns.** Turns made between two steps get lost, or two quick turns kill the snake by reversing it into its neck. A small bounded turn queue fixes both, as long as each turn is checked against the last queued direction.
- **Unbounded queues.** An unbounded queue makes a held key lock the snake onto a path. One open-source Snake had exactly this bug: https://archive.lists.launchpad.net/snakes-game/msg00033.html. The queue must be bounded, and repeats must be dropped.
- **Food placement.** Pick food from a list of free cells, never by retrying random cells. Retrying slows down on a full board and never ends on a completely full one.
- **Timing.** Use a fixed timestep driven by elapsed time, cap catch-up, and interpolate between steps (https://gafferongames.com/post/fix_your_timestep/).
- **Accessibility.** The Game Accessibility Guidelines (https://gameaccessibilityguidelines.com/full-list/) ask for:
  - adjustable game speed
  - not relying on precise timing
  - nothing conveyed by colour alone
  - nothing conveyed by sound alone

Not found: how Nokia Snake treats the tail's cell, and whether Google Snake forgives late turns. The rules below make an explicit choice on both rather than claim a source.

---

## 3. Storage decision **[owner]**

**Owner decision, 2026-10-28:** `:data` uses **`androidx.datastore:datastore-core`** with our own serializer. It does not use `datastore-preferences-core`, so okio is not a dependency.

- **What we keep from DataStore:**
  - atomic writes (write to a temp file, then rename)
  - one writer at a time
  - a corruption handler
  - a `Flow` of the current value

  So the main spec's choice of DataStore (§4) stands, in its core form.
- **What we write:** one serializer per stored value. Each turns the value into a small UTF-8 text format and back.
  - These serializers hold decisions, such as what to do with a bad line, so they sit under the 100% gate.
  - No serialization library is added.
- **Two stores, two files:** one for settings, one for high scores. The file names are constants in `:data`, because Plan 3's backup rules must name exactly those files (main spec §7.2).
- **Corrupt or unreadable file:** the store reads it as the default value (default settings, or an empty high-score table) and the game carries on. A malformed high-score line is dropped, and the other lines are kept. An unknown difficulty or control-scheme name falls back to that field's default.
- **`:data` stays a pure Kotlin JVM module.** This answers main spec §12 "whether `:data` can be pure JVM".
- **Proved first:** the first `:data` task proves, with evidence, that `datastore-core` 1.3.0 runs in a plain Kotlin JVM module under Kover. This is the one remaining unverified point.
- **[call]** If that proof fails, work stops and the owner decides. The fallback the owner has already seen is a JDK-only file store (atomic temp-file-and-rename, a mutex, a `Flow`). It is not adopted without the owner's say, because it would replace the main spec's DataStore choice.
- **Main spec changes:** none to §6, which still lists libGDX as the only third-party runtime library. §11 gains `datastore-core` in place of `datastore-preferences`.

---

## 4. Game feel decisions

### 4.1 Starting a game

- **[owner]** Pressing Play shows a **3-2-1 countdown**, then the snake starts moving.
- **[call]** Each number shows for one second, so the countdown lasts 3 seconds in total.
- **[call]** Turns made during the countdown are queued and used from the first step. A turn back into the snake (left, at the start) is ignored, as always.
- **[call]** The snake starts as in C2: 3 segments in the centre, heading right. The first orb is on the board from the start of the countdown.
- **[call]** The game has **one countdown status**, shared by a new game and by every resume. A Restart from the pause screen and a Play again from the game-over screen are new games, so they also count down.
- **[call]** The countdown runs on the same elapsed-time clock as the steps (4.4). When it reaches 0, the next step happens one full step interval later.
- **[call]** TalkBack announces "3", "2", "1" and "Go". "Go" replaces the main spec's "game start" announcement, so there is no duplicate.

### 4.2 Turning

These rules replace C5 and C6 in the main spec.

- **[call]** Up to **2** turns wait in a queue. The snake takes **one turn per step**.
- **[call]** Each new turn is checked against the **last queued direction**, or against the current direction when the queue is empty:
  - the opposite direction is ignored, because the snake can't reverse
  - the same direction is ignored, so a repeated or held key can't fill the queue
- **[call]** When the queue is full (2 turns), a new valid turn **replaces the last waiting turn**, so the player's latest intent wins. The replacement is checked against the turn before it (or the current direction). With 0 or 1 turns waiting, a valid turn is simply added.
- **[call]** The same 2-turn limit applies during the countdown, so if the player swipes several times while it runs, the last valid swipe is the one that counts.
- **[call]** Because each turn is checked against the one before it, "up then left" within a single step is a safe U-turn over two steps, never a crash into the neck.
- **[call]** Turns are accepted while the game is running or counting down. They are ignored while paused, and after the game ends.

### 4.3 Moving, eating and collisions

- **[owner]** **No grace period.** A move into a wall or into the body ends the game on that step, as in the original Nokia game. A turn that arrives after the step is too late.
- **[call]** Moving into the cell the tail is leaving is legal. On a step that eats, the tail stays, so moving into it is a collision (C7, unchanged).
- **[call]** Food spawns on a uniformly random free cell, including the cell right in front of the head, as in the original (C3, unchanged).
  - The free cell is picked from a list of free cells, so placement takes the same time on a nearly full board.
- **[call]** When no free cell is left after eating, the game ends as a win (C9, unchanged).

### 4.4 Speed and timing

- **[owner]** **Speed is fixed for the whole game** by the chosen difficulty (C8, unchanged). No speed-up as the snake grows.
- **[call]** Steps are timed by elapsed time, not by frame count, so the speed is the same at any frame rate.
- **[call]** After a stall (a hitch, a garbage-collection pause, a slow frame), the game takes **at most one step** per frame and then carries on at normal speed. It never runs a burst of catch-up steps.
- **[call]** The timing model in detail:
  - Each step is due one interval after the previous one.
  - When a frame comes in less than two intervals after the last step, the step happens and the next one is due one interval after the scheduled time. Small frame jitter therefore keeps an even rhythm.
  - When a frame comes in two or more intervals late, the step happens and the leftover time is discarded: the next step is due one interval after this frame.
- **[call]** The glide position (4.7) is the time since the last step divided by the interval, clamped to the range 0 to 1. In a stall frame it is 1 until the step happens.
- **[call]** A clock that goes backwards produces no step and no error. The glide position is 0, and the timer keeps its reference point until time passes it again.

### 4.5 Pausing and interruptions

- **[call]** The game pauses itself when the app goes to the background, and on a configuration change such as rotation (extends C10). It resumes only on an explicit Resume.
- **[call]** **Resume always runs the 3-2-1 countdown** before the snake moves again.
- **[call]** Pausing clears queued turns, so nothing turns the snake after resume until the player acts.
- **[call]** Pausing during a countdown is allowed. It goes to the paused state, clears queued turns and drops the countdown's progress. Resuming starts the countdown again from 3. The snake, food and score are untouched.
- **[call]** The snake, food, score and queue survive rotation and backgrounding. The `:domain` state is plain data, so `:app` can keep it across configuration changes (main spec §5).

### 4.6 Ending a game and scores

- **[call]** At a crash, the snake is drawn at its last legal position, with the crash shown at the head. It is not drawn sliding into the wall or body.
- **[call]** The result is saved to the high-score table at the moment the game ends, before any screen is shown, so closing the app straight after can't lose it. A win and a crash go through the same save.
- **[call]** A score of 0 is not saved.
- **[call]** The table is sorted by score, highest first. On a tie the older entry stays above, so a new score must beat the 10th score to enter a full table.
- **[call]** A score equal to the 10th score does not enter a full table.
- **[call]** A "new high score" means first place. Equalling the current best is not a new high score, because the older entry stays above.
- **[call]** Points per orb are the level number, or 1 in Assist (C8, unchanged). There are no bonuses.

### 4.7 Smooth movement

- **[call]** Between two steps, each segment glides from its previous cell to its current cell, according to how far the time is between the two steps (from 0 to 1).
- **[call]** On a step that eats, the new tail segment holds still while the snake grows.
- **[call]** Nothing is drawn moving past a crash (see 4.6).
- **[builder]** How corners are drawn, for example a segment following the path around a turn rather than cutting the diagonal. It is a drawing choice, as long as the positions come from `:domain` maths.

### 4.8 Swipes and keys

- **[call]** A swipe fires as soon as the finger has moved past the threshold, **not when it lifts**.
- **[call]** Within one stroke, the starting point resets after each turn, so up then right in one stroke gives two turns.
- **[call]** `:domain` receives the threshold in pixels. `:app` converts it from dp, and Plan 3 picks the dp value. Only the first finger on the board counts.
- **[call]** The longer axis decides the direction ("dominant axis", main spec §5). An exact diagonal, where both axes are equal, is no turn. No tolerance is added.
- **[call]** The classification is pure maths in `:domain` and takes a movement vector and a threshold. `:app` only converts `MotionEvent`s into vectors.
- **[call]** Holding a key down repeats the same direction, which the "same direction is ignored" rule in 4.2 already absorbs. No key-repeat handling is needed in `:app`.

### 4.9 Accessibility links

Main spec §3 stays as written. This design adds:

- **[call]** The countdown is announced by TalkBack, as "3", "2", "1", "Go", in addition to the announcements in §3. It is shown visually as well.
- **[call]** Speed choice (Assist plus 1–9), pause at any time, and no grace period are consistent with the Game Accessibility Guidelines' "adjustable speed". Assist is the accessible path for players who need more time.

---

## 5. What changes in Plan 2

- Plan 2 is rewritten from this document together with the main spec. Task boundaries can stay similar, with these changes:
  - the countdown becomes part of the game state, as a new status
  - "replace the last waiting turn when full" replaces "drop when full"
  - resume goes through the countdown
  - `:data` uses `datastore-core` with our own serializers
  - the okio owner checkpoint is removed
- `docs/game-rules.md` gains each **[call]** above as a rule numbered from C13 on, so a player-facing rule list exists in one place. C5 and C6 stay in the list, marked "replaced by" the new rules, because superseded decisions are never deleted. The main spec's C5 and C6 get the same one-line pointer to §4.2 here.
- Plan 3 owns the pieces in `:app`: lifecycle pause, rotation, the swipe-to-vector translation, the dp threshold, TalkBack wiring and drawing.

## 6. Left to the builder

- The exact states and types: for example, how the countdown is represented in `GameState`, and how the swipe's moving start point is represented.
- The text format of the two stored files, within §3.
- How corners are drawn (4.7).
