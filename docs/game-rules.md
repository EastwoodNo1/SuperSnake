# Game rules

Source: design spec §2. Every rule is tagged **[source]** (S1 to S5) or **[call]** (C1 to C12).

## Source **[owner]**

The rules are based on the original Nokia *Snake* (Taneli Armanto, Nokia, 1998, Nokia 6110/5110), as documented by:

- Juho Kuorikoski, *Finnish Video Games: A History and Catalog*, McFarland, 2015, pp. 48–50
- as cited by Wikipedia, [Snake (1998 video game)](https://en.wikipedia.org/wiki/Snake_(1998_video_game))

The 1998 Nokia 6110 user guide does not document the rules in detail. No official rules spec exists.

## Rules from the source

| # | Rule |
|---|------|
| S1 | The snake moves continuously in one of four directions. The player changes its direction. |
| S2 | The play field is a bounded box. |
| S3 | Orbs (food) appear on the field. Eating one increases the score and the snake's length. |
| S4 | Colliding with a wall or with the snake's own body ends the game. |
| S5 | There is a difficulty setting. |

## Rules that are our calls **[call]**

| # | Rule |
|---|------|
| C1 | The board is a fixed 20 × 20 grid, drawn as a square and centred (letterboxed) in any orientation and on any screen size. |
| C2 | The snake starts with 3 segments in the centre, heading right. |
| C3 | One orb is on the board at a time. It spawns on a uniformly random free cell. The random source is injected so tests are deterministic. |
| C4 | Eating an orb adds exactly 1 segment: on the tick that eats it, the tail does not move. |
| C5 | A turn into the opposite of the last queued direction is ignored. If the queue is empty, the current direction is used instead. **Replaced by C15 (2026-10-28).** |
| C6 | Turn inputs are buffered, with up to 2 queued turns, so fast double turns are not lost. A turn is applied at most once per tick. **Replaced by C16 (2026-10-28).** |
| C7 | Moving into the tail's cell is legal only when the tail leaves that cell on the same tick. On a growth tick (C4) the tail stays, so moving into it is a collision. |
| C8 | Difficulty is **Assist** plus levels 1–9.<br>• Levels 1–9: tick interval = `250 − round(22.5 × (level − 1))` ms, where `round` rounds halves up. That gives exactly 250, 227, 205, 182, 160, 137, 115, 92, 70 ms. Points per orb equal the level.<br>• **Assist**: 400 ms per tick and 1 point per orb, for players who need more time. It shows as "A" in high scores. |
| C9 | If no free cell remains after eating, the game ends as a **win**. |
| C10 | The game pauses automatically when the app goes to the background, and resumes only on an explicit user action. |
| C11 | Single player only. |
| C12 | The top 10 high scores (score, level, date) are stored on the device. |
| C13 | **[owner]** A game starts with a 3-2-1 countdown, one second per number, then the snake moves. The same countdown runs on every resume, restart and play-again. The first orb is on the board from the start of the countdown. |
| C14 | Turns are accepted while counting down or running, and ignored while paused or after the game ends. A turn made during the countdown is used from the first step. |
| C15 | A turn is checked against the last queued direction, or the current direction when nothing is queued. The opposite direction and the same direction are both ignored. Replaces C5. |
| C16 | Up to 2 turns wait in a queue, and the snake takes one per step. When 2 are waiting, a new valid turn replaces the last one, checked against the turn before it. Replaces C6. |
| C17 | **[owner]** No grace period. A move into a wall or the body ends the game on that step, as in the original Nokia game. |
| C18 | **[owner]** Speed is fixed for the whole game by the chosen difficulty. |
| C19 | Steps are timed by elapsed time. A frame less than two intervals after the last step keeps the rhythm; a later frame gives one step and drops the leftover time. A frame never gives more than one step. A clock that goes backwards gives no step. |
| C20 | The game pauses itself when the app goes to the background or the screen rotates, and resumes only on Resume, through the countdown. Pausing clears queued turns. Pausing during a countdown restarts it from 3 on resume. The snake, orb and score survive rotation and backgrounding. |
| C21 | At a crash the snake is shown at its last legal position, not sliding into the wall or body. |
| C22 | A finished game, won or lost, is saved the moment it ends. A score of 0 is not saved. Ties keep the older entry above, so a full table needs a score above its 10th. A new high score means first place; equalling the best is not one. |
| C23 | Between steps each segment glides from its previous cell to its current one. On a step that eats, the new tail segment holds still. |
| C24 | A swipe fires as soon as the finger passes the threshold, and the start point resets after each turn, so one stroke can turn twice. Only the first finger counts. The longer axis decides the direction; an exact diagonal is no turn. |
| C25 | TalkBack announces "3", "2", "1" and "Go". "Go" is the game-start announcement. |

The rules engine is a pure function of state, input and random source, which makes it fully unit-testable.

C13 to C25 come from `docs/superpowers/specs/2026-10-28-engine-feel-and-storage-design.md` §4 (owner-approved 2026-10-28). Rules marked **[owner]** were the owner's choices; the rest are designer calls the owner accepted.

## Engine details **[builder]**

Values the rules leave open, chosen while building. Each row names the task that chose it.

| # | Detail | Why |
|---|--------|-----|
| B1 | Default settings are difficulty `LEVEL_1`, sound on, haptics on, swipe controls (Task 5) | A new player gets the first real level with feedback on and the one-finger scheme, with no setup screen in the way. |
| B2 | A stall during the countdown skips the numbers it missed: the frame shows the current number, or `Go`, and announces only that (Task 6) | Announcing numbers that already passed would read them out late and in a burst; the player only needs to hear where the count is now. |
| B3 | Pulse brightness is `0.8 + 0.2 × sin(2π × t / 1200 ms)`, and a constant `1.0` with reduce motion (Task 7) | The orb breathes gently without ever dimming below 80%, and a player who asked for less motion sees it steady. |
| B4 | An orb burst is 12 particles at even angles from the cell centre, travelling 1.2 cells with ease-out `1 − (1 − t)²` over 400 ms, alpha `1 − t`, and none with reduce motion (Task 7) | A short, fast-then-settling burst reads as a pop without lingering over the board, and reduce motion removes it. |
| B5 | Settings are stored as UTF-8 lines `key=value` with keys `difficulty`, `sound`, `haptics`, `control_scheme`: enum names, booleans `true` or `false`; unknown keys and lines without `=` are ignored and a missing or unparsable value takes that field's default (Task 8) | A plain text file survives a half-written or hand-edited state, and reading never throws, so no corruption handler is needed. |
| B6 | High scores are stored one entry per line as `score\|DIFFICULTY_NAME\|yyyy-MM-dd`; lines that do not parse are dropped and the rest is normalized to the top 10 (Task 8) | One bad line costs one entry, not the table, and the format needs no library. |
| B7 | System Back during a game pauses it while the status is `COUNTDOWN` or `RUNNING`, and leaves the game when it is `PAUSED`, `LOST` or `WON` (Task 1) | Back never throws away a running game by accident, and once the game is stopped Back does what a player expects and goes to the previous screen. |
| B8 | Beeps are square waves at amplitude 8192 with a 5 ms linear attack and release per note, made in code: `COUNT` 440 Hz × 80 ms, `GO` 880 Hz × 150 ms, `EAT` 1320 Hz × 40 ms, `CRASH` 220 Hz × 120 ms then 110 Hz × 240 ms, `WIN` 523, 659, 784 and 1047 Hz × 100 ms each (Task 2) | No audio files to license or ship, a quarter-scale wave stays comfortable, and the short ramps stop notes from clicking. |
| B9 | A frame plays `COUNT` for a countdown number, `GO` for go, and `EAT` with a tick, `CRASH` with a reject or `WIN` with a confirm for its events, announcement sounds first; sound off drops every sound and haptics off drops every haptic; pause, milestones and a new high score play nothing (Task 2) | The mapping reads from the frame alone, the spoken announcements already cover pause, milestones and new bests, and each setting silences only its own channel. |
| B10 | The neon palette is background `#07061A`, surface `#1B1446`, snake `#5EF2FF`, orb `#FF4FD8`, wall `#2266CC`, text `#EAFCFF`, on-primary `#1A0A24`; snake, orb and wall are each at least 3:1 against the background and at least 1.5:1 against each other, text and on-primary at least 4.5:1 (Task 3) | Shapes must stay distinguishable by brightness, not only hue. The mockup's wall `#2A8CFF` differed from the orb by only 1.17:1, so the wall is darker, and `Contrast` proves every pair. |
| B11 | The game pauses on `ON_PAUSE`, not `ON_STOP` (Task 4) | The GL surface stops drawing at `onPause`, so a game that kept running there could not be seen. |
| B12 | The swipe threshold is 24 dp, converted to pixels with `LocalDensity` (Task 4) | A fixed physical distance turns the snake the same on every screen density (C24). |
| B13 | Glow: each shape is drawn three times at scale 1.0, 1.3 and 1.6 with alpha 1.0, 0.35 and 0.15. The two glow passes use additive blending and the scale 1.0 pass is drawn opaque on top, so the core keeps the exact palette colour. There are no shaders (Task 4) | A neon look without shader code, and the cell-centre pixel the UI tests read stays within 40 per channel of the palette colour, which pure additive stacking would push to about 1.5 times it. |
| B14 | The APK carries libGDX natives for `arm64-v8a`, `armeabi-v7a` and `x86_64` only (Task 4) | No device with minSdk 31 needs 32-bit x86, and every shipped `libgdx.so` has 16 KB segment alignment. |
| B15 | The D-pad is four 64 dp buttons in a cross, shown only when the control scheme is D-pad, below the board in portrait and beside it in landscape; the swipe layer stays active in both schemes (Task 7) | A player who chose the D-pad can still swipe, and the board keeps its square shape because the buttons take their own space instead of covering it. |
| B16 | Keyboard and gamepad keys map to `GameKey` through one table: arrows and W A S D turn, Space, P, Escape and Start toggle pause; a key that is not in the table is not handled (Task 7) | One data table decides, so the view model needs no branch and an unmapped key keeps its normal focus behaviour. |
| B17 | The countdown clock is held from `newGame()` until the board draws its next frame, through `HoldableClock` in `:domain` (Task 5) | On a slow device the first board frame came 1.76 s after `newGame()`, so the player saw "3" and "2" over an empty screen; holding the clock makes the countdown start when the board is visible, without changing `GameSession`. |
