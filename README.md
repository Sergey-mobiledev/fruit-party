# Fruit Party

Fruit Party is an Android slot game built with Kotlin. It was one of my first full Android projects and helped me practice game logic, MVVM architecture, local persistence, Firebase integration, animations and multi-screen navigation.

The app is inspired by classic arcade slot machines: the player spins fruit reels, selects bet lines and rates, receives wins based on matching symbols, and can trigger a bonus card mini-game.

## Features

- Slot-style gameplay with animated reels
- 5 reel columns rendered with `RecyclerView`
- Configurable bet lines and bet rate
- Credits, current win, and bonus win tracking
- Win calculation for 3, 4, and 5 matching symbols
- Wild-symbol logic with `Coconut`
- Special `Fruit Party` symbol with higher payouts
- Strawberry-triggered bonus game
- Bonus card mini-game with red/black card selection
- Auto-spin mode
- Local user profile with avatar, name, username, credits, and game state
- Local persistence with Room
- Firebase / Firestore-based remote configuration flow
- Blog/privacy-policy screens loaded via Retrofit
- Dependency injection with Koin

## Tech Stack

- Kotlin
- MVVM
- Android ViewModel + LiveData
- Kotlin Coroutines + SharedFlow
- Room Database
- Retrofit + Gson
- Firebase Analytics, Firestore, Auth, Messaging
- Koin
- RecyclerView + DiffUtil
- ViewBinding
- Navigation Component
- Glide

## Architecture

The project follows a simple MVVM-style structure:

```text
app/src/main/java/com/example/fruitparty
├── data
│   ├── database        # Room database, DAOs, converters
│   ├── model           # User, element, card, blog and Firestore models
│   ├── repository      # Local, remote and shared repository logic
│   └── services        # Network API, constants, loading/game states
├── di                  # Koin modules and application setup
└── ui                  # Activities, fragments, adapters and ViewModels
```

`Repository` coordinates the main game state, local Room storage, Firebase data loading, blog API calls, slot result calculation, bonus game state, and shared flows used by the UI.

## Gameplay Logic

The main slot screen contains five vertical reels. During a spin, each reel scrolls to a randomized position. After the animation stops, the visible symbols are collected into active bet lines and passed to the result calculation logic.

The game supports:

- 1 to 3 active lines
- Variable bet rate per line
- Weighted symbol generation
- 3/4/5-symbol payouts
- Wild matching through the Coconut symbol
- Bonus-game activation when enough Strawberries appear

The bonus game lets the player choose a card color and open one of several hidden cards. A correct guess doubles the bonus win, while a wrong guess ends the bonus round.

## Screens

- Splash screen
- Game selection screen
- Slot game screen
- Bonus card game screen
- Blog list and article screens
- Privacy policy screen

## Setup

1. Clone the repository:

   ```bash
   git clone <repository-url>
   ```

2. Open the project in Android Studio.

3. Add your Firebase config if needed:

   ```text
   app/google-services.json
   ```

4. Build and run the app:

   ```bash
   ./gradlew assembleDebug
   ```

## Notes

This project was created early in my Android development journey, so some parts intentionally reflect the learning stage of the codebase. With my current experience, I would improve separation of game-engine logic from UI, add more tests around win calculation, simplify some repository responsibilities, and prepare the monetization layer with Google Play Billing or RevenueCat.

## Future Improvements

- Add in-app purchases for coin packs
- Extract slot result calculation into a dedicated game engine module
- Add unit tests for payout and bonus-game logic
- Improve animations and visual polish
- Add sound and vibration settings
- Refactor Firebase configuration flow
- Add a demo video and screenshots for portfolio presentation

## Author

Sergey Kosarevskiy  
Android / React Native Developer
