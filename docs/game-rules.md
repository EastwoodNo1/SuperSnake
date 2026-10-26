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
| C5 | A turn into the opposite of the last queued direction is ignored. If the queue is empty, the current direction is used instead. |
| C6 | Turn inputs are buffered, with up to 2 queued turns, so fast double turns are not lost. A turn is applied at most once per tick. |
| C7 | Moving into the tail's cell is legal only when the tail leaves that cell on the same tick. On a growth tick (C4) the tail stays, so moving into it is a collision. |
| C8 | Difficulty is **Assist** plus levels 1–9.<br>• Levels 1–9: tick interval = `250 − round(22.5 × (level − 1))` ms, where `round` rounds halves up. That gives exactly 250, 227, 205, 182, 160, 137, 115, 92, 70 ms. Points per orb equal the level.<br>• **Assist**: 400 ms per tick and 1 point per orb, for players who need more time. It shows as "A" in high scores. |
| C9 | If no free cell remains after eating, the game ends as a **win**. |
| C10 | The game pauses automatically when the app goes to the background, and resumes only on an explicit user action. |
| C11 | Single player only. |
| C12 | The top 10 high scores (score, level, date) are stored on the device. |

The rules engine is a pure function of state, input and random source, which makes it fully unit-testable.

