# Changelog

## [1.0.2](https://github.com/EastwoodNo1/SuperSnake/compare/v1.0.1...v1.0.2) (2026-11-20)


### Bug Fixes

* create the board only after the system bars settle ([#195](https://github.com/EastwoodNo1/SuperSnake/issues/195)) ([28df360](https://github.com/EastwoodNo1/SuperSnake/commit/28df3605e9b6cbc010f378a7a0b989fb6c243a99))

## [1.0.1](https://github.com/EastwoodNo1/SuperSnake/compare/v1.0.0...v1.0.1) (2026-11-20)


### Bug Fixes

* add a Back button to High scores and Settings ([#156](https://github.com/EastwoodNo1/SuperSnake/issues/156)) ([dcb617e](https://github.com/EastwoodNo1/SuperSnake/commit/dcb617e4adde1568e803da901324e1c7bd62b773))
* exempt tagged release PRs in pr-rules ([#141](https://github.com/EastwoodNo1/SuperSnake/issues/141)) ([e69bc39](https://github.com/EastwoodNo1/SuperSnake/commit/e69bc391709fa1d40f497c4ec69f75e291a59b66)), closes [#139](https://github.com/EastwoodNo1/SuperSnake/issues/139)
* follow animation scale and reset the live flag ([#164](https://github.com/EastwoodNo1/SuperSnake/issues/164)) ([af7c211](https://github.com/EastwoodNo1/SuperSnake/commit/af7c2111057931f3dff715d6c7fd8b948923371e))
* keep audio and score-store failures from crashing ([#167](https://github.com/EastwoodNo1/SuperSnake/issues/167)) ([29b7b11](https://github.com/EastwoodNo1/SuperSnake/commit/29b7b11d32ad3ff56d736b862d76a6d088fc8e4d))
* keep audio and score-store failures from crashing the app ([29b7b11](https://github.com/EastwoodNo1/SuperSnake/commit/29b7b11d32ad3ff56d736b862d76a6d088fc8e4d)), closes [#152](https://github.com/EastwoodNo1/SuperSnake/issues/152)
* record quit games and keep the end-of-game save alive ([8c660b2](https://github.com/EastwoodNo1/SuperSnake/commit/8c660b2e323dace551c6461d9bc3c86a25677f00))
* record quit games and keep the game-over save alive ([#163](https://github.com/EastwoodNo1/SuperSnake/issues/163)) ([8c660b2](https://github.com/EastwoodNo1/SuperSnake/commit/8c660b2e323dace551c6461d9bc3c86a25677f00))
* silence the apksigner native-access warning ([#142](https://github.com/EastwoodNo1/SuperSnake/issues/142)) ([f668f49](https://github.com/EastwoodNo1/SuperSnake/commit/f668f493fecdd943bdef1aef61d1d7b2e40aa815))


### Performance

* build the beep PCM once per process ([#159](https://github.com/EastwoodNo1/SuperSnake/issues/159)) ([7b9ef17](https://github.com/EastwoodNo1/SuperSnake/commit/7b9ef176d2a83488bbd75645fcbeacec45ae7a48))

## 1.0.0 (2026-11-17)


### Features

* add back stack, overlays, spoken lines and swipe control ([#73](https://github.com/EastwoodNo1/SuperSnake/issues/73)) ([054740e](https://github.com/EastwoodNo1/SuperSnake/commit/054740e03cf0a48ff71d7d538aefaf7637bd8547))
* add board geometry, difficulty and random source ([#49](https://github.com/EastwoodNo1/SuperSnake/issues/49)) ([06eb257](https://github.com/EastwoodNo1/SuperSnake/commit/06eb257d1750856d30dcda6c465e79d32a1075c3))
* add board layout, glide, pulse and particle maths ([#61](https://github.com/EastwoodNo1/SuperSnake/issues/61)) ([3c3d827](https://github.com/EastwoodNo1/SuperSnake/commit/3c3d82709ae4d09396d129a7062c32c34d07fdf5))
* add game state and the step rules ([#51](https://github.com/EastwoodNo1/SuperSnake/issues/51)) ([48d871a](https://github.com/EastwoodNo1/SuperSnake/commit/48d871aa0017e567e743634cee16e6d729653278))
* add git hooks enforcing worktrees, commits and checks ([#18](https://github.com/EastwoodNo1/SuperSnake/issues/18)) ([c55046a](https://github.com/EastwoodNo1/SuperSnake/commit/c55046ac1dbab031f0d1f99a2d5c53ff415eb9c4))
* add high score and settings use cases ([#57](https://github.com/EastwoodNo1/SuperSnake/issues/57)) ([261af9c](https://github.com/EastwoodNo1/SuperSnake/commit/261af9c9191b721156e00b7b9bc148dd80e57eaa))
* add HUD, overlays, D-pad, keys, sound and announcements ([#86](https://github.com/EastwoodNo1/SuperSnake/issues/86)) ([4d451a2](https://github.com/EastwoodNo1/SuperSnake/commit/4d451a22666a4c69c595db24c8d5423d5b6d7c88))
* add neon theme, splash, menu, high scores and settings ([#84](https://github.com/EastwoodNo1/SuperSnake/issues/84)) ([c8649e7](https://github.com/EastwoodNo1/SuperSnake/commit/c8649e723d3c1b44e37d6a1113f13140b5d8d8c0))
* check backup rules name only the two store files ([#90](https://github.com/EastwoodNo1/SuperSnake/issues/90)) ([7410fd6](https://github.com/EastwoodNo1/SuperSnake/commit/7410fd6d75a5ce0b1f34b2773ae0f58799507b46))
* **checks:** add commit message, PR body and tests-changed checks ([#10](https://github.com/EastwoodNo1/SuperSnake/issues/10)) ([5f7e6c6](https://github.com/EastwoodNo1/SuperSnake/commit/5f7e6c6b5d8ed0420c3a8579943aafecd7f2fdbb))
* **checks:** add syntax-aware no-comments checker ([#6](https://github.com/EastwoodNo1/SuperSnake/issues/6)) ([29bbc72](https://github.com/EastwoodNo1/SuperSnake/commit/29bbc72b5c23a18a998e1c7d77f4301b193cd956))
* **checks:** add workflow safety and manifest hardening checks ([#14](https://github.com/EastwoodNo1/SuperSnake/issues/14)) ([9a7ef07](https://github.com/EastwoodNo1/SuperSnake/commit/9a7ef075d0a752c540590916fedb658c91ea4efe))
* classify swipes and keys into game input ([#55](https://github.com/EastwoodNo1/SuperSnake/issues/55)) ([ede91d3](https://github.com/EastwoodNo1/SuperSnake/commit/ede91d391d2b5734a203ca8e1b660d844d60ba7e))
* compose the board scene and the neon palette ([#77](https://github.com/EastwoodNo1/SuperSnake/issues/77)) ([529710b](https://github.com/EastwoodNo1/SuperSnake/commit/529710b9e08284bf1729ef7be820279726f449b3))
* derive versionCode from version.txt ([#110](https://github.com/EastwoodNo1/SuperSnake/issues/110)) ([3c47d3a](https://github.com/EastwoodNo1/SuperSnake/commit/3c47d3a50445de563cce81644ec71dfe52e856c4))
* **domain:** add Direction with opposite ([802bffe](https://github.com/EastwoodNo1/SuperSnake/commit/802bffe8acc2922246607d7b38fff9549783f0ea))
* drive countdown, steps and announcements ([#59](https://github.com/EastwoodNo1/SuperSnake/issues/59)) ([34ab754](https://github.com/EastwoodNo1/SuperSnake/commit/34ab754e8bdacaee711e26d3df035c89f0902fca))
* embed the libGDX board and play a game ([#79](https://github.com/EastwoodNo1/SuperSnake/issues/79)) ([e49b0ec](https://github.com/EastwoodNo1/SuperSnake/commit/e49b0ecee6d58fcb241dea997aad82c07dcd410d))
* map game events to beeps and haptics ([#76](https://github.com/EastwoodNo1/SuperSnake/issues/76)) ([a7c3fe7](https://github.com/EastwoodNo1/SuperSnake/commit/a7c3fe7399280358cd8cbbc098a2e8a498fe8f11))
* persist settings and high scores with DataStore ([#63](https://github.com/EastwoodNo1/SuperSnake/issues/63)) ([b995937](https://github.com/EastwoodNo1/SuperSnake/commit/b995937afa19ee4c738937d22b4925afe567867e))
* play the game in full screen ([#106](https://github.com/EastwoodNo1/SuperSnake/issues/106)) ([a459a4f](https://github.com/EastwoodNo1/SuperSnake/commit/a459a4ff6a6cd05d3b72b346c4dea72725bc016d))
* queue turns and pause the game ([#53](https://github.com/EastwoodNo1/SuperSnake/issues/53)) ([35a5bab](https://github.com/EastwoodNo1/SuperSnake/commit/35a5bab016b02d6b24b600195c638b2504d8763a))


### Bug Fixes

* apply Plan 2 final review findings ([#65](https://github.com/EastwoodNo1/SuperSnake/issues/65)) ([b0a73a3](https://github.com/EastwoodNo1/SuperSnake/commit/b0a73a3738f1c7ef98a81ac2b2e7e7bb9a2d18ac))
* apply Plan 3 final review findings ([#92](https://github.com/EastwoodNo1/SuperSnake/issues/92)) ([074bbdd](https://github.com/EastwoodNo1/SuperSnake/commit/074bbdd9188e9715594bc17c096edf38c4cc3a18))
* apply the Plan 4 review findings ([#116](https://github.com/EastwoodNo1/SuperSnake/issues/116)) ([4676814](https://github.com/EastwoodNo1/SuperSnake/commit/4676814c96d5dcd4c2b62e347912ebbc902e0f23))
* **checks:** allowlist guarded exports and limit launcher rule ([#16](https://github.com/EastwoodNo1/SuperSnake/issues/16)) ([b1f0bd7](https://github.com/EastwoodNo1/SuperSnake/commit/b1f0bd745512fb667c4e3d8297fed39c2700d0e9))
* document plugin install and bump gh to 2.103.2 ([#36](https://github.com/EastwoodNo1/SuperSnake/issues/36)) ([9c0ffc4](https://github.com/EastwoodNo1/SuperSnake/commit/9c0ffc4cff55372cdfae235c6c2103efa883c14f))
* harden bootstrap SDK checks and hook tests ([#20](https://github.com/EastwoodNo1/SuperSnake/issues/20)) ([9e33edb](https://github.com/EastwoodNo1/SuperSnake/commit/9e33edb03f282ffbdb1facbd227d944431fd2c0a))
* harden ktlint file list and PR checks ([#12](https://github.com/EastwoodNo1/SuperSnake/issues/12)) ([1fb7d98](https://github.com/EastwoodNo1/SuperSnake/commit/1fb7d98aa85b4ab4afd9fe1bca918c8f5e74a77d))
* resume the game when a system dialog pauses the smoke ([#119](https://github.com/EastwoodNo1/SuperSnake/issues/119)) ([5459306](https://github.com/EastwoodNo1/SuperSnake/commit/54593066cd3f789a902e77fc33ba658b3f3f5922))
* tap Play again when a system dialog swallows the tap ([#130](https://github.com/EastwoodNo1/SuperSnake/issues/130)) ([44fbf63](https://github.com/EastwoodNo1/SuperSnake/commit/44fbf6323f265497c4c65077422c85add1da4c20))
