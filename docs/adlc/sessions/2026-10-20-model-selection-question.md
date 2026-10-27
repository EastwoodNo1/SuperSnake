---
session: b283293d-0b4c-4c7c-9395-d6dcbaeec908
date: 2026-10-20
harness: claude
branches: none observed
evidence_status: Observed from a short digest (about 2 minutes). No project work happened in this session.
---

# Session note: model selection question (non-project)

## Goal
The human wanted to know whether a newer model was available in their Claude Code instance, and asked for an upgrade.

## What happened
- The human ran `/model` and asked how to get the latest model.
- The first reply reported a model-selection error. The second said Opus 4.7 was the latest and suggested switching back with `/model`.
- The human said newer models exist. The AI said its knowledge cutoff predated the current date, and suggested the `/model` picker and updating the CLI.
- The human exited.

## Key prompts
- Reconstructed prompt intent - not verbatim: asks how to get the newest model in the tool, then insists newer models exist and asks for an upgrade.

## Decisions
None. No alternatives were weighed and no project artifacts were changed.

## Failures and rework
- The AI asserted a model was the "latest" from its own knowledge. The human corrected it. The AI then acknowledged its cutoff and pointed to the picker and a CLI update.

## Human contribution
Noticed that the AI's claim was stale and corrected it.

## AI contribution
Gave generic guidance. The first answer was overconfident about what the latest model was.

## Concepts and principles learned
- A model's knowledge of "latest available models" is bounded by its training cutoff. The tool's own picker is the authoritative source.

## Durable lessons
- When asked about the latest models or versions, check the tool's own listing or an authoritative source on the day instead of answering from memory. This matches the repo's rule that versions are verified on the day they are used.

## Presentation candidates
- Headline: "The AI said it was already on the latest model."
- Key message: stale knowledge presented as fact needs human correction and live verification.
- Example: the exchange above, where the human's "there are newer models" prompted the AI to admit its cutoff.

## Evidence
- Session digest only. No commits, PRs or files.

## Gaps and uncertainty
- The model names and the update command came from the AI's replies and were not verified. The outcome of the human's model change is unknown.
- Not related to the SuperSnake plan or requirements.
