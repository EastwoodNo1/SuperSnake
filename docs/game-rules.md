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
| C20 | The game pauses itself when the app goes to the background or the screen rotates, and resumes only on Resume, through the countdown. Pausing clears queued turns. Pausing during a countdown restarts it from 3 on resume. Snake, orb, score and queue survive rotation and backgrounding. |
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

