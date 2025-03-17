# Java Graphics Simulation

A graphic simulation of a game built in full **Java** using JFrame.

-Small project made for university!

---

## Features

- **Predefined Maze**: Walls (`X`), enemies (`b`), and player (`P`).
- **Collision Detection**: Entities cannot pass through walls.
- **Enemy AI**: Enemies move randomly and change direction on collision.
- **Graphics**: Rendered using Java's `Graphics` class.

---

## Requirements

- **JDK 8+**: Ensure Java is installed.
- **IDE**: IntelliJ, Eclipse, or VS Code recommended.

---

## Setup

1. Clone the repo:
   ```bash
   git clone https://github.com/Noodle-Dev/Graphics-simulation-in-Java.git
   cd Graphics-simulation-in-Java
   ```
---

## Controls

- **Arrow Keys**: Move the player (↑, ↓, ←, →).

---

## Code Overview

- **`Player` Class**: Main game logic, rendering, and input handling.
- **`Block` Class**: Represents entities (player, enemies, walls) with position, direction, and velocity.
- **`tileMap`**: String array defining the maze layout.

---

## Customization

- Edit `tileMap` to redesign the maze.
- Adjust `speed` in `Block.updateVelocity()` for movement speed.

---

## License

**MIT License**. Free to use and modify.

---

## Contact

- **GitHub**: [Enjoy!](https://github.com/Noodle-Dev)

---

Building software with ♥ from Mexico to the world.