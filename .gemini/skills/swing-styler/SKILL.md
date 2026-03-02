---
name: swing-styler
description: Specialized guidance for modernizing Java Swing UIs, including custom button styling, layouts, and Look and Feel enhancements. Use when the user wants to improve the visual aesthetics of the TicTacToe application.
---

# Swing Styler

This skill provides specialized knowledge for styling Java Swing components to create modern, responsive, and visually appealing desktop applications.

## Core Concepts

- **Custom Painting**: Override `paintComponent(Graphics g)` to implement custom shapes, gradients, and anti-aliasing.
- **Modern Look and Feel (L&F)**: Use libraries like FlatLaf or customize standard UIManager properties.
- **Component Extensions**: Extend existing Swing components (like `JButton` to `JAButton`) to add custom properties or behaviors.
- **Layout Management**: Use `GridBagLayout` or `MigLayout` for complex, responsive designs.

## Workflows

### 1. Enhancing Buttons (e.g., JAButton)
- Use `Graphics2D` for high-quality rendering.
- Implement hover and pressed effects using `MouseListener` or `ButtonModel`.
- Add rounded corners and drop shadows.

### 2. Global Styling
- Customize `UIManager` defaults at application startup.
- Define a consistent color palette and typography.

### 3. Responsive Layouts
- Use `EmptyBorder` for consistent padding.
- Avoid fixed pixel sizes (`setPreferredSize`); use layout manager constraints instead.

## References
- [Java Swing Documentation](https://docs.oracle.com/javase/tutorial/uiswing/)
- [FlatLaf - Modern Open Source Look and Feel](https://www.formdev.com/flatlaf/)
